package com.pollub.cookie.validator;

import com.pollub.cookie.exception.FileSizeLimitExceededException;
import com.pollub.cookie.exception.InvalidFileTypeException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class FileValidator {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("Przesłany plik nie jest obrazem.");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new InvalidFileTypeException("Przesłany plik nie jest obrazem.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeLimitExceededException("Rozmiar pliku przekracza dopuszczalny limit 5 MB.");
        }
    }
}

