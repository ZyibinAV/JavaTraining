package com.homeapp.javatraining.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record QuestionCreateRequest(
        @NotBlank
        String questionText,

        @Min(0)
        int correctAnswerIndex,

        @NotEmpty
        @Size(min = 2)
        List<String> answers
) {
}
