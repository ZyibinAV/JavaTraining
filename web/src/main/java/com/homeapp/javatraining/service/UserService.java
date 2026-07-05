package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.User;

public interface UserService {

    User register(String username, String rawPassword, String email);
}
