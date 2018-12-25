package ru.controller;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.constant.ContactType;
import ru.dao.entity.*;
import ru.dao.repository.*;
import ru.service.RegisterService;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
//@PreAuthorize("isAuthenticated()")
@RequestMapping("/data")
public class DataController {
    private final RouteRepository routeRepository;
    private final CompanyRepository companyRepository;
    private final OrderRepository orderRepository;
    private final OrderOfferRepository offerRepository;
    private final ContactRepository contactRepository;
    private final ContractRepository contractRepository;
    private final RegisterService registerService;

    @Autowired
    public DataController(OrderRepository orderRepository, RouteRepository routeRepository, CompanyRepository companyRepository, OrderOfferRepository offerRepository, ContactRepository contactRepository, ContractRepository contractRepository, RegisterService registerService) {
        this.orderRepository = orderRepository;
        this.routeRepository = routeRepository;
        this.companyRepository = companyRepository;
        this.offerRepository = offerRepository;
        this.contactRepository = contactRepository;
        this.contractRepository = contractRepository;
        this.registerService = registerService;
    }

    @PostMapping("/registerTransportCompany")
    public Company registerCompany(@RequestBody Company company) throws MessagingException {
        return registerService.registerCompany(company);
    }

    @GetMapping(value = "/contracts/{contractId}", produces = "application/json; charset=UTF-8")
    private Contract getContractInfo(@PathVariable Integer contractId){
        Contract contract = contractRepository.findById(contractId).orElseThrow(()->new IllegalArgumentException("Данный контракт не найден"));
        Hibernate.initialize(contract.getCompany());
        Hibernate.initialize(contract.getFile());
        Hibernate.initialize(contract.getInitiativeCompany());
        return contract;
    }

    @GetMapping(value = "/routePointsForRoute/{routeId}", produces = "application/json; charset=UTF-8")
    private Set<RoutePoint> getRoutePointsForRoute(@PathVariable Integer routeId){
        Route route = routeRepository.findById(routeId).orElseThrow(()->new IllegalArgumentException("Данного маршрута не существует"));
        Hibernate.initialize(route.getRoutePoints());
        for(RoutePoint routePoint : route.getRoutePoints()){
            Hibernate.initialize(routePoint.getPoint());
            Hibernate.initialize(routePoint.getClient());
            Hibernate.initialize(routePoint.getContact());
        }
        return route.getRoutePoints();
    }

    @GetMapping(value = "/offers/{offerId}", produces = "application/json; charset=UTF-8")
    private OrderOffer getOfferInfo(@PathVariable Integer offerId){
        OrderOffer offer = offerRepository.findById(offerId).orElseThrow(()->new IllegalArgumentException("Данный оффер не найден"));
        Hibernate.initialize(offer.getOrder());
        Hibernate.initialize(offer.getCompany());
        Hibernate.initialize(offer.getDriver());
        Hibernate.initialize(offer.getOrder().getRoute().getRoutePoints());
        Hibernate.initialize(offer.getTransport());
        if (offer.getDriver()!=null){
            if (orderRepository.findFirstByOriginatorAndDriverId(offer.getOrder().getOriginator(), offer.getDriver().getId()).isPresent()){
                offer.getDriver().setIsNew(false);
            } else {
                offer.getDriver().setIsNew(true);
            }
        }
        for(RoutePoint routePoint : offer.getOrder().getRoute().getRoutePoints()){
            Hibernate.initialize(routePoint.getPoint());
        }
        return offer;
    }

    @GetMapping(value = "/offersForOrder/{orderId}", produces = "application/json; charset=UTF-8")
    private List<OrderOffer> getOffersForOrder(@PathVariable Integer orderId){
        Order order= orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Данная заявка не существует"));
        List<OrderOffer> offers= offerRepository.findAllByOrder(order);
        for(OrderOffer offer: offers){
            Hibernate.initialize(offer.getCompany());
        }
        return offers;
    }


    @GetMapping(value="/orders/forAccept/{orderId}",produces = "application/json; charset=UTF-8")
    private Order getFullOrderForAccept(@PathVariable Integer orderId){
        Order order = orderRepository.findById(orderId).orElse(null);
        Hibernate.initialize(order.getRoute());
        Hibernate.initialize(order.getRoute().getRoutePoints());
        for(RoutePoint routePoint : order.getRoute().getRoutePoints()){
            Hibernate.initialize(routePoint.getPoint());
        }
        Hibernate.initialize(order.getCompany());
        Hibernate.initialize(order.getDriver());
        Hibernate.initialize(order.getTransport());

        return order;
    }
    @GetMapping(value="/orders/{orderId}",produces = "application/json; charset=UTF-8")
    private Map<String, Object> getFullOrder(@PathVariable Integer orderId){
        Map<String, Object> map = new HashMap<>();
        Order order = orderRepository.findById(orderId).orElse(null);
        Hibernate.initialize(order.getRoute());
        Hibernate.initialize(order.getRoute().getRoutePoints());
        for(RoutePoint routePoint : order.getRoute().getRoutePoints()){
            Hibernate.initialize(routePoint.getPoint());
        }
        Hibernate.initialize(order.getCompany());
        Hibernate.initialize(order.getDriver());
        Hibernate.initialize(order.getTransport());
        Contact companyContact = contactRepository.findFirstByCompanyAndType(order.getCompany(), ContactType.PRIMARY).orElse(null);
        Company dispatcherCompany = companyRepository.findById(order.getOriginator()).orElseThrow(()->new IllegalStateException("Order does not have a dispatcher company"));
        Contact dispatcherContact = contactRepository.findFirstByCompanyAndType(dispatcherCompany, ContactType.PRIMARY).orElse(null);
        if(order.getCompany()!=null){
            Hibernate.initialize(order.getCompany().getPoint());
        }
        Hibernate.initialize(dispatcherCompany.getPoint());

        map.put("order", order);
        map.put("companyContact", companyContact);
        map.put("dispatcherCompany", dispatcherCompany);
        map.put("dispatcherContact", dispatcherContact);

        return map;
    }

    @GetMapping(value = "/companies/{companyId}", produces = "application/json; charset=UTF-8")
    private Company getFullCompany(@PathVariable Integer companyId){
        Company company = companyRepository.findById(companyId).orElse(null);
        Hibernate.initialize(company.getPoint());
        return company;
    }

    @GetMapping(value="/routes",produces = "application/json; charset=UTF-8")
    private Iterable<Route> getAllRoutes(){
        Iterable<Route> routes = routeRepository.findAll();
        return routes;
    }
}
