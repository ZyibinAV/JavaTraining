package com.homeapp.javatraining.controller;

import com.homeapp.javatraining.config.JwtTokenProvider;
import com.homeapp.javatraining.dto.AuthResponse;
import com.homeapp.javatraining.dto.LoginRequest;
import com.homeapp.javatraining.dto.RegisterRequest;
import com.homeapp.javatraining.model.RefreshToken;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.AuthenticationService;
import com.homeapp.javatraining.service.RefreshTokenService;
import com.homeapp.javatraining.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        User user = authenticationService.authenticate(request.username(), request.password());
        String accessToken = jwtTokenProvider.generateToken(user);
        RefreshToken rt = refreshTokenService.createRefreshToken(user);
        setRefreshTokenCookie(response, rt.getToken());
        return ResponseEntity.ok(new AuthResponse(accessToken, user.getId(), user.getUsername(), user.getRole()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                 HttpServletResponse response) {
        User user = userService.register(request.username(), request.password(), request.email());
        String accessToken = jwtTokenProvider.generateToken(user);
        RefreshToken rt = refreshTokenService.createRefreshToken(user);
        setRefreshTokenCookie(response, rt.getToken());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(accessToken, user.getId(), user.getUsername(), user.getRole()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue("refreshToken") String refreshTokenValue,
                                                HttpServletResponse response) {
        User user = refreshTokenService.validateAndRotate(refreshTokenValue);
        String accessToken = jwtTokenProvider.generateToken(user);
        RefreshToken newRt = refreshTokenService.createRefreshToken(user);
        setRefreshTokenCookie(response, newRt.getToken());
        return ResponseEntity.ok(new AuthResponse(accessToken, user.getId(), user.getUsername(), user.getRole()));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge((int) (refreshExpirationMs / 1000));
        response.addCookie(cookie);
    }
}
