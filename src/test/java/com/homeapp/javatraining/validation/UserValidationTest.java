package com.homeapp.javatraining.validation;

import com.homeapp.javatraining.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User Validation Tests")
class UserValidationTest {

    private final UserValidation userValidation = new UserValidation();

    @Test
    @DisplayName("Should validate successful registration with valid data")
    void validateRegistration_withValidData_shouldNotThrow() {
        // Act & Assert
        userValidation.validateRegistration("testuser", "password123", "test@example.com");
    }

    @Test
    @DisplayName("Should throw exception when username is null")
    void validateRegistration_withNullUsername_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration(null, "password123", "test@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username")
                .hasMessageContaining("Username cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when username is empty")
    void validateRegistration_withEmptyUsername_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("", "password123", "test@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username")
                .hasMessageContaining("Username cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when username is whitespace only")
    void validateRegistration_withWhitespaceUsername_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("   ", "password123", "test@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username")
                .hasMessageContaining("Username cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when username is too short")
    void validateRegistration_withShortUsername_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("ab", "password123", "test@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username")
                .hasMessageContaining("at least 3 characters");
    }

    @Test
    @DisplayName("Should throw exception when username is too long")
    void validateRegistration_withLongUsername_shouldThrowException() {
        // Arrange
        String longUsername = "a".repeat(21);

        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration(longUsername, "password123", "test@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username")
                .hasMessageContaining("at most 20 characters");
    }

    @ParameterizedTest
    @ValueSource(strings = {"user-name", "user_name", "username123", "User123", "test_user-2024"})
    @DisplayName("Should accept valid username formats")
    void validateUsername_withValidFormats_shouldNotThrow(String username) {
        // Act & Assert
        userValidation.validateUsername(username);
    }

    @ParameterizedTest
    @ValueSource(strings = {"user name", "user@name", "user.name", "user$name", "user#name", "user.name"})
    @DisplayName("Should throw exception for invalid username characters")
    void validateUsername_withInvalidCharacters_shouldThrowException(String username) {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateUsername(username))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username")
                .hasMessageContaining("can only contain letters, numbers, underscores and hyphens");
    }

    @Test
    @DisplayName("Should throw exception when password is null")
    void validateRegistration_withNullPassword_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("testuser", null, "test@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password")
                .hasMessageContaining("Password cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when password is empty")
    void validateRegistration_withEmptyPassword_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("testuser", "", "test@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password")
                .hasMessageContaining("Password cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when password is too short")
    void validateRegistration_withShortPassword_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("testuser", "12345", "test@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password")
                .hasMessageContaining("at least 6 characters");
    }

    @Test
    @DisplayName("Should throw exception when password is too long")
    void validateRegistration_withLongPassword_shouldThrowException() {
        // Arrange
        String longPassword = "a".repeat(101);

        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("testuser", longPassword, "test@example.com"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password")
                .hasMessageContaining("no more than 100 characters");
    }

    @Test
    @DisplayName("Should throw exception when email is null")
    void validateRegistration_withNullEmail_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("testuser", "password123", null))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "email")
                .hasMessageContaining("Email cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when email is empty")
    void validateRegistration_withEmptyEmail_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("testuser", "password123", ""))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "email")
                .hasMessageContaining("Email cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when email is whitespace only")
    void validateRegistration_withWhitespaceEmail_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateRegistration("testuser", "password123", "   "))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "email")
                .hasMessageContaining("Email cannot be empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@example.com", "user.name@domain.com", "user+tag@example.co.uk", "user_name@sub.domain.com", "test@com", "test@example"})
    @DisplayName("Should accept valid email formats")
    void validateEmail_withValidFormats_shouldNotThrow(String email) {
        // Act & Assert
        userValidation.validateEmail(email);
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "invalid@", "@example.com"})
    @DisplayName("Should throw exception for invalid email formats")
    void validateEmail_withInvalidFormats_shouldThrowException(String email) {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateEmail(email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "email")
                .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("Should throw exception when email is too long")
    void validateEmail_withLongEmail_shouldThrowException() {
        // Arrange
        String longEmail = "a".repeat(101) + "@example.com";

        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateEmail(longEmail))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "email")
                .hasMessageContaining("Email is too long");
    }

    @Test
    @DisplayName("Should validate successful login with valid data")
    void validateLogin_withValidData_shouldNotThrow() {
        // Act & Assert
        userValidation.validateLogin("testuser", "password123");
    }

    @Test
    @DisplayName("Should throw exception when login username is null")
    void validateLogin_withNullUsername_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateLogin(null, "password123"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username")
                .hasMessageContaining("Username cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when login username is empty")
    void validateLogin_withEmptyUsername_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateLogin("", "password123"))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username")
                .hasMessageContaining("Username cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when login password is null")
    void validateLogin_withNullPassword_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateLogin("testuser", null))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password")
                .hasMessageContaining("Password cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when login password is empty")
    void validateLogin_withEmptyPassword_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateLogin("testuser", ""))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password")
                .hasMessageContaining("Password cannot be empty");
    }

    @Test
    @DisplayName("Should validate successful profile with valid data")
    void validateProfile_withValidData_shouldNotThrow() {
        // Act & Assert
        userValidation.validateProfile("Nickname", "About me");
    }

    @Test
    @DisplayName("Should validate profile with null values")
    void validateProfile_withNullValues_shouldNotThrow() {
        // Act & Assert
        userValidation.validateProfile(null, null);
    }

    @Test
    @DisplayName("Should throw exception when nickname is too long")
    void validateProfile_withLongNickname_shouldThrowException() {
        // Arrange
        String longNickname = "a".repeat(51);

        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateProfile(longNickname, "About me"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Nickname is too long");
    }

    @Test
    @DisplayName("Should throw exception when about section is too long")
    void validateProfile_withLongAbout_shouldThrowException() {
        // Arrange
        String longAbout = "a".repeat(501);

        // Act & Assert
        assertThatThrownBy(() -> userValidation.validateProfile("Nickname", longAbout))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("About section is too long");
    }
}
