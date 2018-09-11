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
import ru.dao.repository.OrderOfferRepository;
import ru.dao.repository.OrderRepository;

@Controller
@RequestMapping("/info")
public class InfoController {

    private final OrderRepository orderRepository;
    private final OrderOfferRepository orderOfferRepository;

    @Autowired
    public InfoController(OrderRepository orderRepository, OrderOfferRepository orderOfferRepository) {
        this.orderRepository = orderRepository;
        this.orderOfferRepository = orderOfferRepository;
    }

    @RequestMapping(value="/orders/{orderId}")
    private String getFullOrder(@PathVariable Integer orderId, ModelMap modelMap){
        Order order = orderRepository.findById(orderId).orElse(null);
        assert order != null;
        Hibernate.initialize(order.getRoute());
        Hibernate.initialize(order.getCompany());
        Hibernate.initialize(order.getOffers());
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
