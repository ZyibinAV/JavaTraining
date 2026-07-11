package com.homeapp.javatraining.dto;

import com.homeapp.javatraining.model.Role;

import java.time.LocalDateTime;

public record UserDTO(
        Long id,
        String username,
        String email,
        String nickname,
        String about,
        String avatarPath,
        Role role,
        LocalDateTime createdAt,
        boolean blocked) {
}
