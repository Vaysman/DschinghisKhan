package ru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.configuration.authentication.AuthToken;
import ru.constant.OrderStatus;
import ru.dao.entity.Order;
import ru.dao.entity.User;
import ru.dto.json.order.OrderAcceptData;
import ru.dto.json.order.OrderAssignData;
import ru.service.OrderLifecycleService;

import java.util.Map;

@RestController
@RequestMapping("/orderLifecycle")
@PreAuthorize("hasAnyAuthority('ADMIN','DISPATCHER','TRANSPORT_COMPANY')")
public class OrderLifecycleController {

    private final OrderLifecycleService orderLifecycleService;

    private User getCurrentUser(){
        return ((AuthToken)SecurityContextHolder.getContext().getAuthentication()).getUser();
    }

    @Autowired
    public OrderLifecycleController(OrderLifecycleService orderLifecycleService) {
        this.orderLifecycleService = orderLifecycleService;
    }


    @PostMapping(value="/dupeOrder/{orderId}")
    @PreAuthorize("hasAuthority('DISPATCHER')")
    private Order dupeOrder(@PathVariable Integer orderId){
          return orderLifecycleService.duplicateOrder(orderId);
    }

    @PostMapping(value="/confirmPayment/{orderId}")
    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    private String confirmPayment(@PathVariable Integer orderId){
        try {
            orderLifecycleService.confirmPayment(getCurrentUser(),orderId);
            return "Success";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    @PostMapping(value="/claimNonPayment/{orderId}")
    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    private String claimNonPayment(@PathVariable Integer orderId){
        try {
            orderLifecycleService.claimNonPayment(getCurrentUser(),orderId);
            return "Success";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    @PostMapping(value="/claimPayment/{orderId}")
    @PreAuthorize("hasAuthority('DISPATCHER')")
    private String claimPayment(@PathVariable Integer orderId){
        try {
            orderLifecycleService.claimPayment(getCurrentUser(),orderId);
            return "Success";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    @PostMapping(value="/confirmDocumentDelivery/{orderId}")
    @PreAuthorize("hasAuthority('DISPATCHER')")
    private String confirmDocumentDelivery(@PathVariable Integer orderId){
        try {
            orderLifecycleService.confirmDocumentDelivery(getCurrentUser(),orderId);
            return "Success";
        } catch (Exception e){
            return e.getMessage();
        }
    }


    @PostMapping(value = "/confirmDelivery/{orderId}")
    @PreAuthorize("isAuthenticated()")
    private String confirmDelivery(@PathVariable Integer orderId){
        try{
            orderLifecycleService.confirmDelivery(getCurrentUser(),orderId);
            return "Success";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    @PostMapping(value = "/changeStatus/{orderId}")
    @PreAuthorize("isAuthenticated()")
    private String changeStatus(@PathVariable Integer orderId, @RequestBody Map<String,OrderStatus> orderStatus){
        try{
            orderLifecycleService.changeStatus(getCurrentUser(),orderId,orderStatus.get("status"));
            return "Success";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/confirm/{offerId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('DISPATCHER')")
    private String confirm(@PathVariable Integer offerId){
        try{
            orderLifecycleService.confirm(getCurrentUser(),offerId);
            return "Success";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/assign/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('DISPATCHER')")
    private String assign(@PathVariable Integer id, @RequestBody OrderAssignData assignData){
        try{
            orderLifecycleService.assign(id, getCurrentUser(), assignData);
            return "Success";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/declineOffer/{offerId}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('DISPATCHER')")
    private String declineOffer(@PathVariable Integer offerId){
        try{
            orderLifecycleService.declineOffer(getCurrentUser(),offerId);
            return "Success";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/accept/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    private String accept(@PathVariable Integer id, @RequestBody OrderAcceptData details) {
        try {
            orderLifecycleService.accept(id, getCurrentUser(), details);
            return "Заявка успешно принята и  отправлена на утверждение диспетчеру";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/reject/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    private String reject(@PathVariable Integer id, @RequestBody Integer companyId) {
        try {
            orderLifecycleService.reject(id, getCurrentUser(), companyId);
            return "Заявка успешно отклонена";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
