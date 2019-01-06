package ru.controller;

import lombok.Getter;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.configuration.authentication.AuthToken;
import ru.constant.OrderStatus;
import ru.dao.entity.Order;
import ru.dao.repository.ClientRepository;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.OrderRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chart")
public class ChartController {

    private final OrderRepository orderRepo;
    private final CompanyRepository companyRepo;
    private final ClientRepository clientRepo;

    @Autowired
    public ChartController(OrderRepository orderRepo, CompanyRepository companyRepo, ClientRepository clientRepo) {
        this.orderRepo = orderRepo;
        this.companyRepo = companyRepo;
        this.clientRepo = clientRepo;
    }

    @RequestMapping(value = "/orders", params = {"from", "to"})
    @ResponseBody
    public List<Map<String, Object>> getOrderForPeriod(@RequestParam String from, @RequestParam String to) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/yyyy");

        Date fromDate = simpleDateFormat.parse(from);
        Date toDate = simpleDateFormat.parse(to);
        Integer originator = ((AuthToken) SecurityContextHolder.getContext().getAuthentication()).getCompanyId();
//        Integer originator = 2;
        List<Order> orderList = orderRepo.findAllByOriginatorAndStatusChangeDateBetween(originator, fromDate, toDate);
        List<Map<String, Object>> orderStatusToOrdersMap = orderList.stream()
                .collect(Collectors.groupingBy(order->{
                    OrderStatus status = order.getStatus();
                    if(Arrays.stream(OrderStatus.getPreDispatchStatuses()).anyMatch(status::equals)){
                        return ChartColours.PRE_DISPATCH;
                    } else if (Arrays.stream(OrderStatus.getStatusesInWork()).anyMatch(status::equals)){
                        return ChartColours.IN_WORK;
                    } else if (Arrays.stream(OrderStatus.getDeliveredStatuses()).anyMatch(status::equals)){
                        return ChartColours.DELIVERED;
                    } else {
                        return ChartColours.OTHER;
                    }
                }))
                .entrySet().stream()
                .map(x -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", x.getKey().getName());
                    item.put("y", x.getValue().size());
                    item.put("color",x.getKey().getColor());
                    return item;
                }).collect(Collectors.toList());


        return orderStatusToOrdersMap;
    }


    private enum ChartColours{
        PRE_DISPATCH("orange","Не отправлено"),
        IN_WORK("#1a2b6d","В работе"),
        DELIVERED("green","Доставлено"),
        OTHER("grey","Другое");


        @Getter
        private String color;

        @Getter
        private String name;

        ChartColours(String color, String name) {
            this.color = color;
            this.name = name;
        }
    }

    @RequestMapping(value = "/ordersPerTransportCompany", params = {"from", "to"})
    @ResponseBody
    public List<Map<String, Object>> getOrdersByTransportCompany(@RequestParam String from, @RequestParam String to) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/yyyy");

        Date fromDate = simpleDateFormat.parse(from);
        Date toDate = simpleDateFormat.parse(to);
        Integer originator = ((AuthToken) SecurityContextHolder.getContext().getAuthentication()).getCompanyId();
//        Integer originator = 2;
        List<Order> orderList = orderRepo.findAllByOriginatorAndStatusChangeDateBetween(originator, fromDate, toDate);
        for(Order order : orderList){
            Hibernate.initialize(order.getCompany());
        }
        return orderList.stream()
                .filter(order->order.getCompany()!=null)
                .collect(Collectors.groupingBy(Order::getCompany))
                .entrySet().stream()
                .map(companyWithOrders->{
                    Map<String, Object> newItem = new HashMap<>();
                    newItem.put("name", companyWithOrders.getKey().getName());
                    newItem.put("y", companyWithOrders.getValue().size());
                    newItem.put("description", companyWithOrders.getKey().getName());
                    return newItem;
                }).collect(Collectors.toList());

    }




}
