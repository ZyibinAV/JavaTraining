package com.homeapp.javatraining.dto;

import jakarta.validation.constraints.Min;

public record AnswerRequest(
        @Min(0)
        int answerIndex) {
}