package ru.controller;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.configuration.authentication.AuthToken;
import ru.constant.*;

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

    @RequestMapping(value = "/orders")
    public String orders(ModelMap model) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        AuthToken authentication=(AuthToken)securityContext.getAuthentication();
        model.addAttribute("currentUser",authentication.getUser());
        model.addAttribute("requirements", OrderRequirements.values());
        model.addAttribute("orderObligations", OrderObligation.values());

        return "/orders";
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

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException() {
        return "error/404";
    }
}
