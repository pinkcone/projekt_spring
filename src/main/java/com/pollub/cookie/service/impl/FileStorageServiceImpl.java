package com.pollub.cookie.service.impl;

import com.pollub.cookie.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    public String saveFile(MultipartFile file) throws IOException {

        String uploadDir = "uploads/images/";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            try {
                uploadDirFile.mkdirs();
            } catch (Exception e) {
                logger.error("Nie udalo sie utworzyć folderu");
            }
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String newFileName = UUID.randomUUID() + "." + fileExtension;


        Path filePath = Paths.get(uploadDir, newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return newFileName;
    }

    public String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        String extension = "";

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = fileName.substring(dotIndex + 1);
        }
        return extension;
    }
}
