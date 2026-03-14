package com.homeapp.javatraining.exception;

public class AuthenticationException extends RuntimeException {

    private final String reason;

    public AuthenticationException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid login or password", "INVALID_CREDENTIALS");
    }

    public static AuthenticationException userBlocked() {
        return new AuthenticationException("User is blocked", "USER_BLOCKED");
    }

    public static AuthenticationException userNotFound() {
        return new AuthenticationException("User not found", "USER_NOT_FOUND");
    }
}
