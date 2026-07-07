package com.homeapp.javatraining.exception.question;

import com.homeapp.javatraining.exception.ApiException;

public class QuestionImportException extends ApiException {

    public QuestionImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
