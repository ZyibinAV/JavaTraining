package com.homeapp.javatraining.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Index;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "test_results")
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @Index(name = "idx_test_results_user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    @Index(name = "idx_test_results_topic_id")
    private Topic topic;

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
                      Topic topic,
                      int totalQuestions,
                      int correctAnswers,
                      boolean passed,
                      LocalDateTime finishedAt) {
        this.user = user;
        this.topic = topic;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.passed = passed;
        this.finishedAt = finishedAt;

    }

    public String getTopicDisplayName() {
        return topic.getDisplayName();
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
