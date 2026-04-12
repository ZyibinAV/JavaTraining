package com.homeapp.javatraining.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Index;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;
    @Column(nullable = false, unique = true)
    @Index(name = "idx_topics_code")
    private String code;
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Version
    private Long version;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<TestResult> testResults = new ArrayList<>();

    public Topic(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public void addQuestion(Question question) {
        questions.add(question);
        question.setTopic(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setTopic(null);
    }

    public void addTestResult(TestResult result) {
        testResults.add(result);
        result.setTopic(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;
        Topic topic = (Topic) o;
        return code != null && code.equals(topic.code);
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }


}
