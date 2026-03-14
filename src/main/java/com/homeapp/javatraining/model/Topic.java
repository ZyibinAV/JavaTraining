package com.homeapp.javatraining.model;

public enum Topic {

    JAVA_SYNTAX("java-syntax", "Java Syntax"),
    JAVA_CORE("java-core", "Java Core"),
    JAVA_CONCURRENCY("java-concurrency", "Java Concurrency"),
    SERVLETS("servlets", "Сервлеты"),
    MAVEN("maven", "Maven"),
    JUNIT("junit5", "JUnit 5"),
    MOCKITO("mockito", "Mockito"),
    LOGGING("logging", "Logging");

    private final String code;
    private final String displayName;

    Topic(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Topic fromCode(String code) {
        for (Topic topic : values()) {
            if (topic.code.equals(code)) {
                return topic;
            }
        }
        throw new IllegalArgumentException("Unknown topic code: " + code);
    }
}
