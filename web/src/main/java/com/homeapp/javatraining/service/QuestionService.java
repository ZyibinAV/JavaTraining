package com.homeapp.javatraining.service;

import com.homeapp.javatraining.exception.ValidationException;
import com.homeapp.javatraining.exception.question.NotEnoughQuestionsException;
import com.homeapp.javatraining.exception.topic.TopicNotFoundException;
import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.util.TopicLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final TopicLoader topicLoader;



    public List<Question> getRandomQuestionsByTopics(List<String> topicCodes, int questionCount) {
        List<Question> allQuestions = new ArrayList<>();

        for (String code : topicCodes) {
            Topic topic = topicLoader.findByCode(code);

            if (topic == null) {
                throw new TopicNotFoundException(code);
            }

            allQuestions.addAll(questionRepository.findByTopic(topic));
        }
        if (allQuestions.size() < questionCount) {
            throw new NotEnoughQuestionsException(questionCount, allQuestions.size());
        }
        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, questionCount);
    }

    public void processAnswer(InterviewState state, String answerIndexStr) {
        if (answerIndexStr == null || answerIndexStr.trim().isEmpty()) {
            throw new ValidationException("Please select an answer", "answerIndex");
        }

        int selectedIndex;
        try {
            selectedIndex = Integer.parseInt(answerIndexStr);
        } catch (NumberFormatException e) {
            throw new ValidationException("Invalid answer format", "answerIndex");
        }

        int maxIndex = state.getCurrentQuestion().getAnswers().size() - 1;
        if (selectedIndex < 0 || selectedIndex > maxIndex) {
            throw new ValidationException("Invalid answer selected", "answerIndex");
        }

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
