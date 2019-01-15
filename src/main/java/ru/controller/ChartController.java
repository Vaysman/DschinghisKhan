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
import ru.dao.entity.Contract;
import ru.dao.entity.Order;
import ru.dao.repository.ClientRepository;
import ru.dao.repository.CompanyRepository;
import ru.dao.repository.ContractRepository;
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
    private final ContractRepository contractRepository;

    @Autowired
    public ChartController(OrderRepository orderRepo, CompanyRepository companyRepo, ClientRepository clientRepo, ContractRepository contractRepository) {
        this.orderRepo = orderRepo;
        this.companyRepo = companyRepo;
        this.clientRepo = clientRepo;
        this.contractRepository = contractRepository;
    }

    @RequestMapping(value = "/orders", params = {"from", "to"})
    @ResponseBody
    public Map<String, Object> getOrderForPeriod(@RequestParam String from, @RequestParam String to) throws ParseException {
        Map<String,Object> dataMap = new HashMap<>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d/M/yyyy");

        Date fromDate = simpleDateFormat.parse(from);
        Date toDate = simpleDateFormat.parse(to);
        Integer originator = ((AuthToken) SecurityContextHolder.getContext().getAuthentication()).getCompanyId();
//        Integer originator = 2;
        List<Order> orderList = orderRepo.findAllByOriginatorAndDispatchDateBetween(originator, fromDate, toDate);

        Map<GeneralizedOrderStatus, List<Order>> generalStatusToOrdersMap = orderList.stream()
                .collect(Collectors.groupingBy(order->{
                    OrderStatus status = order.getStatus();
                    if(Arrays.stream(OrderStatus.getPreDispatchStatuses()).anyMatch(status::equals)){
                        return GeneralizedOrderStatus.PRE_DISPATCH;
                    } else if (Arrays.stream(OrderStatus.getDeliveredStatuses()).anyMatch(status::equals)){
                        return GeneralizedOrderStatus.DELIVERED;
                    } else if (Arrays.stream(OrderStatus.getStatusesInWork()).anyMatch(status::equals)){
                        return GeneralizedOrderStatus.IN_WORK;
                    }  else {
                        return GeneralizedOrderStatus.OTHER;
                    }
                }));

        List<Map<String, Object>> seriesData = generalStatusToOrdersMap.entrySet().stream()
                .map(generalStatusWithOrders -> {
                    Map<String,Object> item = new HashMap<>();
                    item.put("name",generalStatusWithOrders.getKey().getGeneralStatusName());
                    item.put("y",generalStatusWithOrders.getValue().size());
                    item.put("drilldown",generalStatusWithOrders.getKey().name());
                    item.put("color",generalStatusWithOrders.getKey().getColor());
                    return item;
                }).collect(Collectors.toList());


        List<Map<String,Object>> drilldownData = generalStatusToOrdersMap.entrySet().stream()
                .map(entry -> {
                    Map<String,Object> item = new HashMap<>();
                    item.put("name",entry.getKey().getGeneralStatusName());
                    item.put("id",entry.getKey().name());

                    List<List<Object>> itemData = entry.getValue().stream()
                            .collect(Collectors.groupingBy(Order::getStatus))
                            .entrySet().stream()
                            .map(statusWithOrders -> {
                                List<Object> itemDataEntry = new ArrayList<>();
                                itemDataEntry.add(statusWithOrders.getKey().getStatusName());
                                itemDataEntry.add(statusWithOrders.getValue().size());
                                return itemDataEntry;
                            }).collect(Collectors.toList());
                    item.put("data",itemData);

                    return item;
                }).collect(Collectors.toList());


        Map<String,Object> series = new HashMap<>();
        series.put("name","Заявки");
        series.put("colorByPoint",true);
        series.put("data",seriesData);

        dataMap.put("series",series);
        dataMap.put("drilldown", drilldownData);
        
        return dataMap;
    }


    private enum GeneralizedOrderStatus {
        PRE_DISPATCH("orange","Не отправлено"),
        IN_WORK("#1a2b6d","В работе"),
        DELIVERED("green","Доставлено"),
        OTHER("grey","Другое");


        @Getter
        private String color;

        @Getter
        private String generalStatusName;

        GeneralizedOrderStatus(String color, String name) {
            this.color = color;
            this.generalStatusName = name;
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
        List<Order> orderList = orderRepo.findAllByOriginatorAndDispatchDateBetween(originator, fromDate, toDate);
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


    @RequestMapping(value = "/contractsPerCompany")
    @ResponseBody
    public List<Map<String, Object>> getContractsByCompany(){

        Integer originator = ((AuthToken) SecurityContextHolder.getContext().getAuthentication()).getCompanyId();
//        Integer originator = 2;
        List<Contract> contracts = contractRepository.findAllByInitiativeCompanyId(originator);
        for(Contract contract : contracts){
            Hibernate.initialize(contract.getCompany());
        }
         return contracts.stream().collect(Collectors.groupingBy(contract -> contract.getCompany().getName()))
                 .entrySet().stream()
                 .map(companyWithContracts ->{
                     Map<String,Object> item  = new HashMap<>();
                     item.put("name",companyWithContracts.getKey());
                     item.put("y",companyWithContracts.getValue().size());
                     return item;
                 }).collect(Collectors.toList());

    }





}
