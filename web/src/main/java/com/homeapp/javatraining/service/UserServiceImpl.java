package com.homeapp.javatraining.service;

import com.homeapp.javatraining.exception.user.DuplicateEmailException;
import com.homeapp.javatraining.exception.user.DuplicateUsernameException;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(String username, String rawPassword, String email) {

        log.info("User registration attempt: username={}", username);

        checkUsernameAvailability(username);
        checkEmailAvailability(email);

        Role role = determineUserRole();

        String passwordHash = passwordEncoder.encode(rawPassword);

        User user = new User(
                username,
                passwordHash,
                email,
                role
        );

        userRepository.save(user);

        log.info("User registered successfully: id={}, username={}, role={}",
                user.getId(),
                user.getUsername(),
                user.getRole());

        return user;
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
}
