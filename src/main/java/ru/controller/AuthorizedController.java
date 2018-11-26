package ru.controller;

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
import ru.dao.entity.Company;
import ru.dao.entity.Contact;
import ru.dao.entity.Point;
import ru.dao.entity.User;
import ru.dao.repository.*;


@Controller
@Transactional(propagation = Propagation.REQUIRED)
public class AuthorizedController {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ContactRepository contactRepository;
    private final RouteReviewOpinionRepository opinionRepository;
    private final RouteReviewRepository reviewRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public AuthorizedController(CompanyRepository companyRepository, UserRepository userRepository, ContactRepository contactRepository, RouteReviewOpinionRepository opinionRepository, RouteReviewRepository reviewRepository, ContractRepository contractRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.contactRepository = contactRepository;
        this.opinionRepository = opinionRepository;
        this.reviewRepository = reviewRepository;
        this.contractRepository = contractRepository;
    }

    @RequestMapping({"/main", "/index", "/"})
    public String main(Model modelAndView) {
        AuthToken authentication = (AuthToken) SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findById(authentication.getUser().getId()).orElse(null);
        assert user != null;
        Company company = user.getCompany();
        Contact contact = contactRepository.findFirstByCompanyAndType(company, ContactType.PRIMARY).orElse(null);
        Point point = company.getPoint();
        modelAndView.addAttribute("company", company);
        modelAndView.addAttribute("contact", contact);
        modelAndView.addAttribute("user", user);
        modelAndView.addAttribute("point", point);
        return "profile";
    }

    @GetMapping("/routes")
    public String routes() {
        return "routes";
    }

    @RequestMapping(value = "/orders")
    public String orders() {
        return "main";
    }

    //Any methods with @ModelAttribute on them must be public in order for autoWired fields to work.
    //Otherwise you'll get NullPointerException when trying to use autowired field
    //In this instance it'll be upon calling findById in CompanyRepository
    @ModelAttribute
    public ModelMap setConstants(ModelMap model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        AuthToken authentication = (AuthToken) securityContext.getAuthentication();
        authentication.refreshAll();
        if (authentication.getUser().getUserRole() == UserRole.ROLE_TRANSPORT_COMPANY) {
            model.addAttribute("pendingOrders", authentication.getPendingOrders());
            model.addAttribute("pendingOpinions",authentication.getPendingOpinions());
            model.addAttribute("opinions", authentication.getOpinions());;
            model.addAttribute("receivedContracts",authentication.getReceivedContracts());
        } else if (authentication.getUser().getUserRole() == UserRole.ROLE_DISPATCHER) {
            model.addAttribute("managedOffers", authentication.getManagedOffers());
            model.addAttribute("sentContracts", authentication.getSentContracts());
            model.addAttribute("reviews", authentication.getRouteReviews());
        }
        model.addAttribute("companyAddress", authentication.getCompanyPoint().getAddress());
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
        model.addAttribute("changeableStatuses", OrderStatus.getChangeableStatuses());
        model.addAttribute("statusesForDispatcher", UserRole.ROLE_DISPATCHER.getOrderStatuses());
        model.addAttribute("statusesForCompany", UserRole.ROLE_TRANSPORT_COMPANY.getOrderStatuses());
        model.addAttribute("deliveredStatuses", OrderStatus.getDeliveredStatuses());
        return model;
    }
}
