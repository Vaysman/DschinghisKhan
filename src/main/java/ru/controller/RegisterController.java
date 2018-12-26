package ru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.configuration.authentication.CustomPersistentRememberMeService;
import ru.dao.entity.User;
import ru.dto.json.user.UserRegistrationData;
import ru.service.RegisterService;
import ru.service.SendPasswordStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller

//@RequestMapping("/registerDispatcher")
public class RegisterController {

    private final RegisterService registerService;
    private final CustomPersistentRememberMeService rememberMeService;

    @Autowired
    public RegisterController(
            RegisterService registerService,
            CustomPersistentRememberMeService rememberMeService) {
        this.registerService = registerService;
        this.rememberMeService = rememberMeService;
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
    public String resendCode(@ModelAttribute User user){
        return "code";
    }


    @PostMapping("/resendPassword/{userId}")
    @ResponseBody
    public String resendPassword(@PathVariable Integer userId, @RequestBody Map<String, String> params) throws Exception{
        registerService.resendPassword(userId, params.get("email"), SendPasswordStrategy.FORGOT_PASSWORD);
        return "ok";
    }

    @PostMapping("/code")
    public String enterCode(@ModelAttribute User user, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap){
        try{
            Authentication successfulAuthentication = registerService.authorize(user.getUsername(), request.getParameter("password").toUpperCase());
            rememberMeService.loginSuccess(request,response, successfulAuthentication);
            return "redirect:/main";
        } catch (Exception e ){
            user.setLogin(user.getUsername());
            modelMap.addAttribute("error",e.getMessage());
            modelMap.addAttribute("user",user);
            return "code";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registrationData",new UserRegistrationData());
        return "register";
    }
}
