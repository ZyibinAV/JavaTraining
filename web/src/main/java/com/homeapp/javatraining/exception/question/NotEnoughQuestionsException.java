package com.homeapp.javatraining.exception.question;

import com.homeapp.javatraining.exception.ApiException;

public class NotEnoughQuestionsException extends ApiException {

    public NotEnoughQuestionsException(int requested, int available) {
        super("Requested " + requested + " questions, but only " + available + " are available");
    }
}
