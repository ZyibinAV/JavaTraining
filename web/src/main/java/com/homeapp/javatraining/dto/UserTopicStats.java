package com.homeapp.javatraining.dto;

public class UserTopicStats extends BaseStats {

    private final String topicDisplayName;

    public UserTopicStats(String topicDisplayName) {
        this.topicDisplayName = topicDisplayName;
    }

    public String getTopicDisplayName() {
        return topicDisplayName;
    }

}
