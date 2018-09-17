package ru.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.dao.repository.DriverRepository;
import ru.dao.repository.FileRepository;
import ru.dao.repository.TransportRepository;
import ru.service.StorageService;

@Controller
@RequestMapping("/upload")
public class UploadController {
    private final DriverRepository driverRepository;
    private final TransportRepository transportRepository;
    private final FileRepository fileRepository;
    private final StorageService storageService;



    public UploadController(DriverRepository driverRepository, TransportRepository transportRepository, FileRepository fileRepository, StorageService storageService) {
        this.driverRepository = driverRepository;
        this.transportRepository = transportRepository;
        this.fileRepository = fileRepository;
        this.storageService = storageService;
    }

    @PostMapping("/test")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws Exception{
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @PostMapping("/driver/{driverId}")
    public String uploadDriverDocument(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @PathVariable Integer driverId) {

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @PostMapping("/transport/{transportId}")
    public String uploadTransportDocument(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, @PathVariable Integer transportId){
        return "";
    }

    @GetMapping("/test")
    public String testUpload(ModelMap map){
        return "upload";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
