package ru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.dao.entity.User;
import ru.dto.json.user.UserRegistrationData;
import ru.service.RegisterService;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller

//@RequestMapping("/registerDispatcher")
public class RegisterController {

    private final RegisterService registerService;

    @Autowired
    public RegisterController(
            RegisterService registerService
    ) {
        this.registerService = registerService;
    }

    @PostMapping("/register")
    public String process(@ModelAttribute UserRegistrationData registrationData, Model model,HttpServletResponse response) {
        Map<String, String> errors = registrationData.check();

        if (errors.size() == 0) {
            try {
                User registeredUser = registerService.registerDispatcher(registrationData);
                if(registeredUser!=null) {
                    model.addAttribute("user",registeredUser);
//                    registerService.setAuthorized(registeredUser, registrationData.getPassword());
                    return "code";
                } else {
                    model.addAttribute("error","Непредвиденная ошибка. Запишите введенные данные и свяжитесь с поддержкой");
                    model.addAttribute("registrationData", registrationData);
                    return "register";
                }
            } catch (Exception exception) {
                model.addAttribute("registrationData", registrationData);
                model.addAttribute("error", exception.getMessage());
                return "register";
            }
        } else {
            model.addAllAttributes(errors);
            model.addAttribute("registrationData", registrationData);
            return "register";
        }
    }

    @PostMapping("/resendCode")
    @ResponseBody
    public String enterCode(@ModelAttribute User user){

        return "code";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationData",new UserRegistrationData());
        return "register";
    }
}
