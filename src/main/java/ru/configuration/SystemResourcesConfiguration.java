package ru.configuration;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Path;

@Configuration
@Getter
public class SystemResourcesConfiguration {
    private static final Logger logger = LoggerFactory.getLogger("main");

    @Autowired
    public SystemResourcesConfiguration() {

    }

    private void validatePathIsDirectory(Path path) {
        if (!path.toFile().exists()) {
            final String error = String.format("directory %s is not exist", path.toString());
            logger.error(error);
            throw new IllegalStateException(error);
        }
        if (!path.toFile().isDirectory()) {
            final String errorMessage = String.format("path %s must be a directory", path.toString());
            logger.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

}
