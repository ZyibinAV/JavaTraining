package com.homeapp.javatraining.service;


import com.homeapp.javatraining.exception.user.InvalidRefreshTokenException;
import com.homeapp.javatraining.model.RefreshToken;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiresAt(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    public User validateAndRotate(String tokenValue) {
        RefreshToken stored = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (stored.isRevoked()) {
            throw new InvalidRefreshTokenException();
        }

        if (stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new InvalidRefreshTokenException();
        }

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return stored.getUser();
    }

    public void revokeAllForUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
