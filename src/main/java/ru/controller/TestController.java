package ru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import ru.dao.entity.Company;
import ru.dao.entity.User;
import ru.service.RegisterService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/test")
public class TestController {

    private final RegisterService registerService;

    public TestController(RegisterService registerService) {
        this.registerService = registerService;
    }

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

    @PostMapping("/uploadCompanies")
    @Transactional(propagation = Propagation.REQUIRED)
    @ResponseBody
    public Map<String, Map<String,String>> uploadCompanies(@RequestBody ArrayList<Company> companiesRegisterData){
        Map<String, Map<String,String>> registeredCompaniesMap = new HashMap<>();

        for(Company company :companiesRegisterData){
            Map<String,String> companyData = registerService.registerTransportCompanyProgrammatically(company);
            registeredCompaniesMap.put(companyData.get("companyName"),companyData);
        }

        return registeredCompaniesMap;

    }
}
