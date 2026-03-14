package com.homeapp.javatraining.validation;

import com.homeapp.javatraining.exception.ValidationException;
import com.homeapp.javatraining.model.Question;

import java.util.List;

public class QuestionValidator {

    public static void validate(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw ValidationException.general("The list of questions is empty");
        }
        for (int i = 0; i < questions.size(); i++) {
            validateQuestion(questions.get(i), i);
        }
    }

    private static void validateQuestion(Question question, int index) {
        if (question == null) {
            throw ValidationException.general("Index issue " + index + " equals null");
        }
        if (question.getQuestionText() == null ||
                question.getQuestionText().isBlank()) {
            throw ValidationException.general("Blank question text (index=" + index + ")");
        }
        int correctIndex = question.getCorrectAnswerIndex();
        if (question.getAnswers() == null || question.getAnswers().isEmpty()) {
            throw ValidationException.general("Answers list is empty (index=" + index + ")");
        }
        if (correctIndex < 0 || correctIndex >= question.getAnswers().size()) {
            throw ValidationException.general("Incorrect correctAnswerIndex (index=" + index + ")");
        }
    }

    public void validateTopics(String[] topics) {
        if (topics == null || topics.length == 0) {
            throw ValidationException.general("Please select at least one topic");
        }

        if (topics.length > 5) {
            throw ValidationException.general("Please select no more than 5 topics");
        }
    }

    public void validateAnswer(String answerIndexStr, int maxIndex) {
        if (answerIndexStr == null || answerIndexStr.trim().isEmpty()) {
            throw ValidationException.general("Please select an answer");
        }

        try {
            int answerIndex = Integer.parseInt(answerIndexStr);

            if (answerIndex < 0 || answerIndex > maxIndex) {
                throw ValidationException.general("Invalid answer selected");
            }

        } catch (NumberFormatException e) {
            throw ValidationException.general("Invalid answer format");
        }
    }
}