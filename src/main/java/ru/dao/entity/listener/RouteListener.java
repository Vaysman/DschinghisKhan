package ru.dao.entity.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.dao.entity.Company;
import ru.dao.entity.Route;
import ru.dao.entity.RoutePoint;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.RoutePointRepository;

import javax.persistence.PrePersist;

@Component
public class RouteListener {
    private static RoutePointRepository routePointRepository;
    private static CompanyRepository companyRepository;

    @Autowired
    public void init(RoutePointRepository routePointRepository, CompanyRepository companyRepository){
        RouteListener.routePointRepository=routePointRepository;
        RouteListener.companyRepository = companyRepository;
    }

    @PrePersist
    private void createFirstPoint(Route route){
        if(route.getOriginator()==null) return;
        Company company = companyRepository.findById(route.getOriginator()).orElse(null);
        if (company==null) return;
        if(company.getPoint()!=null){
            RoutePoint firstRoutePoint = RoutePoint
                    .builder()
                    .point(company.getPoint())
                    .route(route)
                    .queueNumber(0)
                    .build();
            routePointRepository.save(firstRoutePoint);
            route.getRoutePoints().add(firstRoutePoint);
        }
    }
}
