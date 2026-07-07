package com.homeapp.javatraining.dto;

public record LoginRequest(
        String username,
        String password
) {
}
