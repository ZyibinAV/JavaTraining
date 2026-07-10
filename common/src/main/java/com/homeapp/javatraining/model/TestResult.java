package com.homeapp.javatraining.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "test_results", indexes = {
        @Index(name = "idx_test_results_user_id", columnList = "user_id"),
})
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(name = "test_results_topics",
    joinColumns = @JoinColumn(name = "test_result_id"),
    inverseJoinColumns = @JoinColumn(name = "topic_id"))
    private Set<Topic> topics = new HashSet<>();

    @Version
    private Long version;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;
    @Column(name = "correct_answers", nullable = false)
    private int correctAnswers;
    @Column(name = "passed", nullable = false)
    private boolean passed;
    @Column(name = "finished_at", nullable = false)
    private LocalDateTime finishedAt;

    public TestResult(User user,
                      Set<Topic> topics,
                      int totalQuestions,
                      int correctAnswers,
                      boolean passed,
                      LocalDateTime finishedAt) {
        this.user = user;
        this.topics = topics;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.passed = passed;
        this.finishedAt = finishedAt;
    }

    public String getTopicDisplayName() {
        return topics.stream()
                .map(Topic::getDisplayName)
                .collect(Collectors.joining(", "));
    }

    public String getFormattedFinishedAt() {
        return finishedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestResult)) return false;
        TestResult testResult = (TestResult) o;
        if (id != null && testResult.id != null) {
            return id.equals(testResult.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }
}
