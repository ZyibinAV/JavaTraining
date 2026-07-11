package com.homeapp.javatraining.dto;

import java.util.List;

public record QuestionResponse(
        Long questionId,
        String questionText,
        List<AnswerDTO> answers,
        int questionNumber,
        int totalQuestions,
        int score,
        boolean finished) {
}