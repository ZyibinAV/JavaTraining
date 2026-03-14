package com.homeapp.javatraining.util;

import com.homeapp.javatraining.model.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicUtils {

    public static String convertTopicCodesToDisplayNames(String topicCodes) {
        if (topicCodes == null || topicCodes.trim().isEmpty()) {
            return "";
        }

        String[] codes = topicCodes.split(",");
        List<String> names = new ArrayList<>();

        for (String code : codes) {
            Topic topic = Topic.fromCode(code.trim());
            names.add(topic.getDisplayName());
        }

        return String.join(", ", names);
    }
}
