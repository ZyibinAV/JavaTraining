package com.homeapp.javatraining.exception.topic;

import com.homeapp.javatraining.exception.ApiException;

public class TopicNotFoundException extends ApiException {

    public TopicNotFoundException(String topicCode) {
        super("Topic with code '" + topicCode + "' not found");
    }
}
