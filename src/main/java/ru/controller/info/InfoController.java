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
import ru.dao.entity.Order;
import ru.dao.entity.OrderOffer;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.ContactRepository;
import ru.dao.repository.OrderOfferRepository;
import ru.dao.repository.OrderRepository;

@Controller
@RequestMapping("/info")
public class InfoController {

    private final OrderRepository orderRepository;
    private final OrderOfferRepository orderOfferRepository;
    private final ContactRepository contactRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public InfoController(OrderRepository orderRepository, OrderOfferRepository orderOfferRepository, ContactRepository contactRepository, CompanyRepository companyRepository) {
        this.orderRepository = orderRepository;
        this.orderOfferRepository = orderOfferRepository;
        this.contactRepository = contactRepository;
        this.companyRepository = companyRepository;
    }

    @RequestMapping(value="/orders/{orderId}")
    private String getFullOrder(@PathVariable Integer orderId, ModelMap modelMap){
        Order order = orderRepository.findById(orderId).orElse(null);
        assert order != null;

        Hibernate.initialize(order.getRoute());
        Hibernate.initialize(order.getCompany());
        Hibernate.initialize(order.getOffers());
        Hibernate.initialize(order.getCompany());
        Hibernate.initialize(order.getTransport());
        Hibernate.initialize(order.getDriver());
        Hibernate.initialize(order.getFiles());
        if(order.getDriver()!=null){
            Hibernate.initialize(order.getDriver().getFiles());
        }
        if(order.getTransport()!=null){
            Hibernate.initialize(order.getTransport().getFiles());
        }




        for(OrderOffer offer : order.getOffers()){
            Hibernate.initialize(offer.getCompany());
        }
        modelMap.addAttribute("order",order);

        return "info/order";
    }

    @GetMapping(value = "/offers/{offerId}")
    private String getFullOffer(@PathVariable Integer offerId, ModelMap modelMap){
        OrderOffer offer = orderOfferRepository.findById(offerId).orElse(null);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if(authentication instanceof AuthToken){
            modelMap.addAttribute("currentCompanyId",((AuthToken)authentication).getCompanyId());
        }


        assert offer !=null;
        Hibernate.initialize(offer.getOrder());
        Hibernate.initialize(offer.getOrder().getRoute());
        Hibernate.initialize(offer.getCompany());
        Hibernate.initialize(offer.getTransport());
        Hibernate.initialize(offer.getDriver());
        Hibernate.initialize(offer.getManagerCompany());
        modelMap.addAttribute("offer",offer);
        return "info/offer";
    }
}
