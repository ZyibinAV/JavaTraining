package com.homeapp.javatraining.controller;

import com.homeapp.javatraining.dto.PasswordChangeRequest;
import com.homeapp.javatraining.dto.ProfileResponse;
import com.homeapp.javatraining.dto.ProfileUpdateRequest;
import com.homeapp.javatraining.dto.mapper.UserMapper;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.CurrentUserService;
import com.homeapp.javatraining.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        Long userId = currentUserService.getCurrentUserId(jwt);
        User user = userService.getProfile(userId);
        return ResponseEntity.ok(userMapper.toProfileResponse(user));
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ProfileUpdateRequest request) {
        Long userId = currentUserService.getCurrentUserId(jwt);
        User user = userService.updateProfile(userId, request.nickname(), request.about());
        return ResponseEntity.ok(userMapper.toProfileResponse(user));
    }

    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PasswordChangeRequest request) {
        Long userId = currentUserService.getCurrentUserId(jwt);
        userService.changePassword(userId, request.currentPassword(),
                request.newPassword(), request.confirmPassword());
        return ResponseEntity.ok().build();
    }
}
