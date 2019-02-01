package ru.controller;

import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import ru.configuration.authentication.AuthToken;
import ru.constant.UserRole;
import ru.service.GeocodingService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Transactional(propagation = Propagation.REQUIRED)
public class AuthController {



    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    GeocodingService geoService;


    @RequestMapping(value = "/login")
    public String login() {
        return "login";
    }


    @RequestMapping("/successfulLogin")
    public String successfulLoginHandler(){
        SecurityContext securityContext = SecurityContextHolder.getContext();
        AuthToken authentication = (AuthToken) securityContext.getAuthentication();

        if(authentication.getUser().getHasAcceptedCookies()){
            if (authentication.getUser().getUserRole().equals(UserRole.ROLE_DISPATCHER)){
                return "redirect:/main#tab-dashboard";
            } else {
                return "redirect:/orders";
            }

        } else {
            return "redirect:/main";
        }


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

    @RequestMapping("/geocode-test/{address}")
    @ResponseBody
    public GeocodingResult geocodeTest(@PathVariable("address") String address) {
        try{
            GeocodingResult[] results = geoService.getAddressCoordinates(address);
            System.out.println(results.toString());
            return results[0];
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/pdf-info")
    public void pdfInfo(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline;filename=Kurulway.pdf");

        FileCopyUtils.copy(resourceLoader.getResource("classpath:Kurulway.pdf").getInputStream(),response.getOutputStream());
    }




    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException() {
        return "error/404";
    }


}
