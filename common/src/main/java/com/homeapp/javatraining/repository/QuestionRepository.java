package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {

    @EntityGraph(attributePaths = {"topic", "answers"})
    List<Question> findByTopic(Topic topic);

    @EntityGraph(attributePaths = {"topic", "answers"})
    Optional<Question> findById(Long id);

    @EntityGraph(attributePaths = {"topic", "answers"})
    List<Question> findAll();

    boolean existsByQuestionTextAndTopic(String questionText, Topic topic);
}
