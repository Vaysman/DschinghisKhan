package ru.controller;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.configuration.authentication.AuthToken;
import ru.constant.*;
import ru.dao.entity.*;
import ru.dao.repository.*;

import java.util.*;
import java.util.stream.Collectors;


@Controller
@Transactional(propagation = Propagation.REQUIRED)
public class AuthorizedController {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final RouteReviewOpinionRepository opinionRepository;
    private final RouteReviewRepository reviewRepository;

    @Autowired
    public AuthorizedController(CompanyRepository companyRepository, UserRepository userRepository, ContactRepository contactRepository, RouteReviewOpinionRepository opinionRepository, RouteReviewRepository reviewRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.opinionRepository = opinionRepository;
        this.reviewRepository = reviewRepository;
    }

    @RequestMapping({"/main","/index","/"})
    public String main(Model modelAndView)
    {
        AuthToken authentication = (AuthToken) SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getUser().getId()).orElse(null);
        assert user != null;
        Company company = user.getCompany();
        Contact contact = contactRepository.findFirstByCompanyAndType(company,ContactType.PRIMARY).orElse(null);
        Point point = company.getPoint();
        modelAndView.addAttribute("company",company);
        modelAndView.addAttribute("contact",contact);
        modelAndView.addAttribute("user",user);
        modelAndView.addAttribute("point",point);
        return "main";
    }

    @GetMapping("/routes")
    public String routes() {
        return "routes";
    }

    @RequestMapping(value = "/orders")
    public String orders() {
        return "orders";
    }

    //Any methods with @ModelAttribute on them must be public in order for autoWired fields to work.
    //Otherwise you'll get NullPointerException when trying to use autowired field
    //In this instance it'll be upon calling findById in CompanyRepository
    @ModelAttribute
    public ModelMap setConstants(ModelMap model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        AuthToken authentication = (AuthToken) securityContext.getAuthentication();
        if (authentication.getUser().getUserRole() == UserRole.ROLE_TRANSPORT_COMPANY) {
            companyRepository.findById(authentication.getCompanyId())
                    .ifPresent(company -> model.addAttribute("pendingOrders", company
                            .getPendingOrders()
                            .stream()
                            .map(x -> {
                                Map<String, String> orderMap = new HashMap<>();
                                orderMap.put("id", x.getId().toString());
                                orderMap.put("number", x.getNumber());
                                orderMap.put("isMandatory",String.valueOf(x.getOrderObligation().equals(OrderObligation.MANDATORY)));
                                return orderMap;
                            }).collect(Collectors.toList())));

            List<RouteReviewOpinion> opinionList = new ArrayList<>();
            model.addAttribute("pendingOpinions",opinionRepository.findAllByCompanyId(authentication.getCompanyId())
                    .stream()
                    .peek(x->{
                        Hibernate.initialize(x.getReview());
                        Hibernate.initialize(x.getReview().getRoute());
                        Hibernate.initialize(x.getReview().getCompany());
                        opinionList.add(x);
                    })
                    .filter(x->x.getPrice()==null)
                    .count());
            model.addAttribute("opinions", opinionList);
        } else if (authentication.getUser().getUserRole() == UserRole.ROLE_DISPATCHER) {
            companyRepository.findById(authentication.getCompanyId())
                    .ifPresent(company -> model.addAttribute("managedOffers", company
                            .getManagedOffers()
                            .stream()
                            .map(x -> {
                                Map<String, String> orderMap = new HashMap<>();
                                orderMap.put("id", x.getId().toString());
                                orderMap.put("orderNumber", x.getOrderNumber());
                                orderMap.put("isPriceChanged", String.valueOf((!Objects.equals(x.getProposedPrice(), x.getDispatcherPrice()))));
                                return orderMap;
                            }).collect(Collectors.toList())));
            model.addAttribute("reviews", reviewRepository.findAllByCompanyId(authentication.getCompanyId())
            .stream()
            .peek(x->{
                Hibernate.initialize(x.getRoute());
            }).collect(Collectors.toList()));
        }
        model.addAttribute("currentCompanyId", String.valueOf(authentication.getCompanyId()));

        model.addAttribute("currentUser", authentication.getUser());

        model.addAttribute("loadingTypes", LoadingType.values());
        model.addAttribute("vehicleBodyTypes", VehicleBodyType.values());
        model.addAttribute("paymentTypes", DriverPaymentType.values());
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("requirements", OrderRequirements.values());
        model.addAttribute("orderObligations", OrderObligation.values());
        model.addAttribute("userRoles", UserRole.values());
        model.addAttribute("companyTypes", CompanyType.values());
        model.addAttribute("orderPaymentTypes", OrderPaymentType.values());
        model.addAttribute("changeableStatuses",OrderStatus.getChangeableStatuses());
        model.addAttribute("statusesForDispatcher",UserRole.ROLE_DISPATCHER.getOrderStatuses());
        model.addAttribute("statusesForCompany",UserRole.ROLE_TRANSPORT_COMPANY.getOrderStatuses());
        model.addAttribute("deliveredStatuses", OrderStatus.getDeliveredStatuses());
        return model;
    }
}
