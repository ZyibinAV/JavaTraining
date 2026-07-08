package com.homeapp.javatraining.dto;

public record PasswordChangeRequest(
        String currentPassword,
        String newPassword,
        String confirmPassword
) {
}
