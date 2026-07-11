package com.homeapp.javatraining.controller;

import com.homeapp.javatraining.config.JwtTokenProvider;
import com.homeapp.javatraining.dto.mapper.UserMapper;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.CurrentUserService;
import com.homeapp.javatraining.service.RegistrationService;
import com.homeapp.javatraining.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final RegistrationService registrationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;

    @Value("${jwt.expiration}")
    private long expirationMs;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal Jwt jwt, Model model) {
        Long userId = currentUserService.getCurrentUserId(jwt);
        User user = userService.getProfile(userId);
        model.addAttribute("profile", userMapper.toProfileResponse(user));
        return "home";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal Jwt jwt, Model model) {
        Long userId = currentUserService.getCurrentUserId(jwt);
        User user = userService.getProfile(userId);
        model.addAttribute("profile", userMapper.toProfileResponse(user));
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(@AuthenticationPrincipal Jwt jwt, Model model) {
        Long userId = currentUserService.getCurrentUserId(jwt);
        User user = userService.getProfile(userId);
        model.addAttribute("profile", userMapper.toProfileResponse(user));
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@AuthenticationPrincipal Jwt jwt,
                              @RequestParam(required = false) String nickname,
                              @RequestParam(required = false) String about,
                              Model model) {
        try {
            Long userId = currentUserService.getCurrentUserId(jwt);
            userService.updateProfile(userId, nickname, about);
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            Long userId = currentUserService.getCurrentUserId(jwt);
            User user = userService.getProfile(userId);
            model.addAttribute("profile", userMapper.toProfileResponse(user));
            return "profile-edit";
        }
    }

    @GetMapping("/profile/avatar")
    public String avatarForm(@AuthenticationPrincipal Jwt jwt, Model model) {
        Long userId = currentUserService.getCurrentUserId(jwt);
        User user = userService.getProfile(userId);
        model.addAttribute("currentAvatar", user.getAvatarPath());
        return "avatar-select";
    }

    @PostMapping("/profile/avatar")
    public String selectAvatar(@AuthenticationPrincipal Jwt jwt,
                               @RequestParam String avatar) {
        Long userId = currentUserService.getCurrentUserId(jwt);
        User user = userService.getProfile(userId);
        user.setAvatarPath(avatar);
        userService.updateProfile(userId, user.getNickname(), user.getAbout());
        return "redirect:/profile";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String email,
                           HttpServletResponse response,
                           Model model) {
        try {
            User user = registrationService.registerUser(username, password, email);
            String token = jwtTokenProvider.generateToken(user);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge((int) (expirationMs / 1000));
            response.addCookie(cookie);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
