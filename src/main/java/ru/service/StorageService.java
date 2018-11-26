package ru.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.dao.entity.StoredFile;

import java.nio.file.Path;

public interface StorageService {
    void init();

    //returns new file name
    StoredFile store(MultipartFile file, String name) throws Exception;

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

    void delete(StoredFile file);
}
