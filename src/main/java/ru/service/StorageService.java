package ru.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    void init();

    //returns new file name
    String store(MultipartFile file) throws Exception;

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();
}
