package ru.controller.ajax;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.dto.json.order.OrderAcceptData;
import ru.service.OrderLifecycleService;

@RestController
@RequestMapping("/orderLifecycle")
@PreAuthorize("hasAnyAuthority('ADMIN','DISPATCHER','TRANSPORT_COMPANY')")
public class OrderLifecycleController {

    private final OrderLifecycleService orderLifecycleService;

    @Autowired
    public OrderLifecycleController(OrderLifecycleService orderLifecycleService) {
        this.orderLifecycleService = orderLifecycleService;
    }

    @RequestMapping(value = "/accept/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    private String acceptOrder(@PathVariable Integer id, @RequestBody OrderAcceptData details) {
        try {
            orderLifecycleService.acceptOrder(id, details);
            return "Заявка успешно принята и  отправлена на утверждение диспетчеру";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/reject/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    private String rejectOrder(@PathVariable Integer id, @RequestBody Integer companyId) {
        try {
            orderLifecycleService.rejectOrder(id, companyId);
            return "Заявка успешно отклонена";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
