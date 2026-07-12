package com.homeapp.javatraining.exception.user;

import com.homeapp.javatraining.exception.ApiException;

public class CannotBlockSelfException extends ApiException {
    public CannotBlockSelfException() {
        super("Cannot block self");
    }
}
