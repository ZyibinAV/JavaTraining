package com.homeapp.javatraining.controller;

import com.homeapp.javatraining.dto.mapper.UserMapper;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.CurrentUserService;
import com.homeapp.javatraining.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final CurrentUserService currentUserService;
    private final UserService userService;
    private final UserMapper userMapper;

    public GlobalModelAdvice(CurrentUserService currentUserService, UserService userService, UserMapper userMapper) {
        this.currentUserService = currentUserService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @ModelAttribute("currentUser")
    public Object getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        try {
            Long userId = currentUserService.getCurrentUserId(authentication);
            User user = userService.getProfile(userId);
            return userMapper.toProfileResponse(user);
        } catch (Exception e) {
            return null;
        }
    }
}