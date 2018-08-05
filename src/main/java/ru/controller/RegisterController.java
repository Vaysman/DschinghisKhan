package ru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.dao.entity.User;
import ru.dto.json.user.UserRegistrationData;
import ru.service.UserManagementService;

import java.util.Map;

@Controller
//@RequestMapping("/register")
public class RegisterController {

    private final UserManagementService userManagementService;

    @Autowired
    public RegisterController(
            UserManagementService userManagementService
    ) {
        this.userManagementService = userManagementService;
    }

    @PostMapping("/register")
    public String process(@ModelAttribute UserRegistrationData registrationData, Model model) {
        Map<String, String> errors = registrationData.check();

        if (errors.size() == 0) {
            try {
                User registeredUser =userManagementService.register(registrationData);
                if(registeredUser!=null) {
                    userManagementService.setAuthorized(registeredUser, registrationData.getPassword());
                    return "redirect:/profile";
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

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationData",new UserRegistrationData());
        return "register";
    }
}
