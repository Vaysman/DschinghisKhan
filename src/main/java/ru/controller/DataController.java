package ru.controller;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.constant.ContactType;
import ru.dao.entity.*;
import ru.dao.repository.*;

import java.util.HashMap;
import java.util.Map;

@RestController
//@PreAuthorize("isAuthenticated()")
@RequestMapping("/data")
public class DataController {
    private final RouteRepository routeRepository;
    private final CompanyRepository companyRepository;
    private final OrderRepository orderRepository;
    private final OrderOfferRepository offerRepository;
    private final ContactRepository contactRepository;

    @Autowired
    public DataController(OrderRepository orderRepository, RouteRepository routeRepository, CompanyRepository companyRepository, OrderOfferRepository offerRepository, ContactRepository contactRepository) {
        this.orderRepository = orderRepository;
        this.routeRepository = routeRepository;
        this.companyRepository = companyRepository;
        this.offerRepository = offerRepository;
        this.contactRepository = contactRepository;
    }

    @GetMapping(value = "/offers/{offerId}", produces = "application/json; charset=UTF-8")
    private OrderOffer getOfferInfo(@PathVariable Integer offerId){
        OrderOffer offer = offerRepository.findById(offerId).orElseThrow(()->new IllegalArgumentException("Данный оффер не найден"));
        Hibernate.initialize(offer.getOrder());
        Hibernate.initialize(offer.getCompany());
        Hibernate.initialize(offer.getDriver());
        Hibernate.initialize(offer.getOrder().getRoute().getRoutePoints());
        Hibernate.initialize(offer.getTransport());
        for(RoutePoint routePoint : offer.getOrder().getRoute().getRoutePoints()){
            Hibernate.initialize(routePoint.getPoint());
        }
        return offer;
    }

    @GetMapping(value="/orders/{orderId}",produces = "application/json; charset=UTF-8")
    private Map<String, Object> getFullOrder(@PathVariable Integer orderId){
        Map<String, Object> map = new HashMap<>();
        Order order = orderRepository.findById(orderId).orElse(null);
        Hibernate.initialize(order.getRoute());
        Hibernate.initialize(order.getRoute().getRoutePoints());
        for(RoutePoint routePoint : order.getRoute().getRoutePoints()){
            Hibernate.initialize(routePoint.getPoint());
        }
        Hibernate.initialize(order.getCompany());
        Hibernate.initialize(order.getDriver());
        Hibernate.initialize(order.getTransport());
        Contact companyContact = contactRepository.findFirstByCompanyAndType(order.getCompany(), ContactType.PRIMARY).orElse(null);
        Company dispatcherCompany = companyRepository.findById(order.getOriginator()).orElseThrow(()->new IllegalStateException("Order does not have a dispatcher company"));
        Contact dispatcherContact = contactRepository.findFirstByCompanyAndType(dispatcherCompany, ContactType.PRIMARY).orElse(null);
        Hibernate.initialize(order.getCompany().getPoint());
        Hibernate.initialize(dispatcherCompany.getPoint());

        map.put("order", order);
        map.put("companyContact", companyContact);
        map.put("dispatcherCompany", dispatcherCompany);
        map.put("dispatcherContact", dispatcherContact);

        return map;
    }

    @GetMapping(value = "/companies/{companyId}", produces = "application/json; charset=UTF-8")
    private Company getFullCompany(@PathVariable Integer companyId){
        Company company = companyRepository.findById(companyId).orElse(null);
        Hibernate.initialize(company.getPoint());
        return company;
    }

    @GetMapping(value="/routes",produces = "application/json; charset=UTF-8")
    private Iterable<Route> getAllRoutes(){
        Iterable<Route> routes = routeRepository.findAll();
        return routes;
    }
}
