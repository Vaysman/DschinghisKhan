package ru.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.dao.entity.StoredFile;
import ru.dao.repository.FileRepository;
import ru.util.generator.RandomStringGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Service
@Primary
public class LocalStorageService implements StorageService {

    @Value("${upload-directory}")
    private String uploadDirectory;

    private final FileRepository fileRepository;

    @Autowired
    LocalStorageService(FileRepository fileRepository){
        this.fileRepository = fileRepository;
    }

    @Override
    public void init() {

    }

    @Override
    public StoredFile store(MultipartFile file, String name) throws IOException {
        StoredFile storedFile = new StoredFile();
        storedFile.setFileName(name);
        String fileName = RandomStringGenerator.randomAlphaNumeric(32)+"."+FilenameUtils.getExtension(file.getOriginalFilename());
        storedFile.setPath(fileName);
        file.transferTo(new File(uploadDirectory+fileName));
        System.out.println("Stored file: "+fileName);
        fileRepository.save(storedFile);
        return storedFile;
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

    @Override
    public void delete(StoredFile file) {
        fileRepository.delete(file);
        new File(uploadDirectory+file.getPath()).delete();
    }
}
