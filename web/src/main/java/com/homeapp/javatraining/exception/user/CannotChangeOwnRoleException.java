package com.homeapp.javatraining.exception.user;

import com.homeapp.javatraining.exception.ApiException;

public class CannotChangeOwnRoleException extends ApiException {

    public CannotChangeOwnRoleException() {
        super("You cannot change your own role");
    }
}
