package com.homeapp.javatraining.service;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public Long getCurrentUserId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}
