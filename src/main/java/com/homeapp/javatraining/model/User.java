package com.homeapp.javatraining.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(nullable = false, unique = true)
    private String email;
    private String nickname;
    @Column(columnDefinition = "TEXT")
    private String about;
    @Column(name = "avatar_path")
    private String avatarPath;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    private boolean blocked;

    public User(String username, String passwordHash, String email, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;

        this.nickname = username;
        this.about = "";
        this.avatarPath = "/avatars/default/default.png";
        this.createdAt = LocalDateTime.now();

    }

    public void changeRole(Role newRole) {
        this.role = newRole;
    }

}


