package com.homeapp.javatraining.dto;

import com.homeapp.javatraining.model.Topic;

public class TopicStats extends BaseStats {

    private final String topicCode;
    private final String topicDisplayName;

    public TopicStats(String topicCode, String topicDisplayName) {
        this.topicCode = topicCode;
        this.topicDisplayName = topicDisplayName;
    }

    public String getTopicCode() {
        return topicCode;
    }

    public String getTopicDisplayName() {
        return topicDisplayName;
    }


}
