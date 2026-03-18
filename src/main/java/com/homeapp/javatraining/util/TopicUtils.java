package com.homeapp.javatraining.util;

import com.homeapp.javatraining.model.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TopicUtils {

    private static final Map<String, String> TOPIC_DISPLAY_NAMES = Map.of(
            "java-syntax", "Java Syntax",
            "java-core", "Java Core",
            "java-concurrency", "Java Concurrency",
            "servlets", "Сервлеты",
            "maven", "Maven",
            "junit5", "JUnit 5",
            "mockito", "Mockito",
            "logging", "Logging"
    );

    public static String convertTopicCodesToDisplayNames(String topicCodes) {
        if (topicCodes == null || topicCodes.trim().isEmpty()) {
            return "";
        }

        String[] codes = topicCodes.split(",");
        List<String> names = new ArrayList<>();

        for (String code : codes) {
            String displayName = TOPIC_DISPLAY_NAMES.get(code.trim());
            if (displayName != null) {
                names.add(displayName);
            }
        }

        return String.join(", ", names);
    }
}
