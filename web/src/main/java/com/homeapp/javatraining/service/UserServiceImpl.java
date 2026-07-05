package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import com.homeapp.javatraining.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;



    @Override
    public User register(String username, String rawPassword, String email) {
        log.info("User registration attempt: username={}", username);
        
        Role role;
        Optional<User> existingUser = userRepository.findFirstBy();
        
        if (existingUser.isEmpty()) {
            role = Role.ADMIN;
            log.info("First user registration - assigning ADMIN role to: {}", username);
        } else {
            role = Role.USER;
        }
        
        userRepository.findByUsername(username)
                .ifPresent(u -> {
                    log.warn("Registration failed: username {} already exists", username);
                    throw new IllegalStateException("User with this login already exists");
                });
        String passwordHash = PasswordUtil.hashPassword(rawPassword);
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
                user.getRole()
        );
        return user;
    }

}
