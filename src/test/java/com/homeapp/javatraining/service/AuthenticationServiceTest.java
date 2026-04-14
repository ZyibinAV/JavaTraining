package com.homeapp.javatraining.service;

import com.homeapp.javatraining.exception.AuthenticationException;
import com.homeapp.javatraining.exception.ValidationException;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import com.homeapp.javatraining.util.PasswordUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Should authenticate successfully with valid credentials")
    void authenticate_withValidCredentials_shouldReturnUser() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User(username, hashedPassword, "test@example.com", Role.USER);
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        authenticationService = new AuthenticationService(userRepository);

        // Act
        User result = authenticationService.authenticate(username, password);

        // Assert
        assertThat(result).isEqualTo(user);
        verify(userRepository).findByUserName(username);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void authenticate_withNonExistentUser_shouldThrowException() {
        // Arrange
        String username = "nonexistent";
        String password = "password123";

        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        authenticationService = new AuthenticationService(userRepository);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(username, password))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByUserName(username);
    }

    @Test
    @DisplayName("Should throw exception when user is blocked")
    void authenticate_withBlockedUser_shouldThrowException() {
        // Arrange
        String username = "blockeduser";
        String password = "password123";
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User(username, hashedPassword, "test@example.com", Role.USER);
        user.setBlocked(true);

        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        authenticationService = new AuthenticationService(userRepository);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(username, password))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("User is blocked");

        verify(userRepository).findByUserName(username);
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void authenticate_withIncorrectPassword_shouldThrowException() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";
        String hashedPassword = PasswordUtil.hashPassword("correctpassword");

        User user = new User(username, hashedPassword, "test@example.com", Role.USER);
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        authenticationService = new AuthenticationService(userRepository);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(username, password))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid login or password");

        verify(userRepository).findByUserName(username);
    }

    @Test
    @DisplayName("Should validate login credentials before authentication")
    void authenticate_withEmptyUsername_shouldThrowValidationException() {
        // Arrange
        String username = "";
        String password = "password123";

        authenticationService = new AuthenticationService(userRepository);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(username, password))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username");

        verify(userRepository, never()).findByUserName(anyString());
    }

    @Test
    @DisplayName("Should validate login credentials before authentication - empty password")
    void authenticate_withEmptyPassword_shouldThrowValidationException() {
        // Arrange
        String username = "testuser";
        String password = "";

        authenticationService = new AuthenticationService(userRepository);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(username, password))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password");

        verify(userRepository, never()).findByUserName(anyString());
    }

    @Test
    @DisplayName("Should call repository to find user by username")
    void authenticate_shouldCallRepositoryToFindUser() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User(username, hashedPassword, "test@example.com", Role.USER);
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        authenticationService = new AuthenticationService(userRepository);

        // Act
        authenticationService.authenticate(username, password);

        // Assert
        verify(userRepository, times(1)).findByUserName(username);
    }

    @Test
    @DisplayName("Should return user with correct role")
    void authenticate_withAdminUser_shouldReturnAdminUser() {
        // Arrange
        String username = "admin";
        String password = "admin123";
        String hashedPassword = PasswordUtil.hashPassword(password);

        User user = new User(username, hashedPassword, "admin@example.com", Role.ADMIN);
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(user));

        authenticationService = new AuthenticationService(userRepository);

        // Act
        User result = authenticationService.authenticate(username, password);

        // Assert
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.getUsername()).isEqualTo(username);
    }

    @Test
    @DisplayName("Should handle null username")
    void authenticate_withNullUsername_shouldThrowValidationException() {
        // Arrange
        String password = "password123";

        authenticationService = new AuthenticationService(userRepository);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(null, password))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "username");

        verify(userRepository, never()).findByUserName(anyString());
    }

    @Test
    @DisplayName("Should handle null password")
    void authenticate_withNullPassword_shouldThrowValidationException() {
        // Arrange
        String username = "testuser";

        authenticationService = new AuthenticationService(userRepository);

        // Act & Assert
        assertThatThrownBy(() -> authenticationService.authenticate(username, null))
                .isInstanceOf(ValidationException.class)
                .hasFieldOrPropertyWithValue("field", "password");

        verify(userRepository, never()).findByUserName(anyString());
    }
}
