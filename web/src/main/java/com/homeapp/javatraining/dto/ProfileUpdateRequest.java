package com.homeapp.javatraining.dto;

public record ProfileUpdateRequest(
        String nickname,
        String about
) {
}
