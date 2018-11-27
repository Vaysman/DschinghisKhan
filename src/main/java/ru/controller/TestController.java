package ru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
