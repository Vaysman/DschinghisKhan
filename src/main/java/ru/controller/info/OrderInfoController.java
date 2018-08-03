package ru.controller.info;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.dao.entity.Order;
import ru.dao.repository.OrderRepository;

@Controller
@RequestMapping("/info")
public class OrderInfoController {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderInfoController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @RequestMapping(value="/orders/{orderId}")
    private String getFullOrder(@PathVariable Integer orderId, ModelMap modelMap){
        Order order = orderRepository.findById(orderId).orElse(null);
        assert order != null;
        Hibernate.initialize(order.getRoute());
        Hibernate.initialize(order.getCompany());
        modelMap.addAttribute("order",order);
        return "info/order";
    }

}
