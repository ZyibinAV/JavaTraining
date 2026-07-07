package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findFirstBy();

    Optional<User> findByEmail(String email);
}
