package ru.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dao.entity.Route;
import ru.service.MiscService;


//This controller provides access to tasks that aren't grouped/don't have much in common with any others
@RestController
@RequestMapping("/misc")
public class MiscController {
    private final MiscService miscService;

    public MiscController(MiscService miscController) {
        this.miscService = miscController;
    }


    @PostMapping(value="/dupeRoute/{routeId}")
    @PreAuthorize("hasAuthority('DISPATCHER')")
    private Route dupeRoute(@PathVariable Integer routeId){
        return miscService.duplicateRoute(routeId);
    }
}
