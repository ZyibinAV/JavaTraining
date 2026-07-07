package com.homeapp.javatraining.dto;

public record RegisterRequest(
        String username,
        String password,
        String email
) {
}
