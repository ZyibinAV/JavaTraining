package com.homeapp.javatraining.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class InterviewState {

    private static final int SESSION_TIMEOUT_MINUTES = 60;

    private final Set<Topic> topics;
    private final List<Question> questions;
    private final LocalDateTime createdAt;
    private int currentIndex;
    private int score;

    public InterviewState(Set<Topic> topics, List<Question> questions) {
        this.topics = topics;
        this.questions = questions;
        this.currentIndex = 0;
        this.score = 0;
        this.createdAt = LocalDateTime.now();
    }

    public Question getCurrentQuestion() {
        return questions.get(currentIndex);
    }

    public boolean isFinished() {
        return currentIndex >= questions.size();
    }

    public boolean isExpired() {
        return createdAt.plusMinutes(SESSION_TIMEOUT_MINUTES).isBefore(LocalDateTime.now());
    }

    public void moveToNextQuestion() {
        currentIndex++;
    }

    public void incrementScore() {
        score++;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getScore() {
        return score;
    }
}
