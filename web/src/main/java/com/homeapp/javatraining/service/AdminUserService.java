package com.homeapp.javatraining.service;

import com.homeapp.javatraining.exception.user.CannotBlockSelfException;
import com.homeapp.javatraining.exception.user.CannotChangeOwnRoleException;
import com.homeapp.javatraining.exception.user.UserNotFoundException;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserService {

    private final UserRepository userRepository;

    public void changeUserRole(long adminId, long targetUserId, Role newRole) {

        log.info("Admin {} attempts to change role of user {} to {}",
                adminId,
                targetUserId,
                newRole);

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> {
                    log.warn("Admin {} attempted to change role of non-existing user {}",
                            adminId,
                            targetUserId);
                    return new UserNotFoundException(targetUserId);
                });

        if (user.getId().equals(adminId)) {
            log.warn("Admin {} attempted to change own role", adminId);
            throw new CannotChangeOwnRoleException();
        }

        user.setRole(newRole);

        log.info("User {} role changed to {} by admin {}",
                targetUserId,
                newRole,
                adminId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }

    public void toggleBlockUser(long adminId, long targetUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserNotFoundException(targetUserId));
        if (user.getId().equals(adminId)) {
            throw new CannotBlockSelfException();
        }
        user.setBlocked(!user.isBlocked());
    }
}
