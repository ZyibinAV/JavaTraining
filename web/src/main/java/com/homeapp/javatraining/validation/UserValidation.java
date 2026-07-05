package com.homeapp.javatraining.validation;

import com.homeapp.javatraining.exception.ValidationException;

import java.util.regex.Pattern;

public class UserValidation {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]+$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern USERNAME_PATTERN = Pattern.compile(USERNAME_REGEX);

    public void validateRegistration(String username, String password, String email) {
        validateUsername(username);
        validatePassword(password);
        validateEmail(email);
    }

    public void validateLogin(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw ValidationException.username("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw ValidationException.password("Password cannot be empty");
        }
    }

    public void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw ValidationException.username("Username cannot be empty");
        }
        String trimmedUsername = username.trim();
        if (trimmedUsername.length() < MIN_USERNAME_LENGTH) {
            throw ValidationException.username(String.format("Username must be at least %d characters", MIN_USERNAME_LENGTH));
        }
        if (trimmedUsername.length() > MAX_USERNAME_LENGTH) {
            throw ValidationException.username(String.format("Username must be at most %d characters", MAX_USERNAME_LENGTH));
        }
        if (!USERNAME_PATTERN.matcher(trimmedUsername).matches()) {
            throw ValidationException.username("Username can only contain letters, numbers, underscores and hyphens");
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw ValidationException.password("Password cannot be empty");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw ValidationException.password(
                    String.format("Password must be at least %d characters", MIN_PASSWORD_LENGTH)
            );
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw ValidationException.password(
                    String.format("Password must be no more than %d characters", MAX_PASSWORD_LENGTH)
            );
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw ValidationException.email("Email cannot be empty");
        }

        String trimmedEmail = email.trim();

        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw ValidationException.email("Invalid email format");
        }

        if (trimmedEmail.length() > 100) {
            throw ValidationException.email("Email is too long");
        }
    }

    public void validateProfile(String nickname, String about) {
        if (nickname != null && nickname.length() > 50) {
            throw ValidationException.general("Nickname is too long (max 50 characters)");
        }
        if (about != null && about.length() > 500) {
            throw ValidationException.general("About section is too long (max 500 characters)");
        }
    }
}
