package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Implementation Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userServiceImpl;

    @Test
    @DisplayName("Should register regular user successfully")
    void register_withRegularUser_shouldAssignUserRole() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";

        User existingUser = new User("existing", "hash", "existing@example.com", Role.ADMIN);
        when(userRepository.findAny()).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        userServiceImpl = new UserServiceImpl(userRepository);

        // Act
        User result = userServiceImpl.register(username, password, email);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getRole()).isEqualTo(Role.USER);
        assertThat(result.getPasswordHash()).isNotNull();
        assertThat(result.getPasswordHash()).isNotEqualTo(password);

        verify(userRepository).findByUserName(username);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should register first user with ADMIN role")
    void register_withFirstUser_shouldAssignAdminRole() {
        // Arrange
        String username = "firstuser";
        String password = "password123";
        String email = "first@example.com";

        when(userRepository.findAny()).thenReturn(Optional.empty());
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        userServiceImpl = new UserServiceImpl(userRepository);

        // Act
        User result = userServiceImpl.register(username, password, email);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
        assertThat(result.getEmail()).isEqualTo(email);

        verify(userRepository).findAny();
        verify(userRepository).findByUserName(username);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void register_withExistingUsername_shouldThrowException() {
        // Arrange
        String username = "existinguser";
        String password = "password123";
        String email = "test@example.com";

        User existingUser = new User(username, "hash", "existing@example.com", Role.USER);
        when(userRepository.findByUserName(username)).thenReturn(Optional.of(existingUser));

        userServiceImpl = new UserServiceImpl(userRepository);

        // Act & Assert
        assertThatThrownBy(() -> userServiceImpl.register(username, password, email))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User with this login already exists");

        verify(userRepository).findByUserName(username);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should hash password before saving")
    void register_shouldHashPasswordBeforeSaving() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";

        User existingUser = new User("existing", "hash", "existing@example.com", Role.ADMIN);
        when(userRepository.findAny()).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());
        userServiceImpl = new UserServiceImpl(userRepository);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        // Act
        userServiceImpl.register(username, password, email);

        // Assert
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        
        assertThat(savedUser.getPasswordHash()).isNotNull();
        assertThat(savedUser.getPasswordHash()).isNotEqualTo(password);
        assertThat(savedUser.getPasswordHash()).hasSize(64); // SHA-256 produces 64 hex characters
    }

    @Test
    @DisplayName("Should save user through repository")
    void register_shouldCallRepositorySave() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";

        User existingUser = new User("existing", "hash", "existing@example.com", Role.ADMIN);
        when(userRepository.findAny()).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        userServiceImpl = new UserServiceImpl(userRepository);

        // Act
        userServiceImpl.register(username, password, email);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should check if username exists before registration")
    void register_shouldCheckUsernameExists() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";

        User existingUser = new User("existing", "hash", "existing@example.com", Role.ADMIN);
        when(userRepository.findAny()).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        userServiceImpl = new UserServiceImpl(userRepository);

        // Act
        userServiceImpl.register(username, password, email);

        // Assert
        verify(userRepository, times(1)).findByUserName(username);
    }

    @Test
    @DisplayName("Should assign USER role when users already exist")
    void register_withNonAdminUsername_shouldAssignUserRole() {
        // Arrange
        String username = "regularuser";
        String password = "password123";
        String email = "test@example.com";

        User existingUser = new User("existing", "hash", "existing@example.com", Role.ADMIN);
        when(userRepository.findAny()).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        userServiceImpl = new UserServiceImpl(userRepository);

        // Act
        User result = userServiceImpl.register(username, password, email);

        // Assert
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Should assign USER role when users already exist regardless of username case")
    void register_withAdminInDifferentCase_shouldAssignUserRole() {
        // Arrange
        String username = "Admin"; // Capital A
        String password = "password123";
        String email = "test@example.com";

        User existingUser = new User("existing", "hash", "existing@example.com", Role.ADMIN);
        when(userRepository.findAny()).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        userServiceImpl = new UserServiceImpl(userRepository);

        // Act
        User result = userServiceImpl.register(username, password, email);

        // Assert
        // When users already exist, role assignment doesn't depend on username
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("Should return registered user with all fields set")
    void register_shouldReturnUserWithAllFieldsSet() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String email = "test@example.com";

        User existingUser = new User("existing", "hash", "existing@example.com", Role.ADMIN);
        when(userRepository.findAny()).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUserName(username)).thenReturn(Optional.empty());

        userServiceImpl = new UserServiceImpl(userRepository);

        // Act
        User result = userServiceImpl.register(username, password, email);

        // Assert
        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPasswordHash()).isNotNull();
        assertThat(result.getRole()).isNotNull();
        assertThat(result.getNickname()).isEqualTo(username); // Set in User constructor
        assertThat(result.getAbout()).isEqualTo(""); // Set in User constructor
        assertThat(result.getAvatarPath()).isEqualTo("/resources/avatars/default/default.png"); // Set in User constructor
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.isBlocked()).isFalse();
    }
}
