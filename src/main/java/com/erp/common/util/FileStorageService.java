package com.erp.common.util;

import com.erp.common.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Stores uploaded files under the configured upload directory, returning a web-accessible
 * path ({@code /uploads/...}) that is served as a static resource.
 */
@Slf4j
@Service
public class FileStorageService {

    private final Path rootLocation;

    public FileStorageService(@Value("${erp.upload.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new BusinessException("Could not initialize upload directory");
        }
    }

    public String store(MultipartFile file, String subFolder) {
        try {
            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
            String ext = StringUtils.getFilenameExtension(original);
            String filename = UUID.randomUUID() + (ext != null ? "." + ext : "");
            Path target = rootLocation.resolve(subFolder);
            Files.createDirectories(target);
            Path destination = target.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + subFolder + "/" + filename;
        } catch (IOException e) {
            throw new BusinessException("Failed to store file: " + e.getMessage());
        }
    }

    public Path getRootLocation() {
        return rootLocation;
    }
}
