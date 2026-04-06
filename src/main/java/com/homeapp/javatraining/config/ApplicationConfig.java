package com.homeapp.javatraining.config;


import com.homeapp.javatraining.repository.*;
import lombok.Getter;

@Getter
public class ApplicationConfig {
    private final UserRepository userRepository;
    private final TestResultRepository testResultRepository;
    private final QuestionRepository questionRepository;

    public  ApplicationConfig() {
        this.userRepository = new HibernateUserRepository();
        this.testResultRepository = new HibernateTestResultRepository();
        this.questionRepository = new HibernateQuestionRepository();
    }



}
