package ru.controller;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dao.entity.Order;
import ru.dao.entity.Route;
import ru.dao.repository.OrderRepository;
import ru.dao.repository.RouteRepository;

@RestController
@RequestMapping("/data")
public class DataController {
    private final RouteRepository routeRepository;

    private final OrderRepository orderRepository;

    @Autowired
    public DataController(OrderRepository orderRepository, RouteRepository routeRepository) {
        this.orderRepository = orderRepository;
        this.routeRepository = routeRepository;
    }


    @GetMapping(value="/orders/{orderId}",produces = "application/json; charset=UTF-8")
    private Order getFullOrder(@PathVariable Integer orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        Hibernate.initialize(order.getDropPoints());
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
