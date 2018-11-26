package ru.controller;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.configuration.authentication.AuthToken;
import ru.dao.entity.*;
import ru.dao.repository.*;
import ru.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class UploadController {
    private final DriverRepository driverRepository;
    private final TransportRepository transportRepository;
    private final FileRepository fileRepository;
    private final OrderRepository orderRepository;
    private final StorageService storageService;
    private final CompanyRepository companyRepository;
    private final ContractRepository contractRepository;



    @Autowired
    public UploadController(DriverRepository driverRepository, TransportRepository transportRepository, FileRepository fileRepository, OrderRepository orderRepository, StorageService storageService, CompanyRepository companyRepository, ContractRepository contractRepository) {
        this.driverRepository = driverRepository;
        this.transportRepository = transportRepository;
        this.fileRepository = fileRepository;
        this.orderRepository = orderRepository;
        this.storageService = storageService;
        this.companyRepository = companyRepository;
        this.contractRepository = contractRepository;
    }

    @PostMapping("/test")
    public String upload(@RequestParam("file") MultipartFile file, @RequestParam String fileName, ModelMap map) throws Exception{
        StoredFile newfile = storageService.store(file, fileName);
        map.addAttribute("message","You successfully uploaded " + file.getOriginalFilename() + "!");
        return "upload";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value ="/driver/{driverId}",produces = "application/json; charset=UTF-8")
    public void uploadDriverDocument(@RequestParam("file") MultipartFile file, @RequestParam String fileName, @PathVariable Integer driverId) throws Exception {
        Driver driver = driverRepository.findById(driverId).orElseThrow(()->new IllegalArgumentException("Данный водитель не существует"));
        driver.getFiles().add(storageService.store(file,fileName));
        driverRepository.save(driver);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value= "/transport/{transportId}", produces = "application/json; charset=UTF-8")
    public void uploadTransportDocument(@RequestParam("file") MultipartFile file, @RequestParam String fileName, @PathVariable Integer transportId) throws Exception {
        Transport transport = transportRepository.findById(transportId).orElseThrow(()->new IllegalArgumentException("Данный транспорт не существует"));
        transport.getFiles().add(storageService.store(file,fileName));
        transportRepository.save(transport);
    }

    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    @PostMapping(value = "/driverPhoto/{driverId}", produces = "application/json; charset=UTF-8")
    public void uploadDriverPhoto(@RequestParam("file") MultipartFile file, @PathVariable Integer driverId) throws Exception{
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new IllegalArgumentException("Данный водитель не существует"));
        if (driver.getPhoto()!=null){
            StoredFile previousFile = driver.getPhoto();
            driver.setPhoto(null);
            driverRepository.save(driver);
            storageService.delete(previousFile);
        }
        driver.setPhoto(storageService.store(file,driver.getId().toString()+"-photo"));
        driverRepository.save(driver);
    }

    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    @PostMapping(value = "/driverLicense/{driverId}", produces = "application/json; charset=UTF-8")
    public void uploadDriverLicense(@RequestParam("file") MultipartFile file, @PathVariable Integer driverId) throws Exception{
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new IllegalArgumentException("Данный водитель не существует"));
        if (driver.getLicense()!=null){
            StoredFile previousFile = driver.getLicense();
            driver.setLicense(null);
            driverRepository.save(driver);
            storageService.delete(previousFile);
        }
        driver.setLicense(storageService.store(file,driver.getId().toString()+"-license"));
        driverRepository.save(driver);
    }

    @PreAuthorize("hasAuthority('TRANSPORT_COMPANY')")
    @PostMapping(value = "/driverPassport/{driverId}", produces = "application/json; charset=UTF-8")
    public void uploadDriverPassport(@RequestParam("file") MultipartFile file, @PathVariable Integer driverId) throws Exception{
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new IllegalArgumentException("Данный водитель не существует"));
        if (driver.getPassport()!=null){
            StoredFile previousFile = driver.getPassport();
            driver.setPassport(null);
            driverRepository.save(driver);
            storageService.delete(previousFile);
        }
        driver.setPassport(storageService.store(file,driver.getId().toString()+"photo"));
        driverRepository.save(driver);
    }

    @PreAuthorize("hasAuthority('DISPATCHER')")
    @PostMapping(value= "/contract/{companyId}", produces = "application/json; charset=UTF-8")
    public void uploadContract(@RequestParam("file") MultipartFile file, @RequestParam String fileName, @PathVariable Integer companyId) throws Exception {
        Contract contract = new Contract();
        AuthToken authentication = (AuthToken) SecurityContextHolder.getContext().getAuthentication();
        Company dispatcherCompany = companyRepository.findById(authentication.getCompanyId()).orElseThrow(()->new IllegalArgumentException("Данной компании не существует"));
        Company transportCompany = companyRepository.findById(companyId).orElseThrow(()->new IllegalArgumentException("Данной компании не существует"));
        contract.setCompany(transportCompany);
        contract.setInitiativeCompany(dispatcherCompany);
        contract.setFile(storageService.store(file,fileName));
        contractRepository.save(contract);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/order/{orderId}", produces = "application/json; charset=UTF-8")
    public void uploadOrderDocument(@RequestParam("file") MultipartFile file, @RequestParam String fileName, @PathVariable Integer orderId) throws Exception {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new IllegalArgumentException("Данная заявка не существует"));
        order.getFiles().add(storageService.store(file,fileName));
        orderRepository.save(order);
    }



    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getFile/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
            Resource file = storageService.loadAsResource(filename);
            String mimeType = "";
            MimeType mimeType1 = (MimeType) MimeUtil.getMimeTypes(file.getFile()).stream().findFirst().orElse(null);
            if (mimeType1 != null) {
                mimeType = mimeType1.toString();
            }
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, mimeType)
                    .body(file);
    }


}
