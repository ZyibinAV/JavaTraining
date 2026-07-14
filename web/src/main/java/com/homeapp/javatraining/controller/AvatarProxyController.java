package com.homeapp.javatraining.controller;

import com.homeapp.javatraining.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Controller
@RequiredArgsConstructor
public class AvatarProxyController {

    private final AvatarService avatarService;

    @GetMapping("/uploads/avatars/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        InputStream inputStream = avatarService.loadAvatar(filename);
        if (inputStream == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = resolveMediaType(filename);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic())
                .body(new InputStreamResource(inputStream));
    }

    private MediaType resolveMediaType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (filename.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (filename.endsWith(".gif")) return MediaType.IMAGE_GIF;
        if (filename.endsWith(".svg")) return MediaType.valueOf("image/svg+xml");
        if (filename.endsWith(".webp")) return MediaType.valueOf("image/webp");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
