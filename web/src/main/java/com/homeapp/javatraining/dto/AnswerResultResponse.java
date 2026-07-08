package com.homeapp.javatraining.dto;

import java.util.List;

public record AnswerResultResponse(
        boolean correct,
        int correctAnswerIndex,
        boolean finished,
        int score,
        int totalQuestions,
        QuestionResponse nextQuestion
) {
}