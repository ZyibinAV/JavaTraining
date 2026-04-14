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

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin User Service Tests")
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    private AdminUserService adminUserService;

    // Helper method to set id via reflection for testing
    private void setUserId(User user, Long id) {
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set user id via reflection", e);
        }
    }

    @Test
    @DisplayName("Should change user role successfully")
    void changeUserRole_withValidData_shouldChangeRole() {
        // Arrange
        long adminId = 1L;
        long targetUserId = 2L;
        Role newRole = Role.ADMIN;

        User targetUser = new User("regularuser", "hash", "user@example.com", Role.USER);
        setUserId(targetUser, targetUserId);

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        adminUserService = new AdminUserService(userRepository);

        // Act
        adminUserService.changeUserRole(adminId, targetUserId, newRole);

        // Assert
        assertThat(targetUser.getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository).save(targetUser);
    }

    @Test
    @DisplayName("Should not allow admin to change own role")
    void changeUserRole_whenChangingOwnRole_shouldNotChange() {
        // Arrange
        long adminId = 1L;
        Role newRole = Role.USER;

        User targetUser = new User("admin", "hash", "admin@example.com", Role.ADMIN);
        setUserId(targetUser, adminId);

        when(userRepository.findById(adminId)).thenReturn(Optional.of(targetUser));

        adminUserService = new AdminUserService(userRepository);

        // Act
        adminUserService.changeUserRole(adminId, adminId, newRole);

        // Assert
        assertThat(targetUser.getRole()).isEqualTo(Role.ADMIN); // Role should not change
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle non-existent target user gracefully")
    void changeUserRole_withNonExistentTargetUser_shouldNotThrowException() {
        // Arrange
        long adminId = 1L;
        long targetUserId = 999L;
        Role newRole = Role.ADMIN;

        when(userRepository.findById(targetUserId)).thenReturn(Optional.empty());

        adminUserService = new AdminUserService(userRepository);

        // Act - should not throw exception
        adminUserService.changeUserRole(adminId, targetUserId, newRole);

        // Assert
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should change role from USER to ADMIN")
    void changeUserRole_fromUserToAdmin_shouldChangeSuccessfully() {
        // Arrange
        long adminId = 1L;
        long targetUserId = 2L;
        Role newRole = Role.ADMIN;

        User targetUser = new User("regularuser", "hash", "user@example.com", Role.USER);
        setUserId(targetUser, targetUserId);

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        adminUserService = new AdminUserService(userRepository);

        // Act
        adminUserService.changeUserRole(adminId, targetUserId, newRole);

        // Assert
        assertThat(targetUser.getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository).save(targetUser);
    }

    @Test
    @DisplayName("Should change role from ADMIN to USER")
    void changeUserRole_fromAdminToUser_shouldChangeSuccessfully() {
        // Arrange
        long adminId = 1L;
        long targetUserId = 2L;
        Role newRole = Role.USER;

        User targetUser = new User("otheradmin", "hash", "otheradmin@example.com", Role.ADMIN);
        setUserId(targetUser, targetUserId);

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        adminUserService = new AdminUserService(userRepository);

        // Act
        adminUserService.changeUserRole(adminId, targetUserId, newRole);

        // Assert
        assertThat(targetUser.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(targetUser);
    }

    @Test
    @DisplayName("Should call repository to find target user")
    void changeUserRole_shouldCallRepositoryToFindTargetUser() {
        // Arrange
        long adminId = 1L;
        long targetUserId = 2L;
        Role newRole = Role.ADMIN;

        User targetUser = new User("regularuser", "hash", "user@example.com", Role.USER);
        setUserId(targetUser, targetUserId);

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        adminUserService = new AdminUserService(userRepository);

        // Act
        adminUserService.changeUserRole(adminId, targetUserId, newRole);

        // Assert
        verify(userRepository, times(1)).findById(targetUserId);
    }

    @Test
    @DisplayName("Should save user with new role")
    void changeUserRole_shouldSaveUserWithNewRole() {
        // Arrange
        long adminId = 1L;
        long targetUserId = 2L;
        Role newRole = Role.ADMIN;

        User targetUser = new User("regularuser", "hash", "user@example.com", Role.USER);
        setUserId(targetUser, targetUserId);

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        adminUserService = new AdminUserService(userRepository);

        // Act
        adminUserService.changeUserRole(adminId, targetUserId, newRole);

        // Assert
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertThat(savedUser.getRole()).isEqualTo(Role.ADMIN);
        assertThat(savedUser.getId()).isEqualTo(targetUserId);
    }

    @Test
    @DisplayName("Should preserve other user properties when changing role")
    void changeUserRole_shouldPreserveOtherUserProperties() {
        // Arrange
        long adminId = 1L;
        long targetUserId = 2L;
        Role newRole = Role.ADMIN;

        User targetUser = new User("regularuser", "hash", "user@example.com", Role.USER);
        setUserId(targetUser, targetUserId);
        targetUser.setNickname("Regular User");
        targetUser.setAbout("About me");

        when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));

        adminUserService = new AdminUserService(userRepository);

        // Act
        adminUserService.changeUserRole(adminId, targetUserId, newRole);

        // Assert
        assertThat(targetUser.getRole()).isEqualTo(Role.ADMIN);
        assertThat(targetUser.getUsername()).isEqualTo("regularuser");
        assertThat(targetUser.getNickname()).isEqualTo("Regular User");
        assertThat(targetUser.getAbout()).isEqualTo("About me");
    }
}
