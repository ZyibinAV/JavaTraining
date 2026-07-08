package com.homeapp.javatraining.dto;

import java.util.List;

public record QuestionResponse(
        Long questionId,
        String questionText,
        List<AnswerItem> answers,
        int questionNumber,
        int totalQuestions,
        int score,
        boolean finished) {
}