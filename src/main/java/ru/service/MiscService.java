package ru.service;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.dao.entity.Route;
import ru.dao.entity.RoutePoint;
import ru.dao.repository.RoutePointRepository;
import ru.dao.repository.RouteRepository;

import javax.persistence.EntityManager;

@Service
public class MiscService {
    private final RouteRepository routeRepository;
    private final RoutePointRepository routePointRepository;
    private final EntityManager entityManager;


    public MiscService(RouteRepository routeRepository, RoutePointRepository routePointRepository, EntityManager entityManager) {
        this.routeRepository = routeRepository;
        this.routePointRepository = routePointRepository;
        this.entityManager = entityManager;
    }

    public Route duplicateRoute(Integer routeId){
        Route route = routeRepository.findById(routeId).orElseThrow(()->new IllegalArgumentException("Маршрут не найден"));
        Hibernate.initialize(route.getRoutePoints());
        entityManager.detach(route);
        route.setId(null);
        for(RoutePoint routePoint :route.getRoutePoints()){
            routePoint.setId(null);
        }
        route.setName(route.getName()+" (Копия)");
        routeRepository.save(route);

        return route;
    }
}
