package com.homeapp.javatraining.exception.user;

import com.homeapp.javatraining.exception.ApiException;

public class InvalidRefreshTokenException extends ApiException {
    public InvalidRefreshTokenException() {
        super("Invalid or expired refresh token");
    }
}
