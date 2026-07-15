package com.homeapp.javatraining.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "svg", "webp");
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final String UPLOAD_PREFIX = "/uploads/avatars/";
    private static final int PRESET_COUNT = 12;

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @PostConstruct
    public void initBucket() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("Created MinIO bucket: {}", bucket);
            } else {
                log.info("MinIO bucket already exists: {}", bucket);
            }
        } catch (Exception e) {
            log.warn("MinIO not available (bucket init skipped): {}", e.getMessage());
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
        try (InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.info("Avatar saved to MinIO: {} for user {}", filename, userId);
            return UPLOAD_PREFIX + filename;
        } catch (Exception e) {
            log.error("Failed to save avatar to MinIO for user {}", userId, e);
            throw new RuntimeException("Failed to save avatar file", e);
        }
    }

    public void deleteAvatar(String avatarPath) {
        if (!isUploadedAvatar(avatarPath)) {
            return;
        }
        String filename = avatarPath.substring(UPLOAD_PREFIX.length());
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(filename)
                    .build());
            log.info("Deleted avatar from MinIO: {}", filename);
        } catch (Exception e) {
            log.warn("Failed to delete avatar from MinIO: {}", filename, e);
        }
    }

    public InputStream loadAvatar(String filename) {
        try {
            return minioClient.getObject(
                    io.minio.GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(filename)
                            .build()
            );
        } catch (Exception e) {
            log.warn("Failed to load avatar from MinIO: {}", filename, e);
            return null;
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
