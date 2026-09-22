package com.digitalhealth.platform.common.storage;

import com.digitalhealth.platform.common.exception.BadRequestException;
import com.digitalhealth.platform.common.exception.InternalServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/jpg"
    );

    private final FileStorageProperties properties;

    public String storeFile(MultipartFile file) {

        validateFile(file);

        try {
            Path uploadPath = Paths.get(properties.getLocation()).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = getFileExtension(originalFilename);

            String fileName = UUID.randomUUID() + extension;
            Path targetLocation = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = properties.getBaseUrl()
                    + "/files/profile-pictures/"
                    + fileName;

            log.info("File stored successfully at {}", targetLocation);

            return fileUrl;

        } catch (IOException ex) {
            log.error("File storage failed", ex);
            throw new InternalServerException("Failed to store file");
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only JPG and PNG images are allowed");
        }
    }

    private String getFileExtension(String filename) {

        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0) ? filename.substring(dotIndex) : "";
    }
}