package com.homeapp.javatraining.util;

import com.homeapp.javatraining.handler.ErrorHandler;
import com.homeapp.javatraining.validation.QuestionValidator;
import com.homeapp.javatraining.validation.UserValidation;

public class ValidationFactory {

    private ValidationFactory() {
    }

    public static UserValidation createUserValidator() {
        return new UserValidation();
    }

    public static QuestionValidator createQuestionValidator() {
        return new QuestionValidator();
    }

    public static ErrorHandler createErrorHandler() {
        return new ErrorHandler();
    }
}
