package com.homeapp.javatraining.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Question {

    private final String questionText;
    private final List<String> answers;
    private final int correctAnswerIndex;

    @JsonCreator
    public Question(@JsonProperty("questionText") String questionText,
                    @JsonProperty("answers") List<String> answers,
                    @JsonProperty("correctAnswerIndex") int correctAnswerIndex) {
        this.questionText = questionText;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
}
