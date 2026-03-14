package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.util.ValidationFactory;
import com.homeapp.javatraining.validation.UserValidation;

public class RegistrationService {

    private final UserService userService;
    private final UserValidation userValidator;

    public RegistrationService(UserService userService) {
        this.userService = userService;
        this.userValidator = ValidationFactory.createUserValidator();
    }

    public User registerUser(String username, String password, String email) {
        userValidator.validateRegistration(username, password, email);

        return userService.register(username, password, email);
    }
}
