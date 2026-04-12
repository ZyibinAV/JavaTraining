package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.Topic;

import java.util.List;
import java.util.Optional;

public interface TopicRepository {
    List<Topic> findAll();
    Optional<Topic> findByCode(String code);
    void save(Topic topic);
    void delete(Topic topic);
}
