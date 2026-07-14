package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.User;

public interface UserService {

    User register(String username, String rawPassword, String email);
    User updateProfile(Long userId, String nickname, String about);
    void updateAvatar(Long userId, String avatarPath);
    void changePassword(Long userId, String currentPassword, String newPassword, String confirmPassword);
    User getProfile(Long userId);
}
