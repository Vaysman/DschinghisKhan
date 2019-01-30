package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.constant.OrderLifecycleActions;
import ru.constant.OrderStatus;
import ru.constant.UserRole;
import ru.dao.entity.*;
import ru.dao.repository.*;
import ru.dto.json.order.OrderAcceptData;
import ru.dto.json.order.OrderAssignData;
import ru.util.generator.RandomIntGenerator;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * This service provides methods for every step of an order lifecycle
 */
@Service
@Transactional
public class OrderLifecycleService {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final DriverRepository driverRepository;
    private final TransportRepository transportRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderOfferRepository orderOfferRepository;
    private final MailService mailService;

    @Autowired
    public OrderLifecycleService(
            OrderRepository orderRepository,
            CompanyRepository companyRepository,
            DriverRepository driverRepository,
            TransportRepository transportRepository,
            OrderHistoryRepository orderHistoryRepository,
            OrderOfferRepository orderOfferRepository, MailService mailService) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.driverRepository = driverRepository;
        this.transportRepository = transportRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderOfferRepository = orderOfferRepository;
        this.mailService = mailService;
    }


    /**
     * @param orderId ID of an order to be duplicated
     * @return Brand new order, containing the same:
     * {@link Route},
     * {@link ru.constant.OrderObligation},
     * documentReturn date, paymentDate, paymentType,
     * routePrice, originator, afterLoad, requirements and rating
     */
    public Order duplicateOrder(Integer orderId){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заявки не существует"));
        Order newOrder = Order.builder()
                .number("LSS-"+RandomIntGenerator.randomAlphaNumeric(8).toString())
                .route(order.getRoute())
                .orderObligation(order.getOrderObligation())
                .documentReturnDate(order.getDocumentReturnDate())
                .paymentDate(order.getPaymentDate())
                .paymentType(order.getPaymentType())
                .routePrice(order.getRoutePrice())
                .originator(order.getOriginator())
                .afterLoad(order.getAfterLoad())
                .status(OrderStatus.CREATED)
                .requirements(order.getRequirements())
                .rating(order.getRating())
                .build();
        orderRepository.save(newOrder);

        return newOrder;

    }

    /**
     * @param currentUser User that attempts to confirm payment
     * @param orderId ID of an order, status of which will be changed from one of delivered statues to PAYMENT_CONFD
     */
    public void confirmPayment(User currentUser, Integer orderId) {
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, OrderStatus.getDeliveredStatuses())
                .orElseThrow(() -> new IllegalArgumentException("Не может быть изменен статус заявки:\n– Оплата уже была подтверждена\n– Доставка еще не была доставлена\n- Заявки не существует"));
        order.setStatus(OrderStatus.PAYMENT_CONFD);
        saveOrder(order);

        saveActionHistory(OrderLifecycleActions.PAYMENT_RECEIVED, currentUser, order);
    }

    /**
     * @param currentUser User that attempts to claim that payment for order wasn't received
     * @param orderId ID of an order, status of which will be changed from PAYED to NOT_PAYED
     */
    public void claimNonPayment(User currentUser, Integer orderId){
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, new OrderStatus[]{OrderStatus.PAYED})
                .orElseThrow(() -> new IllegalArgumentException("Невозможно заявить о неоплате:\n- О неоплате заявил другой пользователь\n- Пользователь ТК заявил о получении оплаты\n- Заявки не существует"));
        order.setStatus(OrderStatus.NOT_PAYED);
        saveOrder(order);
        saveActionHistory(OrderLifecycleActions.PAYMENT_REJECTED,currentUser,order);
    }

    /**
     * @param currentUser User that attempts to claim
     *                    that payment for order was sent
     * @param orderId ID of an order, status of which will be changed
     *                from PAY_PENDING to PAYED
     */
    public void claimPayment(User currentUser, Integer orderId){
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, new OrderStatus[]{OrderStatus.PAY_PENDING})
                .orElseThrow(() -> new IllegalArgumentException("Невозможно заявить о оплате:\n- О оплате заявил другой диспетчер\n- Пользователь ТК заявил о получении оплаты\n- Заявки не существует"));
        order.setStatus(OrderStatus.PAYED);
        saveOrder(order);
        saveActionHistory(OrderLifecycleActions.PAYMENT_CONFIRMED,currentUser,order);
    }

    /**
     * @param currentUser User that attempts to claim that
     *                    documents for order were delivered
     * @param orderId ID of an order, status of which will be changed
     *                from DOCUMENT_RETURN/DELIVERY_CONFDS to DOCS_RECEIVED
     */
    public void confirmDocumentDelivery(User currentUser, Integer orderId){
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, new OrderStatus[]{OrderStatus.DOCUMENT_RETURN, OrderStatus.DELIVERY_CONFD})
                .orElseThrow(() -> new IllegalArgumentException("Невозможно подтвердить получение документов:\n- Получение документов подтвердил другой диспетчер\n- Пользователь ТК заявил о получении оплаты\n- Заявки не существует"));
        order.setStatus(OrderStatus.DOCS_RECEIVED);
        saveOrder(order);
        saveActionHistory(OrderLifecycleActions.DOCUMENT_DELIVERED,currentUser,order);
    }


    /**
     * It's not meant to be called outside the service,
     * but {@link Scheduled} annotation
     * requires it to be public
     */
    @Scheduled(fixedRate = 3600000)
    public void changeStatusToPaymentReception() {
        try {
            List<Order> ordersReadyForPaymentReception = orderRepository.getOrdersForPaymentReception();
            for (Order order : ordersReadyForPaymentReception) {
                changeStatus(order, OrderStatus.PAY_PENDING);
            }
        } catch (Exception e) {
            System.out.println("Unable to change scheduled order status:\n" + e.getMessage());
        }
    }

    /**
     * It's not meant to be called outside the service,
     * but {@link Scheduled} annotation
     * requires it to be public
     */
    @Scheduled(fixedRate = 3600000)
    public void findAndChangeOrdersToDocumentReturn() {
        try {
            List<Order> ordersReadyForDocumentReturn = orderRepository.getOrdersForDocumentReturn();
            for (Order order : ordersReadyForDocumentReturn) {
                changeStatus(order, OrderStatus.DOCUMENT_RETURN);
            }
        } catch (Exception e) {
            System.out.println("Unable to change scheduled order status:\n" + e.getMessage());
        }
    }

    /**
     * It's not meant to be called outside the service,
     * but {@link Scheduled} annotation
     * requires it to be public
     */
    @Scheduled(fixedRate = 300000)
    public void clearUnfinishedOffers(){
        try{
            orderOfferRepository.getOutdatedOffers()
                    .forEach(x->{
                        this.notifyCompanyOfDecline(x);
                        this.declineOffer(x);
                    });
        } catch (Exception e){
            System.out.println("Unable to clear unfinished offers:\n"+e.getMessage());
        }
    }

    /**
     * Notifies company of a declined offer
     *
     * @param offer offer that is to be declined
     */
    @Transactional
    void notifyCompanyOfDecline(OrderOffer offer){
        try {
            mailService.send(
                    offer.getCompany().getEmail(),
                    "Предложение отклонено",
                    "Вы не заполнили данные в указанный срок для заявки " +
                            offer.getOrderNumber() +
                            " компании " + offer.getManagerCompany().getName() +
                            " по маршруту " + offer.getOrder().getRoute().getName() +
                            ".\nВаше предложение было отклонено автоматически."
                    );
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param currentUser User that attempts to claim that
     *                    order was delivered.
     * @param orderId ID of an order, status of which will be changed
     *                from one of interchangeable statuses to DELIVERED.
     *                If user has role ROLE_DISPATCHER - it will be changed to DELIVERY_CONFDS
     */
    public void confirmDelivery(User currentUser, Integer orderId) {
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, OrderStatus.getChangeableStatuses())
                .orElseThrow(() -> new IllegalArgumentException("Не может быть изменен статус заявки:\n– Доставка была подтверждена\n- Заявки не существует"));
        if (currentUser.getUserRole().equals(UserRole.ROLE_TRANSPORT_COMPANY)) {
            order.setStatus(OrderStatus.DELIVERED);
        } else {
            order.setStatus(OrderStatus.DELIVERY_CONFD);
        }
        saveOrder(order);

        saveActionHistory(OrderLifecycleActions.CONFIRM_DELIVERY, currentUser, order);
    }

    /**
     * @param currentUser User that attempts to change order's status
     * @param orderId ID of an order, status of which will be changed
     *                from one of interchangeable statuses to the one provided
     * @param orderStatus One of interchangeable statuses
     */
    public void changeStatus(User currentUser, Integer orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, OrderStatus.getChangeableStatuses()).orElseThrow(() -> new IllegalArgumentException("Не может быть изменен статус заявки:\n– Доставка была подтверждена\n- Заявки не существует"));
        order.setStatus(orderStatus);
        saveOrder(order);

        saveActionHistory(OrderLifecycleActions.CHANGE_STATUS, currentUser, order);
    }


    /**
     * A method to change the status of an order without user
     * Isn't meant to be public, really
     *
     * @param order Order, status of which is to change
     * @param orderStatus Status which will be assigned to the provided order
     */
    private void changeStatus(Order order, OrderStatus orderStatus) {
        order.setStatus(orderStatus);
        saveOrder(order);
        saveActionHistory(OrderLifecycleActions.STATUS_TRANSIT, order);
    }


    /**
     * Declines an offer on an order. If there're no more offers on the order present,
     * order's status will be changed to CREATED.
     *
     * This method is meant to be used by scheduled methods, without user's input
     *
     * @param offer offer which is to be declined
     */
    private void declineOffer(OrderOffer offer){
        Order order = offer.getOrder();

        if (Arrays.stream(OrderStatus.getStatusesWithOffers()).noneMatch(orderStatus -> orderStatus.equals(order.getStatus())))
            throw new IllegalArgumentException("Предложение не может быть отклонено: \nДругой диспетчер утвердил/отклонил заявку");

        order.getOffers().remove(offer);
        checkForMoreOffers(order);
        saveOrder(order);

        orderOfferRepository.delete(offer);

        saveActionHistory(OrderLifecycleActions.DECLINE_OFFER, order);
    }


    /**
     * Declines an offer on an order. If there're no more offers on the order present,
     * order's status will be changed to CREATED.
     *
     * @param currentUser User that attempts to decline the offer
     * @param offerId offerId which is to be declined
     */
    public void declineOffer(User currentUser, Integer offerId) {
        OrderOffer offer = orderOfferRepository.findById(offerId).orElseThrow(() -> new IllegalArgumentException("Предложение не может быть отклонено: \nДругой диспетчер утвердил/отклонил заявку"));
        Order order = offer.getOrder();
        if (Arrays.stream(OrderStatus.getStatusesWithOffers()).noneMatch(orderStatus -> orderStatus.equals(order.getStatus())))
            throw new IllegalArgumentException("Предложение не может быть отклонено: \nДругой диспетчер утвердил/отклонил заявку");

        order.getOffers().remove(offer);
        checkForMoreOffers(order);
        saveOrder(order);

        orderOfferRepository.delete(offer);

        saveActionHistory(OrderLifecycleActions.DECLINE_OFFER, currentUser, order);
    }

    private void checkForMoreOffers(Order order) {
        if (order.getOffers().size() == 0) {
            if (order.getAssignedCompanies().size() == 0) {
                order.setStatus(OrderStatus.CREATED);
            } else {
                order.setStatus(OrderStatus.ASSIGNED);
            }
            order.setProposedPrice(null);
        }
    }

    public void confirm(User currentUser, Integer offerId) {
        OrderOffer orderOffer = orderOfferRepository.findById(offerId).orElseThrow(() -> new IllegalArgumentException("Заявка не может быть утверждена: \nДругой диспетчер утвердил/отклонил заявку"));
        Order order = orderOffer.getOrder();
        if (Arrays.stream(OrderStatus.getStatusesWithOffers()).noneMatch(orderStatus -> orderStatus.equals(order.getStatus())))
            throw new IllegalArgumentException("Заявка не может быть утверждена: \nДругой диспетчер утвердил/отклонил заявку");

        order.setCompany(orderOffer.getCompany());
        order.setDriver(orderOffer.getDriver());
        order.setTransport(orderOffer.getTransport());
        order.setProposedPrice(orderOffer.getProposedPrice());
        order.setAdditionalDrivers(orderOffer.getAdditionalDrivers());
        order.setAdditionalTransports(orderOffer.getAdditionalTransports());
        order.setOffers(new HashSet<>());
        if (order.getStatus().equals(OrderStatus.PRICE_CHANGED)) order.setAssignedCompanies(new HashSet<>());
        order.setStatus(OrderStatus.CONFIRMED);
        if(orderOffer.getDriver()!=null){
            if (orderOffer.getDriver().getIsNew()){
                orderOffer.getDriver().setIsNew(false);
                driverRepository.save(orderOffer.getDriver());
            }
        }

        saveOrder(order);

        orderOfferRepository.delete(orderOffer);

        saveActionHistory(OrderLifecycleActions.CONFIRMED, currentUser, order);
    }

    public void accept(Integer orderId, User currentUser, OrderAcceptData orderAcceptData) throws IllegalArgumentException, IllegalStateException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        Company company = companyRepository.findById(orderAcceptData.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Данный пользователь не привязан к компании"));
        Driver driver = null;
        if(Optional.ofNullable(orderAcceptData.getDriverId()).isPresent()){
            driver = driverRepository.findById(orderAcceptData.getDriverId()).orElseThrow(()->new IllegalArgumentException("Данного водителя не существует"));
        }

        Transport transport = null;
        if(Optional.ofNullable(orderAcceptData.getTransportId()).isPresent()){
            transport = transportRepository.findById(orderAcceptData.getTransportId()).orElseThrow(()->new IllegalArgumentException("Данного транспорта не существует"));
        }

        Set<Transport> additionalTransports = new HashSet<>();
        Set<Driver> additionalDrivers = new HashSet<>();
        if(!orderAcceptData.getAdditionalDrivers().isEmpty()){
            driverRepository.findAllById(orderAcceptData.getAdditionalDrivers()).forEach(additionalDrivers::add);
        }
        if(!orderAcceptData.getAdditionalTransports().isEmpty()){
            transportRepository.findAllById(orderAcceptData.getAdditionalTransports()).forEach(additionalTransports::add);
        }


        if ((order.getStatus().equals(OrderStatus.ASSIGNED) || order.getStatus().equals(OrderStatus.PRICE_CHANGED))
                && order.getAssignedCompanies().contains(company)) {

            Company managerCompany = companyRepository.findById(order.getOriginator()).orElse(null);

            OrderOffer orderOffer = OrderOffer
                    .builder()
                    .order(order)
                    .company(company)
                    .proposedPrice(orderAcceptData.getProposedPrice())
                    .dispatcherPrice(order.getDispatcherPrice())
                    .proposedPriceComment(orderAcceptData.getProposedPriceComment())
                    .orderNumber(order.getNumber())
                    .driver(driver)
                    .additionalDrivers(additionalDrivers)
                    .transport(transport)
                    .additionalTransports(additionalTransports)
                    .managerCompany(managerCompany)
                    .offerDatetime(LocalDateTime.now(ZoneId.of("Europe/Moscow")))
                    .build();
            orderOfferRepository.save(orderOffer);

            if (orderAcceptData.getProposedPrice().equals(order.getDispatcherPrice())) {
                order.setAssignedCompanies(new HashSet<>());
                order.setOffers(new HashSet<>(Collections.singletonList(orderOffer)));
                order.setStatus(OrderStatus.ACCEPTED);
            } else {
                order.getOffers().add(orderOffer);
                order.getAssignedCompanies().remove(company);
                order.setStatus(OrderStatus.PRICE_CHANGED);
            }

            saveOrder(order);

        } else
            throw new IllegalStateException("Заявка не может быть принята: \n- Другая компания уже приняла заявку \n- Ваша компания была исключена из претендентов на зявку");


        saveActionHistory(OrderLifecycleActions.ACCEPTED, currentUser, order, orderAcceptData.getProposedPrice());
    }

    public void reject(Integer orderId, User currentUser, Integer companyId) throws IllegalArgumentException, IllegalStateException {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Данный пользователь не привязан к компании"));
        if (order.getStatus().equals(OrderStatus.ASSIGNED)) {
            order.getAssignedCompanies().remove(company);
            if (order.getAssignedCompanies().size() == 0) order.setStatus(OrderStatus.REJECTED);
            saveOrder(order);
        } else throw new IllegalStateException("Заявка не может быть отклонена:\n- Другая компания приняла заявку.");


        saveActionHistory(OrderLifecycleActions.REJECT_ORDER, currentUser, order);
    }


    public void assign(Integer orderId, User currentUser, OrderAssignData assignData) throws IllegalArgumentException, IllegalStateException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        if (!order.getStatus().equals(OrderStatus.CREATED) && !order.getStatus().equals(OrderStatus.REJECTED)) {
            throw new IllegalStateException("Заявка не может быть переназначена:" +
                    "\n- Другой диспетчер уже назначил компании");
        }

        Iterable<Company> companies = companyRepository.findAllById(assignData.getAssignedCompanies());
        if (companies.spliterator().getExactSizeIfKnown() == 0) {
            throw new IllegalArgumentException("Ошибка: Не указаны компании");
        }

        order.setAssignedCompanies(new HashSet<>((Collection<Company>) companies));
        order.setStatus(OrderStatus.ASSIGNED);
        order.setDispatcherPrice(assignData.getDispatcherPrice());
        saveOrder(order);

        saveActionHistory(OrderLifecycleActions.ASSIGN, currentUser, order);
    }

    private void saveOrder(Order order) {
        order.setStatusChangeDate(new Date());
        orderRepository.save(order);
    }

    private void saveActionHistory(OrderLifecycleActions action, Order order) {
        OrderHistory assignAction = OrderHistory.builder()
                .action(action)
                .actionUser("Система")
                .orderStatus(order.getStatus())
                .dispatcherPrice(order.getDispatcherPrice())
                .orderId(order.getId())
                .companyPrice(order.getProposedPrice())
                .orderNumber(order.getNumber())
                .date(new Date())
                .build();
        orderHistoryRepository.save(assignAction);
    }

    private void saveActionHistory(OrderLifecycleActions action, User user, Order order) {
        saveActionHistory(action, user, order, order.getProposedPrice());
    }

    private void saveActionHistory(OrderLifecycleActions action, User user, Order order, Float proposedPrice) {
        OrderHistory assignAction = OrderHistory.builder()
                .action(action)
                .company(user.getCompany())
                .actionCompany(user.getCompany().getName())
                .actionUser(user.getUsername())
                .user(user)
                .orderStatus(order.getStatus())
                .dispatcherPrice(order.getDispatcherPrice())
                .orderId(order.getId())
                .companyPrice(proposedPrice)
                .orderNumber(order.getNumber())
                .date(new Date())
                .build();
        orderHistoryRepository.save(assignAction);
    }


}
