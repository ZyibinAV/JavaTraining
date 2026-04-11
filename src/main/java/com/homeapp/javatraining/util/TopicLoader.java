package com.homeapp.javatraining.util;

import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.TopicRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TopicLoader {

    private final TopicRepository topicRepository;

    public List<Topic> loadAllTopics() {
        return topicRepository.findAll();
    }

    public Topic findByCode(String code) {
        return topicRepository.findByCode(code).orElse(null);
    }
}
