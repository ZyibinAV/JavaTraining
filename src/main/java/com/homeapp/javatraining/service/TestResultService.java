package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.*;
import com.homeapp.javatraining.repository.TestResultRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class TestResultService {

    private final TestResultRepository testResultRepository;

    public TestResultService(TestResultRepository testResultRepository) {
        this.testResultRepository = testResultRepository;
    }

    public TestResult processAndSaveResult(User user, InterviewState state) {

        int totalQuestions = state.getTotalQuestions();
        int correctAnswers = state.getScore();

        boolean passed = correctAnswers * 2 >= totalQuestions;

        List<Question> questions = state.getQuestions();
        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions in interview state");
        }
        Topic topic = questions.get(0).getTopic();
        TestResult result = new TestResult(
                user,
                topic,
                totalQuestions,
                correctAnswers,
                passed,
                LocalDateTime.now()
        );
        testResultRepository.save(result);
        log.info("Test result saved for user {}", user.getUsername());
        return result;
    }
}
