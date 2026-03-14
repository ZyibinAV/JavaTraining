package com.homeapp.javatraining.dto;

import com.homeapp.javatraining.model.Topic;

public class TopicStats extends BaseStats {

    private final String topicCode;
    private String topicDisplayName;


    public TopicStats(String topicCode) {
        this.topicCode = topicCode;
        Topic topic = Topic.fromCode(topicCode);
        this.topicDisplayName = topic.getDisplayName();
    }

    public String getTopicCode() {
        return topicCode;
    }

    public String getTopicDisplayName() {
        return topicDisplayName;
    }

}
