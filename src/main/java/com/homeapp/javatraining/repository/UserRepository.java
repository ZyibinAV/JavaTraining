package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    void delete(User user);

    Optional<User> findByUserName(String username);

    Optional<User> findById(long id);

    List<User> findAll();
}
