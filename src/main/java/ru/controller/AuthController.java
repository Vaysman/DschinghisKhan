package ru.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@Transactional(propagation = Propagation.REQUIRED)
public class AuthController {

    @Autowired
    ResourceLoader resourceLoader;

    @GetMapping("/")
    public String root() {
        return "main";
    }

    @GetMapping("/index")
    public String index() {
        return "main";
    }


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
