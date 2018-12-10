package ru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.dao.entity.User;

@Controller
@RequestMapping("/test")
public class TestController {
    @GetMapping("/upload")
    public String testUpload(){
        return "test/upload";
    }

    @GetMapping("/")
    public String testPage() {return "test/test";}

    @GetMapping("/crop")
    public String testCrop(){
        return "test/crop";
    }

    @GetMapping("/code")
    public String testCodePage(ModelMap map) {
        map.addAttribute("user", new User());
        return "code";
    }
}
