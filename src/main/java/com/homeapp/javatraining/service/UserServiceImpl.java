package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import com.homeapp.javatraining.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserServiceImpl implements UserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(String username, String rawPassword, String email) {
        log.info("User registration attempt: username={}", username);
        Role role = username.equals("admin")
                ? Role.ADMIN
                : Role.USER;
        userRepository.findByUserName(username)
                .ifPresent(u -> {
                    log.warn("Registration failed: username {} already exists", username);
                    throw new IllegalStateException("User with this login already exists");
                });
        String passwordHash = PasswordUtil.hashPassword(rawPassword);
        User user = new User(
                0,
                username,
                passwordHash,
                email,
                role
        );

        userRepository.save(user);
        log.info("User registered successfully: id={}, username={}, role={}",
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
        return user;
    }

}
