package com.homeapp.javatraining.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Service
public class AvatarService {

    private static final Logger log = LoggerFactory.getLogger(AvatarService.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "svg", "webp");
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final String UPLOAD_PREFIX = "/uploads/avatars/";
    private static final int PRESET_COUNT = 12;

    private final Path uploadDir;

    public AvatarService(@Value("${app.avatar.upload-dir:uploads/avatars}") String uploadDirConfig) {
        Path configured = Paths.get(uploadDirConfig);
        this.uploadDir = configured.isAbsolute() ? configured : Paths.get(System.getProperty("user.dir")).resolve(uploadDirConfig).normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadDir);
            log.info("Avatar upload directory initialized: {}", uploadDir.toAbsolutePath());
        } catch (IOException e) {
            log.error("Could not create avatar upload directory: {}", uploadDir.toAbsolutePath(), e);
        }
    }

    public List<String> getPresetAvatars() {
        return IntStream.rangeClosed(1, PRESET_COUNT)
                .mapToObj(i -> "/img/avatars/" + i + ".svg")
                .toList();
    }

    public boolean isUploadedAvatar(String avatarPath) {
        return avatarPath != null && avatarPath.startsWith(UPLOAD_PREFIX);
    }

    public String saveAvatar(MultipartFile file, Long userId) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 2MB");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        String extension = extractExtension(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("File type not allowed. Supported: " + ALLOWED_EXTENSIONS);
        }

        String filename = "avatar_" + userId + "_" + System.currentTimeMillis() + "." + extension;
        Path targetPath = uploadDir.resolve(filename);

        try {
            file.transferTo(targetPath.toFile());
            log.info("Avatar saved: {} for user {}", targetPath, userId);
            return UPLOAD_PREFIX + filename;
        } catch (IOException e) {
            log.error("Failed to save avatar for user {}", userId, e);
            throw new RuntimeException("Failed to save avatar file", e);
        }
    }

    public void deleteAvatar(String avatarPath) {
        if (!isUploadedAvatar(avatarPath)) {
            return;
        }
        String filename = avatarPath.substring(UPLOAD_PREFIX.length());
        Path filePath = uploadDir.resolve(filename);
        try {
            Files.deleteIfExists(filePath);
            log.info("Deleted avatar file: {}", filePath);
        } catch (IOException e) {
            log.warn("Failed to delete avatar file: {}", filePath, e);
        }
    }

    private String extractExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == filename.length() - 1) {
            return null;
        }
        return filename.substring(dotIndex + 1).toLowerCase();
    }
}
