package com.homeapp.javatraining.model;

import java.util.List;
import java.util.Set;

public class InterviewState {
    private final Set<Topic> topics;
    private final List<Question> questions;
    private int currentIndex;
    private int score;

    public InterviewState(Set<Topic> topics, List<Question> questions) {
        this.topics = topics;
        this.questions = questions;
        this.currentIndex = 0;
        this.score = 0;
    }

    public Question getCurrentQuestion() {
        return questions.get(currentIndex);
    }

    public boolean isFinished() {
        return currentIndex >= questions.size();
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
