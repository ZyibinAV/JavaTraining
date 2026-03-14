package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.source.FileQuestionSource;
import com.homeapp.javatraining.source.QuestionSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class QuestionRepository {
    private static final Logger log = LoggerFactory.getLogger(QuestionRepository.class);

    private final QuestionSource source;

    public QuestionRepository(QuestionSource source) {
        this.source = source;
    }

    public static QuestionRepository defaultRepository() {
        return new QuestionRepository(new FileQuestionSource());
    }

    public List<Question> getQuestions(Topic topic) {
        log.debug("Loading questions for topic={}", topic);
        List<Question> questions = source.loadQuestions(topic);
        log.info("Questions loaded for topic={}, count={}", topic, questions.size());
        return questions;
    }
}
