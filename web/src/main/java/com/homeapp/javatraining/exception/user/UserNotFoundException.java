package com.homeapp.javatraining.exception.user;

import com.homeapp.javatraining.exception.ApiException;

public class UserNotFoundException extends ApiException {

    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " not found");
    }
}
