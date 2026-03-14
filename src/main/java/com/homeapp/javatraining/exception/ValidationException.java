package com.homeapp.javatraining.exception;

public class ValidationException extends RuntimeException {

    private final String field;
    private final String errorCode;

    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
        this.errorCode = "VALIDATION_ERROR";
    }

    public ValidationException(String message, String field, String errorCode) {
        super(message);
        this.field = field;
        this.errorCode = errorCode;
    }

    public String getField() {
        return field;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static ValidationException username(String message) {
        return new ValidationException(message, "username", "USERNAME_INVALID");
    }

    public static ValidationException password(String message) {
        return new ValidationException(message, "password", "PASSWORD_INVALID");
    }

    public static ValidationException email(String message) {
        return new ValidationException(message, "email", "EMAIL_INVALID");
    }

    public static ValidationException general(String message) {
        return new ValidationException(message, "general", "GENERAL_ERROR");
    }
}