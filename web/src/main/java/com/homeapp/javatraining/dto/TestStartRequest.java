package com.homeapp.javatraining.dto;

import java.util.List;

public record TestStartRequest(
        List<String> topics,
        int questionCount
) {
}