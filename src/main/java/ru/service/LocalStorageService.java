package ru.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.util.generator.RandomStringGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class LocalStorageService implements StorageService {

    @Value("${upload-directory}")
    private String uploadDirectory;

    @Override
    public void init() {

    }

    @Override
    public String store(MultipartFile file) throws IOException {
        String fileName = RandomStringGenerator.randomAlphaNumeric(32)+"."+FilenameUtils.getExtension(file.getOriginalFilename());
        file.transferTo(new File(uploadDirectory+fileName));
        System.out.println("Stored file: "+fileName);
        return fileName;
    }

    @Override
    public Path load(String filename) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        Resource resource = new FileSystemResource(new File(uploadDirectory+filename));
        return resource;
    }

    @Override
    public void deleteAll() {

    }
}
