package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.constant.OrderLifecycleActions;
import ru.constant.OrderStatus;
import ru.dao.entity.*;
import ru.dao.repository.*;
import ru.dto.json.order.OrderAcceptData;
import ru.dto.json.order.OrderAssignData;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

@Service
@Transactional
public class OrderLifecycleService {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final DriverRepository driverRepository;
    private final TransportRepository transportRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final OrderOfferRepository orderOfferRepository;

    @Autowired
    public OrderLifecycleService(
            OrderRepository orderRepository,
            CompanyRepository companyRepository,
            DriverRepository driverRepository,
            TransportRepository transportRepository,
            OrderHistoryRepository orderHistoryRepository,
            OrderOfferRepository orderOfferRepository)
    {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.driverRepository = driverRepository;
        this.transportRepository = transportRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderOfferRepository = orderOfferRepository;
    }



    public void confirm(Integer orderId, User currentUser, Integer offerId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        if(!order.getStatus().equals(OrderStatus.ACCEPTED) && !order.getStatus().equals(OrderStatus.PRICE_CHANGED)) throw new IllegalArgumentException("Заявка не может быть утверждена: \nДругой диспетчер утвердил заявку");
        OrderOffer orderOffer = orderOfferRepository.findById(offerId).orElseThrow(() -> new IllegalArgumentException("Заявка не может быть утверждена: \nДругой диспетчер утвердил заявку"));

        order.setCompany(orderOffer.getCompany());
        order.setDriver(orderOffer.getDriver());
        order.setTransport(orderOffer.getTransport());
        order.setCompanyPrice(orderOffer.getProposedPrice());
        order.setOffers(new HashSet<>());
        if(order.getStatus().equals(OrderStatus.PRICE_CHANGED)) order.setAssignedCompanies(new HashSet<>());
        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order);

        saveActionHistory(OrderLifecycleActions.CONFIRMED,currentUser,order);
    }

    public void accept(Integer orderId, User currentUser, OrderAcceptData orderAcceptData) throws IllegalArgumentException, IllegalStateException{
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        Company company = companyRepository.findById(orderAcceptData.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Данный пользователь не привязан к компании"));
        Driver driver =driverRepository.findById(orderAcceptData.getDriverId())
                .orElseThrow(() -> new IllegalArgumentException("Не указан водитель"));
        Transport transport = transportRepository.findById(orderAcceptData.getTransportId())
                .orElseThrow(() -> new IllegalArgumentException("Не указан транспорт"));


        if((order.getStatus().equals(OrderStatus.ASSIGNED)||order.getStatus().equals(OrderStatus.PRICE_CHANGED))
                && order.getAssignedCompanies().contains(company)){

            OrderOffer orderOffer = OrderOffer
                    .builder()
                    .order(order)
                    .company(company)
                    .proposedPrice(orderAcceptData.getProposedPrice())
                    .driver(driver)
                    .transport(transport)
                    .build();
            orderOfferRepository.save(orderOffer);

            if(orderAcceptData.getProposedPrice().equals(order.getDispatcherPrice())){
                order.setAssignedCompanies(new HashSet<>());
                order.setOffers(new HashSet<>(Collections.singletonList(orderOffer)));
                order.setStatus(OrderStatus.ACCEPTED);
            } else {
                order.getOffers().add(orderOffer);
                order.getAssignedCompanies().remove(company);
                order.setStatus(OrderStatus.PRICE_CHANGED);
            }

            orderRepository.save(order);

        }  else throw new IllegalStateException("Заявка не может быть принята: \n- Другая компания уже приняла заявку \n- Ваша компания была исключена из претендентов на зявку");



        saveActionHistory(OrderLifecycleActions.ACCEPTED,currentUser,order,orderAcceptData.getProposedPrice());
    }



    public void reject(Integer orderId, User currentUser, Integer companyId) throws IllegalArgumentException, IllegalStateException{
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(()->new IllegalArgumentException("Данный пользователь не привязан к компании"));
        if(order.getStatus().equals(OrderStatus.ASSIGNED)){
            order.getAssignedCompanies().remove(company);
            if (order.getAssignedCompanies().size()==0) order.setStatus(OrderStatus.REJECTED);
            orderRepository.save(order);
        } else throw new IllegalStateException("Заявка не может быть отклонена:\n- Другая компания приняла заявку.");


        saveActionHistory(OrderLifecycleActions.REJECT_ORDER, currentUser,order);
    }



    public void assign(Integer orderId, User currentUser, OrderAssignData assignData) throws IllegalArgumentException, IllegalStateException{
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        if(!order.getStatus().equals(OrderStatus.CREATED) && !order.getStatus().equals(OrderStatus.REJECTED)){
            throw new IllegalStateException("Заявка не может быть переназначена:" +
                    "\n- Другой диспетчер уже назначил компании");
        }

        Iterable<Company> companies = companyRepository.findAllById(assignData.getAssignedCompanies());
        if (companies.spliterator().getExactSizeIfKnown()==0) {
            throw new IllegalArgumentException("Ошибка: Не указаны компании");
        }

        order.setAssignedCompanies(new HashSet<>((Collection<Company>) companies));
        order.setStatus(OrderStatus.ASSIGNED);
        order.setDispatcherPrice(assignData.getDispatcherPrice());
        orderRepository.save(order);

        saveActionHistory(OrderLifecycleActions.ASSIGN,currentUser,order);
    }

    private void saveActionHistory(OrderLifecycleActions action, User user, Order order){
        saveActionHistory(action,user,order,order.getCompanyPrice());
    }

    private void saveActionHistory(OrderLifecycleActions action, User user, Order order, Float proposedPrice){
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
