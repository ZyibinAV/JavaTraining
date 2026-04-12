package com.homeapp.javatraining.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Index;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
    @Index(name = "idx_users_username")
    private String username;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(nullable = false, unique = true)
    @Index(name = "idx_users_email")
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

    @Version
    private Long version;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TestResult> testResults = new ArrayList<>();

    public User(String username, String passwordHash, String email, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;

        this.nickname = username;
        this.about = "";
        this.avatarPath = "/resources/avatars/default/default.png";
        this.createdAt = LocalDateTime.now();

    }

    public void addTestResult(TestResult result) {
        testResults.add(result);
        result.setUser(this);
    }

    public void removeTestResult(TestResult result) {
        testResults.remove(result);
        result.setUser(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return username != null && username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

}


