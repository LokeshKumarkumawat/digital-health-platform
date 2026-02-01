package com.digitalhealth.platform.common.storage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/files/profile-pictures")
public class FileAccessController {

    private final FileStorageProperties properties;

    public FileAccessController(FileStorageProperties properties) {
        this.properties = properties;
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename)
            throws MalformedURLException {

        Path filePath = Paths.get(properties.getLocation())
                .resolve(filename)
                .normalize();

        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}