package com.homeapp.javatraining.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(name = "display_name", nullable = false)
    private String displayName;

    public Topic(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof  Topic)) return false;
        Topic topic = (Topic) o;
        return id != null && id.equals(topic.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /*    JAVA_SYNTAX("java-syntax", "Java Syntax"),
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
    }*/
}
