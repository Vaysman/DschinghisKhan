package ru.controller.info;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.configuration.authentication.AuthToken;
import ru.constant.ContactType;
import ru.dao.entity.*;
import ru.dao.repository.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/info")
public class InfoController {

    private final OrderRepository orderRepository;
    private final OrderOfferRepository orderOfferRepository;
    private final RouteReviewRepository reviewRepository;
    private final CompanyRepository companyRepository;
    private final ContactRepository contactRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public InfoController(OrderRepository orderRepository, OrderOfferRepository orderOfferRepository, RouteReviewRepository routeReviewRepository, CompanyRepository companyRepository, ContactRepository contactRepository, ContractRepository contractRepository) {
        this.orderRepository = orderRepository;
        this.orderOfferRepository = orderOfferRepository;
        this.reviewRepository = routeReviewRepository;
        this.companyRepository = companyRepository;
        this.contactRepository = contactRepository;
        this.contractRepository = contractRepository;
    }

    @GetMapping(value = "/company/{companyId}")
    public String showCompany(@PathVariable Integer companyId, ModelMap modelMap) {
        companyRepository.findById(companyId).ifPresent(company -> {
            Hibernate.initialize(company.getPoint());
            modelMap.addAttribute("company", company);
            contactRepository.findFirstByCompanyAndType(company, ContactType.PRIMARY).ifPresent(contact -> {
                modelMap.addAttribute("contact", contact);
            });

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof AuthToken) {
                modelMap.addAttribute("contracts",contractRepository.findAllByCompanyIdAndInitiativeCompanyId(company.getId(), ((AuthToken) authentication).getCompanyId())
                        .stream()
                        .peek(contract -> {
                            Hibernate.initialize(contract.getFile());
                        })
                        .collect(Collectors.toList()));
            }
        });
        return "info/company";
    }

    @GetMapping(value = "/review/{reviewId}")
    public String showReview(@PathVariable Integer reviewId, ModelMap modelMap) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AuthToken) {
            modelMap.addAttribute("companyId", ((AuthToken) authentication).getCompanyId());
        } else {
            modelMap.addAttribute("companyId", null);
        }

        RouteReview routeReview = reviewRepository.findById(reviewId).orElseThrow(() -> new IllegalArgumentException("Ревью не существует"));
        Hibernate.initialize(routeReview.getOpinions());
        Hibernate.initialize(routeReview.getRoute());
        Hibernate.initialize(routeReview.getCompany());
        for (RouteReviewOpinion reviewOpinion : routeReview.getOpinions()) {
            Hibernate.initialize(reviewOpinion.getCompany());
        }
        for (RoutePoint routePoint : routeReview.getRoute().getRoutePoints()) {
            Hibernate.initialize(routePoint.getPoint());
        }
        modelMap.addAttribute("review", routeReview);
        return "info/review";
    }

    @RequestMapping(value = "/orders/{orderId}")
    private String getFullOrder(@PathVariable Integer orderId, ModelMap modelMap) {
        Order order = orderRepository.findById(orderId).orElse(null);
        assert order != null;

        Hibernate.initialize(order.getRoute());
        Hibernate.initialize(order.getCompany());
        Hibernate.initialize(order.getOffers());
        Hibernate.initialize(order.getCompany());
        Hibernate.initialize(order.getTransport());
        Hibernate.initialize(order.getDriver());
        Hibernate.initialize(order.getFiles());
        if (order.getDriver() != null) {
            Hibernate.initialize(order.getDriver().getFiles());
        }
        if (order.getTransport() != null) {
            Hibernate.initialize(order.getTransport().getFiles());
        }


        for (OrderOffer offer : order.getOffers()) {
            Hibernate.initialize(offer.getCompany());
        }
        modelMap.addAttribute("order", order);

        return "info/order";
    }

    @GetMapping(value = "/offers/{offerId}")
    private String getFullOffer(@PathVariable Integer offerId, ModelMap modelMap) {
        OrderOffer offer = orderOfferRepository.findById(offerId).orElse(null);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication instanceof AuthToken) {
            modelMap.addAttribute("currentCompanyId", ((AuthToken) authentication).getCompanyId());
        }


        assert offer != null;
        Hibernate.initialize(offer.getOrder());
        Hibernate.initialize(offer.getOrder().getRoute());
        Hibernate.initialize(offer.getCompany());
        Hibernate.initialize(offer.getTransport());
        Hibernate.initialize(offer.getDriver());
        Hibernate.initialize(offer.getManagerCompany());
        modelMap.addAttribute("offer", offer);
        return "info/offer";
    }
}
