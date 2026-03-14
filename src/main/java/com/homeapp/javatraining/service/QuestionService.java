package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.util.ValidationFactory;
import com.homeapp.javatraining.validation.QuestionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);
    private final QuestionValidator questionValidator;

    public QuestionService() {
        this.questionValidator = ValidationFactory.createQuestionValidator();
    }

    public void processAnswer(InterviewState state, String answerIndexStr) {
        questionValidator.validateAnswer(answerIndexStr, state.getCurrentQuestion().getAnswers().size() - 1);

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
