package com.homeapp.javatraining.model;

import com.homeapp.javatraining.util.TopicUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestResult {

    private long id;
    private long userId;
    private String topicCode;
    private int totalQuestions;
    private int correctAnswers;
    private boolean passed;
    private LocalDateTime finishedAt;

    public TestResult(long userId,
                      String topicCode,
                      int totalQuestions,
                      int correctAnswers,
                      boolean passed,
                      LocalDateTime finishedAt) {
        this.userId = userId;
        this.topicCode = topicCode;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.passed = passed;
        this.finishedAt = finishedAt;
    }

    public void setId(long id) {
        if (this.id != 0) {
            throw new IllegalStateException("id is already set");
        }
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getTopicCode() {
        return topicCode;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public boolean isPassed() {
        return passed;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public String getTopicDisplayName() {
        return TopicUtils.convertTopicCodesToDisplayNames(topicCode);
    }

    public String getFormattedFinishedAt() {
        return finishedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }
}
