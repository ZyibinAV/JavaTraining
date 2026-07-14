package com.homeapp.javatraining.exception.user;

import com.homeapp.javatraining.exception.ApiException;

public class UserBlockedException extends ApiException {

    public UserBlockedException() {
        super("User is blocked");
    }
}
