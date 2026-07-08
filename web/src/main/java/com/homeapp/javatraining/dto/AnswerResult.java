package com.homeapp.javatraining.dto;

public record AnswerResult(
        boolean correct,
        int correctAnswerIndex,
        boolean finished,
        int score,
        int totalQuestions
) {
}