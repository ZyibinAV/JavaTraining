package com.homeapp.javatraining.exception.user;

import com.homeapp.javatraining.exception.ApiException;

public class DuplicateEmailException extends ApiException {

    public DuplicateEmailException(String email) {
        super("Email '" + email + "' is already in use");
    }
}
