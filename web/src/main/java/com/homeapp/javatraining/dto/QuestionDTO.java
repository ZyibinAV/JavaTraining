package com.homeapp.javatraining.dto;

import java.util.List;

public record QuestionDTO(
        Long id,
        String questionText,
        int correctAnswerIndex,
        String topicCode,
        List<AnswerDTO> answers) {
}
