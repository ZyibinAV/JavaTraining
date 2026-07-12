package com.homeapp.javatraining.service;

import com.homeapp.javatraining.exception.ValidationException;
import com.homeapp.javatraining.exception.user.DuplicateEmailException;
import com.homeapp.javatraining.exception.user.DuplicateUsernameException;
import com.homeapp.javatraining.exception.user.InvalidCredentialsException;
import com.homeapp.javatraining.exception.user.UserNotFoundException;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(String username, String rawPassword, String email) {
        log.info("User registration attempt: username={}", username);
        checkUsernameAvailability(username);
        checkEmailAvailability(email);
        Role role = determineUserRole();
        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = new User(username, passwordHash, email, role);
        userRepository.save(user);
        log.info("User registered successfully: id={}, username={}, role={}",
                user.getId(), user.getUsername(), user.getRole());
        return user;
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, String nickname, String about) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (nickname != null) {
            user.setNickname(nickname.isBlank() ? null : nickname);
        }
        if (about != null) {
            user.setAbout(about.isBlank() ? null : about);
        }
        log.info("User {} updated profile: nickname={}", userId, nickname);
        return user;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("confirmPassword", "New password and confirmation do not match");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        log.info("User {} changed password", userId);
    }

    private void checkUsernameAvailability(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    log.warn("Registration failed: username {} already exists", username);
                    throw new DuplicateUsernameException(username);
                });
    }

    private void checkEmailAvailability(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    log.warn("Registration failed: email {} already exists", email);
                    throw new DuplicateEmailException(email);
                });
    }

    private Role determineUserRole() {

        if (userRepository.findFirstBy().isEmpty()) {
            log.info("First user registration - assigning ADMIN role");
            return Role.ADMIN;
        }

        return Role.USER;
    }

    @Override
    @Transactional(readOnly = true)
    public User getProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
