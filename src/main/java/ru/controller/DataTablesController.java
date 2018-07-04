package ru.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.dao.entity.*;
import ru.dao.entity.specification.RoutePoints;
import ru.dao.entity.specification.TransportCompanies;
import ru.dao.repository.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/dataTables")
public class DataTablesController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private RouteRepository routeRepository;

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/users", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<User> getRequests(@Valid @RequestBody DataTablesInput input) {
        return userRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/points", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<Point> getPoints(@Valid @RequestBody DataTablesInput input) {
        return pointRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/transportCompanies", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<Company> getTransportCompanies(@Valid @RequestBody DataTablesInput input) {
        return companyRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/transportCompanies", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<Company> getAllTransportCompanies() {
        return companyRepository.findAll(new DataTablesInput());
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/routePoints", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<RoutePoint> getRoutePoints(@Valid @RequestBody DataTablesInput input) {
        return routePointRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/routes", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<Route> getRoutes(@Valid @RequestBody DataTablesInput input) {
        return routeRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/routes", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<Route> getGetRoutes() {
        return routeRepository.findAll(new DataTablesInput());
    }


    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/routePoints/routePointsForRoute/{routeId}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<RoutePoint> routePointsForRoute(@Valid @RequestBody DataTablesInput input, @PathVariable Integer routeId) {
        return routePointRepository.findAll(input, RoutePoints.pointsForRoute(routeRepository.findById(routeId).orElseThrow(()->new IllegalArgumentException("No such route"))));
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/transportCompanies/transportCompaniesForRoute/{routeId}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<Company> transportCompaniesForRoute(@Valid @RequestBody DataTablesInput input, @PathVariable Integer routeId) {
        return companyRepository.findAll(input, TransportCompanies.transportCompaniesForRoute(routeRepository.findById(routeId).orElseThrow(()->new IllegalArgumentException("No such route"))));
    }
}
