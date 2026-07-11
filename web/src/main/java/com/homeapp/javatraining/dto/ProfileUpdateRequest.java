package com.homeapp.javatraining.dto;

import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @Size(max = 50)
        String nickname,

        @Size(max = 500)
        String about
) {
}
