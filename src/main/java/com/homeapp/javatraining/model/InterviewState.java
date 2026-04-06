package com.homeapp.javatraining.model;

import java.util.List;
import java.util.Set;

public class InterviewState {

    private final Set<String> topicCodes;
    private final List<Question> questions;
    private int currentIndex;
    private int score;

    public InterviewState(Set<String> topicCodes, List<Question> questions) {
        this.topicCodes = topicCodes;
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

    public Set<String> getTopicCodes() {
        return topicCodes;
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
