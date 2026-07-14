package com.homeapp.javatraining.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvatarMigrationService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @PostConstruct
    public void init() {
        ensureBucketExists();
        migrateExistingAvatars();
    }

    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created MinIO bucket: {}", bucket);
            } else {
                log.info("MinIO bucket already exists: {}", bucket);
            }
        } catch (Exception e) {
            log.warn("Could not verify/create MinIO bucket (MinIO may not be running): {}", e.getMessage());
        }
    }

    private void migrateExistingAvatars() {
        Path uploadDir = findAvatarDirectory();
        if (uploadDir == null) {
            log.info("No local avatar directory found, skipping migration");
            return;
        }

        List<Path> files;
        try (Stream<Path> stream = Files.list(uploadDir)) {
            files = stream.filter(Files::isRegularFile).toList();
        } catch (IOException e) {
            log.warn("Failed to list local avatar files for migration: {}", e.getMessage());
            return;
        }

        if (files.isEmpty()) {
            log.info("No local avatar files to migrate");
            return;
        }

        log.info("Found {} local avatar file(s) to migrate to MinIO", files.size());
        int migrated = 0;
        int failed = 0;

        for (Path filePath : files) {
            String filename = filePath.getFileName().toString();
            try {
                byte[] content = Files.readAllBytes(filePath);
                String contentType = probeContentType(filename);
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(filename)
                        .stream(new ByteArrayInputStream(content), content.length, -1)
                        .contentType(contentType)
                        .build());
                log.info("Migrated avatar: {}", filename);
                migrated++;
            } catch (Exception e) {
                log.warn("Failed to migrate avatar {}: {}", filename, e.getMessage());
                failed++;
            }
        }

        log.info("Avatar migration complete: {} migrated, {} failed", migrated, failed);
    }

    private Path findAvatarDirectory() {
        String userDir = System.getProperty("user.dir");
        Path[] candidates = {
                Paths.get(userDir, "uploads", "avatars"),
                Paths.get(userDir, "..", "uploads", "avatars").normalize()
        };
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private String probeContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".webp")) return "image/webp";
        return "application/octet-stream";
    }
}
