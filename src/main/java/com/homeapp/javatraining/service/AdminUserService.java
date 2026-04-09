package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminUserService {
    private static final Logger log = LoggerFactory.getLogger(AdminUserService.class);

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void changeUserRole(long adminId, long targetUserId, Role newRole) {
        log.info("Admin {} attempts to change role of user {} to {}",
                adminId,
                targetUserId,
                newRole
        );

        userRepository.findById(targetUserId).ifPresentOrElse(user -> {
            if (user.getId() == adminId) {
                log.warn("Admin {} attempted to change own role. Operation denied", adminId);
                return;
            }
            user.changeRole(newRole);
            userRepository.save(user);
            log.info("User {} role changed to {} by admin {}",
                    targetUserId,
                    newRole,
                    adminId
            );
        }, () -> {
            log.warn("Admin {} attempted to change role of non-existing user {}",
                    adminId,
                    targetUserId);
        });
    }
}
