package com.homeapp.javatraining.service;

import com.homeapp.javatraining.exception.AuthenticationException;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import com.homeapp.javatraining.util.PasswordUtil;
import com.homeapp.javatraining.util.ValidationFactory;
import com.homeapp.javatraining.validation.UserValidation;

import java.util.Optional;

public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserValidation userValidator;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.userValidator = ValidationFactory.createUserValidator();
    }

    public User authenticate(String username, String password) {
        userValidator.validateLogin(username, password);

        Optional<User> userOptional = userRepository.findByUserName(username);
        if (userOptional.isEmpty()) {
            throw AuthenticationException.userNotFound();
        }

        User user = userOptional.get();

        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            throw AuthenticationException.invalidCredentials();
        }

        if (user.isBlocked()) {
            throw AuthenticationException.userBlocked();
        }

        return user;
    }
}
