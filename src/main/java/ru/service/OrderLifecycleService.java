package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.configuration.authentication.AuthToken;
import ru.constant.OrderLifecycleActions;
import ru.constant.OrderStatus;
import ru.dao.entity.*;
import ru.dao.repository.*;
import ru.dto.json.order.OrderAcceptData;
import ru.dto.json.order.OrderAssignData;

import java.util.Collection;
import java.util.HashSet;

@Service
@Transactional
public class OrderLifecycleService {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final DriverRepository driverRepository;
    private final TransportRepository transportRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    private final PendingOrderRepository pendingOrderRepository;

    @Autowired
    public OrderLifecycleService(
            OrderRepository orderRepository,
            CompanyRepository companyRepository,
            DriverRepository driverRepository,
            TransportRepository transportRepository,
            OrderHistoryRepository orderHistoryRepository,
            PendingOrderRepository pendingOrderRepository)
    {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.driverRepository = driverRepository;
        this.transportRepository = transportRepository;
        this.orderHistoryRepository = orderHistoryRepository;
        this.pendingOrderRepository = pendingOrderRepository;
    }




    public void accept(Integer orderId, OrderAcceptData orderAcceptData) throws IllegalArgumentException, IllegalStateException{
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
            order.setCompany(company);
            order.setDriver(driver);
            order.setTransport(transport);
            if(orderAcceptData.getProposedPrice().equals(order.getDispatcherPrice())){
                order.setStatus(OrderStatus.ACCEPTED);
                order.setAssignedCompanies(new HashSet<>());
            } else {
                PendingOrder pendingOrder = pendingOrderRepository.findFirstByCompanyAndOrder(company,order).orElseThrow(()->new IllegalStateException("Заявка не может быть принята: \n- Другая компания уже приняла заявку \n- Ваша компания была исключена из претендентов на зявку"));
                pendingOrder.setProposedPrice(orderAcceptData.getProposedPrice());
                pendingOrderRepository.save(pendingOrder);
                order.setStatus(OrderStatus.PRICE_CHANGED);
            }
            orderRepository.save(order);

        }  else throw new IllegalStateException("Заявка не может быть принята: \n- Другая компания уже приняла заявку \n- Ваша компания была исключена из претендентов на зявку");

        User currentUser = ((AuthToken)SecurityContextHolder.getContext().getAuthentication()).getUser();
        Company currentCompany = currentUser.getCompany();
        OrderHistory assignAction = OrderHistory.builder()
                .action(OrderLifecycleActions.ACCEPTED)
                .orderStatus(order.getStatus())
                .company(currentCompany)
                .actionCompany(currentCompany.getName())
                .actionUser(currentUser.getUsername())
                .user(currentUser)
                .dispatcherPrice(order.getDispatcherPrice())
                .companyPrice(orderAcceptData.getProposedPrice())
                .order(order)
                .orderNumber(order.getNumber())
                .build();
        orderHistoryRepository.save(assignAction);
    }



    public void reject(Integer orderId, Integer companyId) throws IllegalArgumentException, IllegalStateException{
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(()->new IllegalArgumentException("Данный пользователь не привязан к компании"));
        if(order.getStatus().equals(OrderStatus.ASSIGNED)){
            order.getAssignedCompanies().remove(company);
            if (order.getAssignedCompanies().size()==0) order.setStatus(OrderStatus.REJECTED);
            orderRepository.save(order);
        } else throw new IllegalStateException("Заявка не может быть отклонена:\n- Другая компания приняла заявку.");

        User currentUser = ((AuthToken)SecurityContextHolder.getContext().getAuthentication()).getUser();
        Company currentCompany = currentUser.getCompany();
        OrderHistory assignAction = OrderHistory.builder()
                .action(OrderLifecycleActions.REJECT_ORDER)
                .company(currentCompany)
                .actionCompany(currentCompany.getName())
                .actionUser(currentUser.getUsername())
                .orderStatus(order.getStatus())
                .user(currentUser)
                .dispatcherPrice(order.getDispatcherPrice())
                .order(order)
                .orderNumber(order.getNumber())
                .build();
        orderHistoryRepository.save(assignAction);
    }



    public void assign(Integer orderId, OrderAssignData assignData) throws IllegalArgumentException, IllegalStateException{
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


        User currentUser = ((AuthToken)SecurityContextHolder.getContext().getAuthentication()).getUser();
        Company currentCompany = currentUser.getCompany();
        OrderHistory assignAction = OrderHistory.builder()
                .action(OrderLifecycleActions.ASSIGN)
                .company(currentCompany)
                .actionCompany(currentCompany.getName())
                .actionUser(currentUser.getUsername())
                .user(currentUser)
                .orderStatus(order.getStatus())
                .dispatcherPrice(order.getDispatcherPrice())
                .order(order)
                .orderNumber(order.getNumber())
                .build();
        orderHistoryRepository.save(assignAction);
    }




}
