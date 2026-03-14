package com.homeapp.javatraining.model;

import java.time.LocalDateTime;

public class User {

    private long id;
    private final String username;
    private final String passwordHash;
    private final String email;
    private String nickname;
    private String about;
    private String avatarPath;
    private Role role;
    private final LocalDateTime createdAt;
    private boolean blocked;

    public User(long id, String username, String passwordHash, String email, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;

        this.nickname = username;
        this.about = "";
        this.avatarPath = "/avatars/default/default.png";
        this.createdAt = LocalDateTime.now();

    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAbout() {
        return about;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void setId(long id) {
        if (this.id != 0) {
            throw new IllegalStateException("ID пользователя уже установлен");
        }
        this.id = id;
    }
}
