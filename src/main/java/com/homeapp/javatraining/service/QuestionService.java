package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.util.TopicLoader;
import com.homeapp.javatraining.util.ValidationFactory;
import com.homeapp.javatraining.validation.QuestionValidator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class QuestionService {

    private final QuestionValidator questionValidator;
    private final QuestionRepository questionRepository;
    private final TopicLoader topicLoader;

    public QuestionService(QuestionValidator questionValidator, QuestionRepository questionRepository, TopicLoader topicLoader) {
        this.questionValidator = questionValidator;
        this.questionRepository = questionRepository;
        this.topicLoader = topicLoader;
    }

    public List<Question> getRandomQuestionsByTopics(List<String> topicCodes, int questionCount) {
        List<Question> allQuestions = new ArrayList<>();

        for (String code : topicCodes) {
            Topic topic = topicLoader.findByCode(code);

            if (topic == null) {
                throw  new IllegalArgumentException("Topic not found: " + code);
            }

            allQuestions.addAll(questionRepository.getQuestions(topic));
        }
        if (allQuestions.size() < questionCount) {
            throw new IllegalStateException("Not enough questions");
        }
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, questionCount);
    }

    public void processAnswer(InterviewState state, String answerIndexStr) {
        questionValidator.validateAnswer(
                answerIndexStr, state.getCurrentQuestion().getAnswers().size() - 1
        );

        int selectedIndex = Integer.parseInt(answerIndexStr);

        if (selectedIndex == state.getCurrentQuestion().getCorrectAnswerIndex()) {
            log.debug("Correct answer selected");
            state.incrementScore();
        } else {
            log.debug("Incorrect answer selected");
        }

        state.moveToNextQuestion();
        log.debug("Moving to next question, current index is now {}", state.getCurrentIndex());
    }
}
