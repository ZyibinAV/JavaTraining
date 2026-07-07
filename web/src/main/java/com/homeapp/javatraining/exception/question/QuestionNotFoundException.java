package com.homeapp.javatraining.exception.question;

import com.homeapp.javatraining.exception.ApiException;

public class QuestionNotFoundException extends ApiException {
    public QuestionNotFoundException(Long questionId) {
        super("Question with id " + questionId + " not found");
    }

}
