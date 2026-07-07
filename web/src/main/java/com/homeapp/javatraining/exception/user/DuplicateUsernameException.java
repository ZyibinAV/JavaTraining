package com.homeapp.javatraining.exception.user;

import com.homeapp.javatraining.exception.ApiException;

public class DuplicateUsernameException extends ApiException {

    public DuplicateUsernameException(String username) {
        super("Username '" + username + "' is already in use");
    }
}
