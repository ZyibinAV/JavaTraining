package com.homeapp.javatraining.config;


import com.homeapp.javatraining.repository.*;

public class ApplicationConfig {
    private final UserRepository userRepository;
    private final TestResultRepository testResultRepository;
    private final QuestionRepository questionRepository;

    public  ApplicationConfig() {
        this.userRepository = new InMemoryUserRepository();
        this.testResultRepository = new InMemoryTestResultRepository();
        this.questionRepository = QuestionRepository.defaultRepository();
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public TestResultRepository getTestResultRepository() {
        return testResultRepository;
    }

    public QuestionRepository getQuestionRepository() {
        return questionRepository;
    }

}
