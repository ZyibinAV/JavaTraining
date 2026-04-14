package com.homeapp.javatraining.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Password Utility Tests")
class PasswordUtilTest {

    @Test
    @DisplayName("Should hash password consistently")
    void hashPassword_withSamePassword_shouldReturnSameHash() {
        // Arrange
        String password = "password123";

        // Act
        String hash1 = PasswordUtil.hashPassword(password);
        String hash2 = PasswordUtil.hashPassword(password);

        // Assert
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("Should hash different passwords to different hashes")
    void hashPassword_withDifferentPasswords_shouldReturnDifferentHashes() {
        // Arrange
        String password1 = "password123";
        String password2 = "password456";

        // Act
        String hash1 = PasswordUtil.hashPassword(password1);
        String hash2 = PasswordUtil.hashPassword(password2);

        // Assert
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Should verify password with correct password")
    void verifyPassword_withCorrectPassword_shouldReturnTrue() {
        // Arrange
        String password = "password123";
        String hash = PasswordUtil.hashPassword(password);

        // Act
        boolean result = PasswordUtil.verifyPassword(password, hash);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should not verify password with incorrect password")
    void verifyPassword_withIncorrectPassword_shouldReturnFalse() {
        // Arrange
        String password = "password123";
        String wrongPassword = "wrongpassword";
        String hash = PasswordUtil.hashPassword(password);

        // Act
        boolean result = PasswordUtil.verifyPassword(wrongPassword, hash);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle empty password")
    void hashPassword_withEmptyPassword_shouldReturnHash() {
        // Arrange
        String password = "";

        // Act
        String hash = PasswordUtil.hashPassword(password);

        // Assert
        assertThat(hash).isNotEmpty();
    }

    @Test
    @DisplayName("Should handle special characters in password")
    void hashPassword_withSpecialCharacters_shouldReturnHash() {
        // Arrange
        String password = "p@$$w0rd!#$%";

        // Act
        String hash = PasswordUtil.hashPassword(password);

        // Assert
        assertThat(hash).isNotEmpty();
    }

    @Test
    @DisplayName("Should verify password with special characters")
    void verifyPassword_withSpecialCharacters_shouldReturnTrue() {
        // Arrange
        String password = "p@$$w0rd!#$%";
        String hash = PasswordUtil.hashPassword(password);

        // Act
        boolean result = PasswordUtil.verifyPassword(password, hash);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when trying to hash null password")
    void hashPassword_withNullPassword_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> PasswordUtil.hashPassword(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to hash password");
    }

    @Test
    @DisplayName("Should throw exception when verifying with null password")
    void verifyPassword_withNullPassword_shouldThrowException() {
        // Arrange
        String hash = PasswordUtil.hashPassword("password123");

        // Act & Assert
        assertThatThrownBy(() -> PasswordUtil.verifyPassword(null, hash))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to hash password");
    }

    @Test
    @DisplayName("Should return false when verifying with null hash")
    void verifyPassword_withNullHash_shouldReturnFalse() {
        // Arrange
        String password = "password123";

        // Act
        boolean result = PasswordUtil.verifyPassword(password, null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should hash should not contain plain password")
    void hashPassword_shouldNotContainPlainPassword() {
        // Arrange
        String password = "password123";

        // Act
        String hash = PasswordUtil.hashPassword(password);

        // Assert
        assertThat(hash).doesNotContain(password);
    }

    @Test
    @DisplayName("Should produce consistent hash length")
    void hashPassword_shouldProduceConsistentLength() {
        // Arrange
        String password = "password123";

        // Act
        String hash = PasswordUtil.hashPassword(password);

        // Assert
        assertThat(hash).hasSize(64); // SHA-256 produces 64 hex characters
    }
}
