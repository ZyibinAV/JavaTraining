package com.homeapp.javatraining.util;

import com.homeapp.javatraining.exception.topic.TopicNotFoundException;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TopicLoader {

    private final TopicRepository topicRepository;

    public List<Topic> loadAllTopics() {
        return topicRepository.findAll();
    }

    public Topic findByCode(String code) {
        return topicRepository.findByCode(code)
                .orElseThrow(() -> new TopicNotFoundException(code));
    }
}
