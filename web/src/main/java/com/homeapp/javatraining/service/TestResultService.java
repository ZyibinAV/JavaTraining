package com.homeapp.javatraining.service;

import com.homeapp.javatraining.dto.TestResultResponse;
import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestResultService {

    private final TestResultRepository testResultRepository;

    public TestResultResponse processResult(InterviewState state) {
        int correctAnswers = state.getScore();
        int totalQuestions = state.getTotalQuestions();
        boolean passed = correctAnswers * 2 >= totalQuestions;
        return new TestResultResponse(correctAnswers, totalQuestions, passed, totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100 : 0.0);
    }

    @Transactional
    public TestResult saveResult(User user, InterviewState state) {
        Set<Topic> topics = state.getTopics();
        if (topics.isEmpty()) {
            throw new IllegalStateException("InterviewState contains no topics");
        }
        int totalQuestions = state.getTotalQuestions();
        int correctAnswers = state.getScore();
        boolean passed = correctAnswers * 2 >= totalQuestions;

        TestResult result = new TestResult(user, topics, totalQuestions, correctAnswers, passed, LocalDateTime.now());
        testResultRepository.save(result);
        log.info("Test result saved for user {}", user.getUsername());
        return result;

    }

}
