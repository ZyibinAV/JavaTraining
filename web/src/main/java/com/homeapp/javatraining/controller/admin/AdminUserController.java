package com.homeapp.javatraining.controller.admin;


import com.homeapp.javatraining.dto.RoleUpdateRequest;
import com.homeapp.javatraining.dto.UserDTO;
import com.homeapp.javatraining.dto.mapper.UserMapper;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@Slf4j
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.debug("GET /api/admin/users");
        List<UserDTO> users = adminUserService.getAllUsers().stream()
                .map(userMapper::toUserDTO)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.debug("GET /api/admin/users/{}", id);
        User user = adminUserService.getUserById(id);
        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.debug("DELETE /api/admin/users/{}", id);
        adminUserService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<Void> toggleBlock(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        Long adminId = Long.parseLong(jwt.getSubject());
        log.debug("POST /api/admin/users/{}/block by admin {}", id, adminId);
        adminUserService.toggleBlockUser(adminId, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<Void> changeRole(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        Long adminId = Long.parseLong(jwt.getSubject());
        log.debug("PUT /api/admin/users/{}/role by admin {} -> {}", id, adminId, request.role());
        adminUserService.changeUserRole(adminId, id, Role.valueOf(request.role()));
        return ResponseEntity.ok().build();
    }
}
