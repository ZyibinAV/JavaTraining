package com.homeapp.javatraining.service;

import com.homeapp.javatraining.exception.ValidationException;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Registration Service Tests")
class RegistrationServiceTest {

    @Mock
    private UserService userService;

    private RegistrationService registrationService;

    @Test
    @DisplayName("Should register user successfully with valid data")
    void registerUser_withValidData_shouldReturnUser() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";

        User expectedUser = new User(username, "hashedPassword", email, Role.USER);
        when(userService.register(username, password, email)).thenReturn(expectedUser);

        registrationService = new RegistrationService(userService);

        // Act
        User result = registrationService.registerUser(username, password, email);

        // Assert
        assertThat(result).isEqualTo(expectedUser);
        verify(userService).register(username, password, email);
    }

    @Test
    @DisplayName("Should throw validation exception when username is invalid")
    void registerUser_withInvalidUsername_shouldThrowValidationException() {
        // Arrange
        String username = "ab"; // Too short
        String password = "password123";
        String email = "test@example.com";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(username, password, email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw validation exception when password is invalid")
    void registerUser_withInvalidPassword_shouldThrowValidationException() {
        // Arrange
        String username = "testuser";
        String password = "12345"; // Too short
        String email = "test@example.com";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(username, password, email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw validation exception when email is invalid")
    void registerUser_withInvalidEmail_shouldThrowValidationException() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "invalid-email";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(username, password, email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "email");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw validation exception when username is null")
    void registerUser_withNullUsername_shouldThrowValidationException() {
        // Arrange
        String password = "password123";
        String email = "test@example.com";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(null, password, email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw validation exception when password is null")
    void registerUser_withNullPassword_shouldThrowValidationException() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(username, null, email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw validation exception when email is null")
    void registerUser_withNullEmail_shouldThrowValidationException() {
        // Arrange
        String username = "testuser";
        String password = "password123";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(username, password, null))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "email");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw validation exception when username is empty")
    void registerUser_withEmptyUsername_shouldThrowValidationException() {
        // Arrange
        String username = "";
        String password = "password123";
        String email = "test@example.com";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(username, password, email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw validation exception when password is empty")
    void registerUser_withEmptyPassword_shouldThrowValidationException() {
        // Arrange
        String username = "testuser";
        String password = "";
        String email = "test@example.com";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(username, password, email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw validation exception when email is empty")
    void registerUser_withEmptyEmail_shouldThrowValidationException() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(username, password, email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "email");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should call userService.register after validation")
    void registerUser_shouldCallUserServiceAfterValidation() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";

        User expectedUser = new User(username, "hashedPassword", email, Role.USER);
        when(userService.register(username, password, email)).thenReturn(expectedUser);

        registrationService = new RegistrationService(userService);

        // Act
        registrationService.registerUser(username, password, email);

        // Assert
        verify(userService, times(1)).register(username, password, email);
    }

    @Test
    @DisplayName("Should handle username with special characters")
    void registerUser_withUsernameWithSpecialCharacters_shouldThrowValidationException() {
        // Arrange
        String username = "user@name"; // Invalid characters
        String password = "password123";
        String email = "test@example.com";

        registrationService = new RegistrationService(userService);

        // Act & Assert
        assertThatThrownBy(() -> registrationService.registerUser(username, password, email))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username");

        verify(userService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle username with valid special characters")
    void registerUser_withValidUsernameSpecialCharacters_shouldRegisterSuccessfully() {
        // Arrange
        String username = "user_name-123"; // Valid
        String password = "password123";
        String email = "test@example.com";

        User expectedUser = new User(username, "hashedPassword", email, Role.USER);
        when(userService.register(username, password, email)).thenReturn(expectedUser);

        registrationService = new RegistrationService(userService);

        // Act
        User result = registrationService.registerUser(username, password, email);

        // Assert
        assertThat(result).isEqualTo(expectedUser);
        verify(userService).register(username, password, email);
    }
}
