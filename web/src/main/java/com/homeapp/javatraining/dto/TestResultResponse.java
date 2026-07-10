package com.homeapp.javatraining.dto;

public record TestResultResponse(
        int correctAnswers,
        int totalQuestions,
        boolean passed,
        double score) {
    public TestResultResponse {
        score = totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0;
    }
}
