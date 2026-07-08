package com.homeapp.javatraining.dto;

import java.time.LocalDateTime;

public record ProfileResponse(
        Long id,
        String username,
        String email,
        String nickname,
        String about,
        String avatarPath,
        String role,
        LocalDateTime createdAt
) {
}
