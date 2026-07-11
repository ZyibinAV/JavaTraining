package com.homeapp.javatraining.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9_-]+$")
        String username,

        @NotBlank
        @Size(min = 6, max = 100)
        String password,

        @NotBlank
        @Size(max = 100)
        @Email
        String email
) {
}
