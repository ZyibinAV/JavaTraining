package com.homeapp.javatraining.dto;

import java.util.List;

public record QuestionUpdateRequest(
        String questionText,
        int correctAnswerIndex,
        List<String> answers
) {

}
