package com.homeapp.javatraining.dto;

import java.util.List;

public record QuestionCreateRequest(
        String questionText,
        int correctAnswerIndex,
        List<String> answers
) {
}
