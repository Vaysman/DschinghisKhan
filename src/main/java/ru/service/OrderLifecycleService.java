package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional
public class OrderLifecycleService {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final DriverRepository driverRepository;
    private final TransportRepository transportRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderOfferRepository orderOfferRepository;
    private final JavaMailSender sender;

    @Autowired
    public OrderLifecycleService(
            OrderRepository orderRepository,
            CompanyRepository companyRepository,
            DriverRepository driverRepository,
            TransportRepository transportRepository,
            OrderHistoryRepository orderHistoryRepository,
            OrderOfferRepository orderOfferRepository, JavaMailSender sender) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.driverRepository = driverRepository;
        this.transportRepository = transportRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderOfferRepository = orderOfferRepository;
        this.sender = sender;
    }

    public void confirmPayment(User currentUser, Integer orderId) {
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, OrderStatus.getDeliveredStatuses())
                .orElseThrow(() -> new IllegalArgumentException("Не может быть изменен статус заявки:\n– Оплата уже была подтверждена\n– Доставка еще не была доставлена\n- Заявки не существует"));
        order.setStatus(OrderStatus.PAYMENT_CONFD);
        saveOrder(order);

        saveActionHistory(OrderLifecycleActions.PAYMENT_RECEIVED, currentUser, order);
    }

    public void claimNonPayment(User currentUser, Integer orderId){
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, new OrderStatus[]{OrderStatus.PAYED})
                .orElseThrow(() -> new IllegalArgumentException("Невозможно заявить о неоплате:\n- О неоплате заявил другой пользователь\n- Пользователь ТК заявил о получении оплаты\n- Заявки не существует"));
        order.setStatus(OrderStatus.NOT_PAYED);
        saveOrder(order);
        saveActionHistory(OrderLifecycleActions.PAYMENT_REJECTED,currentUser,order);
    }

    public void claimPayment(User currentUser, Integer orderId){
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, new OrderStatus[]{OrderStatus.PAY_PENDING})
                .orElseThrow(() -> new IllegalArgumentException("Невозможно заявить о оплате:\n- О оплате заявил другой диспетчер\n- Пользователь ТК заявил о получении оплаты\n- Заявки не существует"));
        order.setStatus(OrderStatus.PAYED);
        saveOrder(order);
        saveActionHistory(OrderLifecycleActions.PAYMENT_CONFIRMED,currentUser,order);
    }

    public void confirmDocumentDelivery(User currentUser, Integer orderId){
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, new OrderStatus[]{OrderStatus.DOCUMENT_RETURN})
                .orElseThrow(() -> new IllegalArgumentException("Невозможно подтвердить получение документов:\n- Получение документов подтвердил другой диспетчер\n- Пользователь ТК заявил о получении оплаты\n- Заявки не существует"));
        order.setStatus(OrderStatus.DOCS_RECEIVED);
        saveOrder(order);
        saveActionHistory(OrderLifecycleActions.DOCUMENT_DELIVERED,currentUser,order);
    }



    //Is not meant to be called from outside, but doesn't schedule if private
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

    //Is not meant to be called from outside, but doesn't schedule if private
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

    @Transactional
    void notifyCompanyOfDecline(OrderOffer offer){
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setFrom("tarificationsquad@gmail.com");
            helper.setTo(offer.getCompany().getEmail());
            helper.setText("Вы не заполнили данные в указанный срок для заявки " +
                    offer.getOrderNumber() +
                    " компании " + offer.getManagerCompany().getName() +
                    " по маршруту " + offer.getOrder().getRoute().getName() +
            ".\nВаше предложение было отклонено автоматически.");
            helper.setSubject("Предложение отклонено");
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

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

    public void changeStatus(User currentUser, Integer orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findFirstByIdAndStatusIn(orderId, OrderStatus.getChangeableStatuses()).orElseThrow(() -> new IllegalArgumentException("Не может быть изменен статус заявки:\n– Доставка была подтверждена\n- Заявки не существует"));
        order.setStatus(orderStatus);
        saveOrder(order);

        saveActionHistory(OrderLifecycleActions.CHANGE_STATUS, currentUser, order);
    }

    //For changing status without user
    public void changeStatus(Order order, OrderStatus orderStatus) {
        order.setStatus(orderStatus);
        saveOrder(order);
        saveActionHistory(OrderLifecycleActions.STATUS_TRANSIT, order);
    }


    private void declineOffer(OrderOffer offer){
        Order order = offer.getOrder();

        if (!order.getStatus().equals(OrderStatus.ACCEPTED) && !order.getStatus().equals(OrderStatus.PRICE_CHANGED))
            throw new IllegalArgumentException("Предложение не может быть отклонено: \nДругой диспетчер утвердил/отклонил заявку");

        order.getOffers().remove(offer);
        checkForMoreOffers(order);
        saveOrder(order);

        orderOfferRepository.delete(offer);

        saveActionHistory(OrderLifecycleActions.DECLINE_OFFER, order);
    }



    public void declineOffer(User currentUser, Integer offerId) {
        OrderOffer offer = orderOfferRepository.findById(offerId).orElseThrow(() -> new IllegalArgumentException("Предложение не может быть отклонено: \nДругой диспетчер утвердил/отклонил заявку"));
        Order order = offer.getOrder();
        if (!order.getStatus().equals(OrderStatus.ACCEPTED) && !order.getStatus().equals(OrderStatus.PRICE_CHANGED))
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
        if (!order.getStatus().equals(OrderStatus.ACCEPTED) && !order.getStatus().equals(OrderStatus.PRICE_CHANGED))
            throw new IllegalArgumentException("Заявка не может быть утверждена: \nДругой диспетчер утвердил/отклонил заявку");

        order.setCompany(orderOffer.getCompany());
        order.setDriver(orderOffer.getDriver());
        order.setTransport(orderOffer.getTransport());
        order.setProposedPrice(orderOffer.getProposedPrice());
        order.setOffers(new HashSet<>());
        if (order.getStatus().equals(OrderStatus.PRICE_CHANGED)) order.setAssignedCompanies(new HashSet<>());
        order.setStatus(OrderStatus.CONFIRMED);

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
                    .transport(transport)
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
