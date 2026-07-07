package com.homeapp.javatraining.dto;

import com.homeapp.javatraining.model.Role;

public record AuthResponse(
        String token,
        Long userId,
        String username,
        Role role
) {

}
