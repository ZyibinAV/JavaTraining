package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository {

    List<Question> getQuestions(Topic topic);

    Optional<Question> findById(Long id);

    List<Question> findAll();

    void save(Question question);

    boolean existsByTextAndTopic(String questionText, Topic topic);

    void saveAll(List<Question> questions);

    void delete(Question question);
}
