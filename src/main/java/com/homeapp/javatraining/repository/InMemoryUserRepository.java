package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);

    private final Map<Long, User> usersById = new ConcurrentHashMap<>();

    private final Map<String, User> usersByUserName = new ConcurrentHashMap<>();

    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            user.setId(idGenerator.getAndIncrement());
        }
        usersById.put(user.getId(), user);
        usersByUserName.put(user.getUsername(), user);
        log.info("User saved: id={}, username={}, new={}",
                user.getId(),
                user.getUsername(),
                user.getId() == idGenerator.get() - 1);
    }

    @Override
    public Optional<User> findByUserName(String username) {
        Optional<User> user = Optional.ofNullable(usersByUserName.get(username));
        log.debug("findByUserName called: username={}, found={}", username, user.isPresent());
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        Optional<User> user = Optional.ofNullable(usersById.get(id));
        log.debug("findById called: id={}, found={}", id, user.isPresent());
        return user;
    }

    @Override
    public List<User> findAll() {
        ArrayList<User> users = new ArrayList<>(usersById.values());
        log.debug("findAll called: count={}", users.size());
        return users;
    }
}
