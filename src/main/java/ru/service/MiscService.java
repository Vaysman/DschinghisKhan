package ru.service;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.dao.entity.Route;
import ru.dao.entity.RoutePoint;
import ru.dao.repository.RouteRepository;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service containing tasks that have very little in common with any other
 * and aren't welcome anywhere else
 */
@Service
public class MiscService {
    private final RouteRepository routeRepository;
    private final EntityManager entityManager;


    public MiscService(RouteRepository routeRepository, EntityManager entityManager) {
        this.routeRepository = routeRepository;
        this.entityManager = entityManager;
    }

    /**
     * @param routeId ID of a route to be duplicated
     * @return A brand new route with brand new routePoints.
     * It's completely identical to the route with the provided ID
     * But with current date added to it's name
     */
    public Route duplicateRoute(Integer routeId){
        Route route = routeRepository.findById(routeId).orElseThrow(()->new IllegalArgumentException("Маршрут не найден"));
        Hibernate.initialize(route.getRoutePoints());
        entityManager.detach(route);
        route.setId(null);
        for(RoutePoint routePoint :route.getRoutePoints()){
            routePoint.setId(null);
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        route.setName(route.getName()+" "+simpleDateFormat.format(new Date()));
        routeRepository.save(route);

        return route;
    }
}
