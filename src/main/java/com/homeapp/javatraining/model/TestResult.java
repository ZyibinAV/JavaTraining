package com.homeapp.javatraining.model;

import com.homeapp.javatraining.util.TopicUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
@Getter
@NoArgsConstructor
@Entity
@Table(name = "test_results")
public class TestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;
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
}
