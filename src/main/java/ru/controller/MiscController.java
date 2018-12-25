package ru.controller;

import org.hibernate.Hibernate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.configuration.authentication.AuthToken;
import ru.dao.entity.Route;
import ru.dao.entity.RoutePoint;
import ru.dao.repository.RouteRepository;
import ru.service.MiscService;


//This controller provides access to tasks that aren't grouped/don't have much in common with any others
@Controller
@RequestMapping("/misc")
public class MiscController {
    private final MiscService miscService;
    private final RouteRepository routeRepository;

    public MiscController(MiscService miscController, RouteRepository routeRepository) {
        this.miscService = miscController;
        this.routeRepository = routeRepository;
    }


    @PostMapping(value="/dupeRoute/{routeId}")
    @PreAuthorize("hasAuthority('DISPATCHER')")
    @ResponseBody
    private Route dupeRoute(@PathVariable Integer routeId){
        return miscService.duplicateRoute(routeId);
    }

    @GetMapping(value = "/editAndCopy/route/{routeId}")
    @PreAuthorize("hasAuthority('DISPATCHER')")
    private String editAndDupeRoute(@PathVariable Integer routeId, ModelMap modelMap){
        modelMap.addAttribute("currentCompanyId",((AuthToken)SecurityContextHolder.getContext().getAuthentication()).getCompanyId());
        Route route = routeRepository.findById(routeId).orElseThrow(()->new IllegalArgumentException("Данного маршрута не существует"));
        Hibernate.initialize(route.getRoutePoints());
        for(RoutePoint routePoint : route.getRoutePoints()){
            Hibernate.initialize(routePoint.getPoint());
            Hibernate.initialize(routePoint.getClient());
            Hibernate.initialize(routePoint.getContact());
        }
        Hibernate.initialize(route.getCompany().getPoint());
        modelMap.addAttribute("route",route);
        return "editAndCopyRoute";
    }
}
