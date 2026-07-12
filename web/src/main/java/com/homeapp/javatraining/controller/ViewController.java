package com.homeapp.javatraining.controller;

import com.homeapp.javatraining.config.JwtTokenProvider;
import com.homeapp.javatraining.dto.mapper.UserMapper;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.CurrentUserService;
import com.homeapp.javatraining.service.RegistrationService;
import com.homeapp.javatraining.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }
        return "login";
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        User user = userService.getProfile(userId);
        model.addAttribute("profile", userMapper.toProfileResponse(user));
        return "home";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        User user = userService.getProfile(userId);
        model.addAttribute("profile", userMapper.toProfileResponse(user));
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(Authentication authentication, Model model) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        User user = userService.getProfile(userId);
        model.addAttribute("profile", userMapper.toProfileResponse(user));
        return "profile-edit";
    }

    @PostMapping("/profile/edit")
    public String editProfile(Authentication authentication,
                              @RequestParam(required = false) String nickname,
                              @RequestParam(required = false) String about,
                              Model model) {
        try {
            Long userId = currentUserService.getCurrentUserId(authentication);
            userService.updateProfile(userId, nickname, about);
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            Long userId = currentUserService.getCurrentUserId(authentication);
            User user = userService.getProfile(userId);
            model.addAttribute("profile", userMapper.toProfileResponse(user));
            return "profile-edit";
        }
    }

    @GetMapping("/profile/avatar")
    public String avatarForm(Authentication authentication, Model model) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        User user = userService.getProfile(userId);
        model.addAttribute("currentAvatar", user.getAvatarPath());
        return "avatar-select";
    }

    @PostMapping("/profile/avatar")
    public String selectAvatar(Authentication authentication,
                               @RequestParam String avatar) {
        Long userId = currentUserService.getCurrentUserId(authentication);
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
                           HttpServletRequest request,
                           HttpServletResponse response,
                           Model model) {
        try {
            User user = registrationService.registerUser(username, password, email);
            String token = jwtTokenProvider.generateToken(user);
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(cookieSecure);
            cookie.setPath("/");
            cookie.setMaxAge((int) (expirationMs / 1000));
            response.addCookie(cookie);

            var auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    user.getUsername(), null,
                    java.util.List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
            var securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(auth);
            new org.springframework.security.web.context.HttpSessionSecurityContextRepository()
                    .saveContext(securityContext, request, response);

            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/profile/password")
    public String passwordForm() {
        return "profile-password";
    }

    @PostMapping("/profile/password")
    public String changePassword(Authentication authentication,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Model model) {
        try {
            Long userId = currentUserService.getCurrentUserId(authentication);
            userService.changePassword(userId, currentPassword, newPassword, confirmPassword);
            model.addAttribute("success", "Пароль успешно изменён");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "profile-password";
    }
}
