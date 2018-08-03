package ru.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.configuration.authentication.AuthToken;
import ru.constant.CompanyType;
import ru.constant.OrderStatus;
import ru.dao.entity.*;
import ru.dao.entity.specification.*;
import ru.dao.repository.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/dataTables")
public class DataTablesController {

    private final OrderHistoryRepository orderHistoryRepository;

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final PointRepository pointRepository;

    private final CompanyRepository companyRepository;

    private final RoutePointRepository routePointRepository;

    private final RouteRepository routeRepository;

    private final ContactRepository contactRepository;

    private final TransportRepository transportRepository;

    private final DriverRepository driverRepository;

    @Autowired
    public DataTablesController(OrderHistoryRepository orderHistoryRepository,
                                OrderRepository orderRepository,
                                UserRepository userRepository,
                                PointRepository pointRepository,
                                CompanyRepository companyRepository,
                                RoutePointRepository routePointRepository,
                                RouteRepository routeRepository,
                                ContactRepository contactRepository,
                                TransportRepository transportRepository,
                                DriverRepository driverRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.pointRepository = pointRepository;
        this.companyRepository = companyRepository;
        this.routePointRepository = routePointRepository;
        this.routeRepository = routeRepository;
        this.contactRepository = contactRepository;
        this.transportRepository = transportRepository;
        this.driverRepository = driverRepository;
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/users", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<User> getRequests(@Valid @RequestBody DataTablesInput input) {
        return userRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/usersForUser", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<User> getRequestsForDispatcher(@Valid @RequestBody DataTablesInput input) {
        return userRepository.findAll(input, Users.usersForUser(AuthToken.getCurrentAuthToken().getCompanyId()));
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/points", method = RequestMethod.POST)
    public DataTablesOutput<Point> getPoints(@Valid @RequestBody DataTablesInput input) {
        return pointRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/pointsForUser", method = RequestMethod.POST)
    public DataTablesOutput<Point> getPointsForUser(@Valid @RequestBody DataTablesInput input) {
        return pointRepository.findAll(input, Points.pointsForUser(AuthToken.getCurrentAuthToken().getCompanyId()));
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/companies", method = RequestMethod.POST)
    public DataTablesOutput<Company> getCompanies(@Valid @RequestBody DataTablesInput input) {
        return companyRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/transportCompanies", method = RequestMethod.POST)
    public DataTablesOutput<Company> getTransportCompanies(@Valid @RequestBody DataTablesInput input) {
        return companyRepository.findAll(input,Companies.companiesByType(CompanyType.TRANSPORT));
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/companiesForUser", method = RequestMethod.POST)
    public DataTablesOutput<Company> getCompaniesForUser(@Valid @RequestBody DataTablesInput input) {
        return companyRepository.findAll(input,Companies.companiesForUser(AuthToken.getCurrentAuthToken().getCompanyId()));
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/transportCompanies", method = RequestMethod.GET)
    public DataTablesOutput<Company> getAllTransportCompanies() {
        return companyRepository.findAll(new DataTablesInput());
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/routePoints", method = RequestMethod.POST)
    public DataTablesOutput<RoutePoint> getRoutePoints(@Valid @RequestBody DataTablesInput input) {
        return routePointRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/routes", method = RequestMethod.POST)
    public DataTablesOutput<Route> getRoutes(@Valid @RequestBody DataTablesInput input) {
        return routeRepository.findAll(input);
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/routesForUser", method = RequestMethod.POST)
    public DataTablesOutput<Route> getRoutesForUser(@Valid @RequestBody DataTablesInput input) {
        return routeRepository.findAll(input, Routes.usersForUser(AuthToken.getCurrentAuthToken().getCompanyId()));
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/driversForUser", method = RequestMethod.POST)
    public DataTablesOutput<Driver> driversForUser(@Valid @RequestBody DataTablesInput input) {
        return driverRepository.findAll(input, Drivers.driversForUser(AuthToken.getCurrentAuthToken().getCompanyId()));
    }
    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/transportsForUser", method = RequestMethod.POST)
    public DataTablesOutput<Transport> transportsForUser(@Valid @RequestBody DataTablesInput input) {
        return transportRepository.findAll(input, Transports.transportsForUser(AuthToken.getCurrentAuthToken().getCompanyId()));
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/ordersForUser", method = RequestMethod.POST)
    public DataTablesOutput<Order> getOrdersForUser(@Valid @RequestBody DataTablesInput input) {
        try {
            return orderRepository.findAll(input, Orders.ordersForUser(AuthToken.getCurrentAuthToken().getCompanyId()));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/ordersForUserInWork", method = RequestMethod.POST)
    public DataTablesOutput<Order> getOrdersForUserInWork(@Valid @RequestBody DataTablesInput input) {
        try {
            return orderRepository.findAll(input, Orders.ordersInStatus(OrderStatus.getStatusesInWork()), Orders.ordersForUser(AuthToken.getCurrentAuthToken().getCompanyId()));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/ordersForCompanyInWork", method = RequestMethod.POST)
    public DataTablesOutput<Order> getOrdersForCompanyInWork(@Valid @RequestBody DataTablesInput input) {
        try {
            return orderRepository.findAll(input, Orders.ordersForCompany(AuthToken.getCurrentAuthToken().getCompanyId()), Orders.ordersInStatus(OrderStatus.getStatusesInWork()));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/contactsForUser", method = RequestMethod.POST)
    public DataTablesOutput<Contact> getContactsForUser(@Valid @RequestBody DataTablesInput input) {
        try {

            return contactRepository.findAll(input, Contacts.contactsForUser(AuthToken.getCurrentAuthToken().getCompanyId()));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public DataTablesOutput<Order> getOrders() {
        try {
            return orderRepository.findAll(new DataTablesInput());
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @JsonView(DataTablesOutput.View.class)
    @PostMapping(value = "/orderHistory/{id}")
    private DataTablesOutput<OrderHistory> getOrderHistory(@PathVariable Integer id, @Valid @RequestBody DataTablesInput input){
        try{
            return orderHistoryRepository.findAll(input, OrderHistories.historyForOrder(id));
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @JsonView(DataTablesOutput.View.class)
    @PostMapping(value = "/routePoints/routePointsForRoute/{routeId}")
    public DataTablesOutput<RoutePoint> routePointsForRoute(@Valid @RequestBody DataTablesInput input, @PathVariable Integer routeId) {
        return routePointRepository.findAll(input, RoutePoints.pointsForRoute(routeRepository.findById(routeId).orElseThrow(()->new IllegalArgumentException("No such route"))));
    }

    @RequestMapping(value = "/transportCompanies/transportCompaniesForRoute/{routeId}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public DataTablesOutput<Company> transportCompaniesForRoute(@Valid @RequestBody DataTablesInput input, @PathVariable Integer routeId) {
        return companyRepository.findAll(input, Companies.companiesForRoute(routeRepository.findById(routeId).orElseThrow(()->new IllegalArgumentException("No such route"))));
    }
}
