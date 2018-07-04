package ru.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.configuration.authentication.AuthToken;
import ru.constant.CompanyType;
import ru.constant.LoadingType;
import ru.constant.UserRole;
import ru.constant.VehicleType;

@Controller
public class AuthController {

    @GetMapping("/")
    public String root() {
        return "/main";
    }

    @GetMapping("/index")
    public String index() {
        return "/main";
    }

    @RequestMapping("/main")
    public String main(ModelMap model)
    {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        AuthToken authentication=(AuthToken)securityContext.getAuthentication();
        model.addAttribute("currentUser",authentication.getUser());
        model.addAttribute("userRoles", UserRole.values());
        model.addAttribute("companyTypes", CompanyType.values());
        model.addAttribute("loadingTypes", LoadingType.values());
        model.addAttribute("vehicleTypes", VehicleType.values());
        return "/main";
    }

    @GetMapping("/routes")
    public String routes() { return "/routes";}

    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String postLogin() {
        // TODO Enable form login with Spring Security (trigger error for now)
        return "redirect:/login-error";
    }

    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }
}
