package ru.controller;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dao.entity.Order;
import ru.dao.entity.OrderOffer;
import ru.dao.entity.Route;
import ru.dao.repository.OrderOfferRepository;
import ru.dao.repository.OrderRepository;
import ru.dao.repository.RouteRepository;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/data")
public class DataController {
    private final RouteRepository routeRepository;

    private final OrderRepository orderRepository;
    private final OrderOfferRepository offerRepository;

    @Autowired
    public DataController(OrderRepository orderRepository, RouteRepository routeRepository, OrderOfferRepository offerRepository) {
        this.orderRepository = orderRepository;
        this.routeRepository = routeRepository;
        this.offerRepository = offerRepository;
    }

    @GetMapping(value = "/offers/{offerId}", produces = "application/json; charset=UTF-8")
    private OrderOffer getOfferInfo(@PathVariable Integer offerId){
        OrderOffer offer = offerRepository.findById(offerId).orElseThrow(()->new IllegalArgumentException("Данный оффер не найден"));
        Hibernate.initialize(offer.getOrder());
        Hibernate.initialize(offer.getCompany());
        Hibernate.initialize(offer.getDriver());
        Hibernate.initialize(offer.getOrder().getRoute().getRoutePoints());
        Hibernate.initialize(offer.getTransport());
        return offer;
    }

    @GetMapping(value="/orders/{orderId}",produces = "application/json; charset=UTF-8")
    private Order getFullOrder(@PathVariable Integer orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        Hibernate.initialize(order.getRoute());
        Hibernate.initialize(order.getRoute().getRoutePoints());
        return order;
    }

    @GetMapping(value="/routes",produces = "application/json; charset=UTF-8")
    private Iterable<Route> getAllRoutes(){
        Iterable<Route> routes = routeRepository.findAll();
        return routes;
    }
}
