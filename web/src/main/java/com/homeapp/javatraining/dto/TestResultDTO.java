package com.homeapp.javatraining.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record TestResultDTO(
        Long id,
        Long userId,
        Set<String> topicCodes,
        int totalQuestions,
        int correctAnswers,
        boolean passed,
        LocalDateTime finishedAt) {
}
