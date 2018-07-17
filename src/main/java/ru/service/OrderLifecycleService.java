package ru.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.constant.OrderStatus;
import ru.dao.entity.Company;
import ru.dao.entity.Driver;
import ru.dao.entity.Order;
import ru.dao.entity.Transport;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.DriverRepository;
import ru.dao.repository.OrderRepository;
import ru.dao.repository.TransportRepository;
import ru.dto.json.order.OrderAcceptData;

import java.util.HashSet;

@Service
public class OrderLifecycleService {
    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final DriverRepository driverRepository;
    private final TransportRepository transportRepository;

    @Autowired
    public OrderLifecycleService(
            OrderRepository orderRepository,
            CompanyRepository companyRepository,
            DriverRepository driverRepository,
            TransportRepository transportRepository) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.driverRepository = driverRepository;
        this.transportRepository = transportRepository;
    }

    public void acceptOrder (Integer orderId, OrderAcceptData orderAcceptData) throws IllegalArgumentException, IllegalStateException{
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        Company company = companyRepository.findById(orderAcceptData.getCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Данный пользователь не привязан к компании"));
        Driver driver =driverRepository.findById(orderAcceptData.getDriverId())
                .orElseThrow(() -> new IllegalArgumentException("Не указан водитель"));
        Transport transport = transportRepository.findById(orderAcceptData.getTransportId())
                .orElseThrow(() -> new IllegalArgumentException("Не указан транспорт"));

        if(order.getStatus().equals(OrderStatus.ASSIGNED) && order.getAssignedCompanies().contains(company)){
            order.setCompany(company);
            order.setDriver(driver);
            order.setTransport(transport);
            order.setProposedPrice(orderAcceptData.getProposedPrice());
            order.setAssignedCompanies(new HashSet<>());
            order.setStatus(OrderStatus.ACCEPTED);
            orderRepository.save(order);
        }  else throw new IllegalStateException("Заявка не может быть принята: \n- Другая компания уже приняла заявку \n- Ваша компания была исключена из претендентов на зявку");
    }

    public void rejectOrder(Integer orderId, Integer companyId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Данной заявки не существует"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(()->new IllegalArgumentException("Данный пользователь не привязан к компании"));
        if(order.getStatus().equals(OrderStatus.ASSIGNED)){
            order.getAssignedCompanies().remove(company);
            if (order.getAssignedCompanies().size()==0) order.setStatus(OrderStatus.REJECTED);
            orderRepository.save(order);
        } else throw new IllegalStateException("Заявка не может быть отклонена:\n- Другая компания приняла заявку.");
    }



}
