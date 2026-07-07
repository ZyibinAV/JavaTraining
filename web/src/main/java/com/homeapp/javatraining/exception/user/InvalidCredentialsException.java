package com.homeapp.javatraining.exception.user;

import com.homeapp.javatraining.exception.ApiException;

public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException() {
        super("Invalid username or password");
    }
}
