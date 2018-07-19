package ru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.configuration.authentication.AuthToken;
import ru.constant.*;
import ru.dao.entity.Company;
import ru.dao.entity.PendingOrder;
import ru.dao.repository.CompanyRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@Transactional(propagation = Propagation.REQUIRED)
public class AuthorizedController {

    @Autowired
    private CompanyRepository companyRepository;
    private final Integer fgsfds;

    public AuthorizedController() {
        this.fgsfds=15;
    }

    @RequestMapping("/profile")
    @Transactional
    public String profile(ModelMap model) {
        return "companyProfile";
    }


    @RequestMapping("/main")
    public String main(ModelMap model)
    {
        return "main";
    }

    @GetMapping("/routes")
    public String routes() {
        return "routes";
    }

    @RequestMapping(value = "/orders")
    public String orders(ModelMap model) {
        return "orders";
    }

    //Any methods with @ModelAttribute on them must be public in order for autoWired fields to work.
    //Otherwise you'll get NullPointerException when trying to use autowired field
    //In this instance it'll be upon calling findById in CompanyRepository
    @ModelAttribute
    public ModelMap setConstants(ModelMap model){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        AuthToken authentication=(AuthToken)securityContext.getAuthentication();
        if(authentication.getUser().getUserRole()==UserRole.ROLE_TRANSPORT_COMPANY){
            Company company = companyRepository.findById(authentication.getCompanyId()).orElse(null);
            if(company!=null){
                model.addAttribute("pendingOrders", company.getPendingOrderSet().stream()
                        .filter(x->x.getProposedPrice()==null)
                        .map(PendingOrder::getOrder)
                        .map(x->{
                    Map<String,String> orderMap= new HashMap<>();
                    orderMap.put("id", x.getId().toString());
                    orderMap.put("number", x.getNumber());
                    return orderMap;
                }).collect(Collectors.toList()));
            }
        }
        model.addAttribute("currentCompanyId", String.valueOf(authentication.getCompanyId()));

        model.addAttribute("currentUser",authentication.getUser());

        model.addAttribute("loadingTypes", LoadingType.values());
        model.addAttribute("vehicleBodyTypes", VehicleBodyType.values());
        model.addAttribute("paymentTypes", DriverPaymentType.values());
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("requirements", OrderRequirements.values());
        model.addAttribute("orderObligations", OrderObligation.values());
        model.addAttribute("userRoles", UserRole.values());
        model.addAttribute("companyTypes", CompanyType.values());
        model.addAttribute("orderPaymentTypes", OrderPaymentType.values());
        return model;
    }
}
