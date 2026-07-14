package com.homeapp.javatraining.service;

import com.homeapp.javatraining.exception.user.InvalidCredentialsException;
import com.homeapp.javatraining.exception.user.UserBlockedException;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public User authenticate(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (user.isBlocked()) {
            throw new UserBlockedException();
        }
        return user;
    }
}
