package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;



    public User registerUser(String username, String password, String email) {

        return userService.register(username, password, email);
    }
}
