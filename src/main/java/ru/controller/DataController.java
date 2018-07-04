package ru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dao.entity.Route;
import ru.dao.repository.RouteRepository;

@RestController
@RequestMapping("/data")
public class DataController {
    @Autowired
    private RouteRepository routeRepository;


    @GetMapping(value="/routes",produces = "application/json; charset=UTF-8")
    private Iterable<Route> getAllRoutes(){
        Iterable<Route> routes = routeRepository.findAll();
//        routes.forEach(x->{
//            Hibernate.initialize(x.getCompany());
//        });
        return routes;
    }
}
