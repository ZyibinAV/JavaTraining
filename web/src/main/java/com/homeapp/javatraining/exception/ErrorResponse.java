package com.homeapp.javatraining.exception;

public record ErrorResponse(
        int status,
        String error,
        String message,
        String field
) {
}
