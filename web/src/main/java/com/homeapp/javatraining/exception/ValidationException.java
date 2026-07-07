package com.homeapp.javatraining.exception;

import com.homeapp.javatraining.exception.ApiException;

public class ValidationException extends ApiException {

    private final String field;

    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}