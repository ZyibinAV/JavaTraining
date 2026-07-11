package com.homeapp.javatraining.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record TestStartRequest(
        @NotEmpty
        List<String> topics,

        @Min(1)
        int questionCount
) {
}