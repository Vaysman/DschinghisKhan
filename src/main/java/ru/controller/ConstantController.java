package ru.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.constant.OrderStatus;
import ru.constant.UserRole;

@RestController
@RequestMapping("/constants")
public class ConstantController {

    @JsonView(DataTablesOutput.View.class)
    @RequestMapping(value = "/userRoles", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public UserRole[] getUserRoles() {
        return UserRole.values();
    }


    @RequestMapping(value = "/orderStatuses", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public OrderStatus[] getRequestStatuses() {
        return OrderStatus.values();
    }




    @GetMapping(value = "/requirements", produces = "application/json; charset=UTF-8")
    public String[] getRequirements(){
        return new String[]{"Медицинская карта",
                "Светоотражающий жилет",
                "Ботинки с защитным носком",
                "GPS",
                "Мобильное приложение",
                "Отслеживание по номеру телефона",
                "Тип погрузки - боковая",
                "Тип погрузки - задняя",
                "Коники",
                "Гидробот"};
    }
}
