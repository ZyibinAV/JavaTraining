package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.*;
import com.homeapp.javatraining.repository.TestResultRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Result Service Tests")
class TestResultServiceTest {

    @Mock
    private TestResultRepository testResultRepository;

    private TestResultService testResultService;

    @Test
    @DisplayName("Should process and save result with passing score")
    void processAndSaveResult_withPassingScore_shouldReturnPassedResult() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(10, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);
        
        // Set score to 7 out of 10 (70% - should pass)
        for (int i = 0; i < 7; i++) {
            state.incrementScore();
            state.moveToNextQuestion();
        }

        testResultService = new TestResultService(testResultRepository);

        // Act
        TestResult result = testResultService.processAndSaveResult(user, state);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getTopic()).isEqualTo(topic);
        assertThat(result.getTotalQuestions()).isEqualTo(10);
        assertThat(result.getCorrectAnswers()).isEqualTo(7);
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getFinishedAt()).isNotNull();
        assertThat(result.getFinishedAt()).isBeforeOrEqualTo(LocalDateTime.now());

        verify(testResultRepository).save(any(TestResult.class));
    }

    @Test
    @DisplayName("Should process and save result with failing score")
    void processAndSaveResult_withFailingScore_shouldReturnFailedResult() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(10, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);
        
        // Set score to 4 out of 10 (40% - should fail, threshold is 50%)
        for (int i = 0; i < 4; i++) {
            state.incrementScore();
            state.moveToNextQuestion();
        }

        testResultService = new TestResultService(testResultRepository);

        // Act
        TestResult result = testResultService.processAndSaveResult(user, state);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getTopic()).isEqualTo(topic);
        assertThat(result.getTotalQuestions()).isEqualTo(10);
        assertThat(result.getCorrectAnswers()).isEqualTo(4);
        assertThat(result.isPassed()).isFalse();

        verify(testResultRepository).save(any(TestResult.class));
    }

    @Test
    @DisplayName("Should throw exception when questions list is empty")
    void processAndSaveResult_withEmptyQuestions_shouldThrowException() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        List<Question> questions = new ArrayList<>();
        Set<Topic> topics = new HashSet<>();
        InterviewState state = new InterviewState(topics, questions);

        testResultService = new TestResultService(testResultRepository);

        // Act & Assert
        assertThatThrownBy(() -> testResultService.processAndSaveResult(user, state))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No questions in interview state");

        verify(testResultRepository, never()).save(any(TestResult.class));
    }

    @Test
    @DisplayName("Should calculate passed correctly at 50% threshold")
    void processAndSaveResult_withExactlyFiftyPercent_shouldPass() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(10, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);
        
        // Set score to 5 out of 10 (50% - should pass because condition is >= 50%)
        for (int i = 0; i < 5; i++) {
            state.incrementScore();
            state.moveToNextQuestion();
        }

        testResultService = new TestResultService(testResultRepository);

        // Act
        TestResult result = testResultService.processAndSaveResult(user, state);

        // Assert
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getCorrectAnswers()).isEqualTo(5);
        assertThat(result.getTotalQuestions()).isEqualTo(10);

        verify(testResultRepository).save(any(TestResult.class));
    }

    @Test
    @DisplayName("Should calculate passed correctly just below 50% threshold")
    void processAndSaveResult_withJustBelowFiftyPercent_shouldFail() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(10, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);
        
        // Set score to 4 out of 10 (40% - should fail)
        for (int i = 0; i < 4; i++) {
            state.incrementScore();
            state.moveToNextQuestion();
        }

        testResultService = new TestResultService(testResultRepository);

        // Act
        TestResult result = testResultService.processAndSaveResult(user, state);

        // Assert
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getCorrectAnswers()).isEqualTo(4);
        assertThat(result.getTotalQuestions()).isEqualTo(10);

        verify(testResultRepository).save(any(TestResult.class));
    }

    @Test
    @DisplayName("Should save result with correct timestamp")
    void processAndSaveResult_shouldSaveWithCurrentTimestamp() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(5, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);
        
        state.incrementScore();
        state.moveToNextQuestion();

        testResultService = new TestResultService(testResultRepository);
        LocalDateTime beforeTest = LocalDateTime.now();

        // Act
        TestResult result = testResultService.processAndSaveResult(user, state);
        LocalDateTime afterTest = LocalDateTime.now();

        // Assert
        assertThat(result.getFinishedAt()).isNotNull();
        assertThat(result.getFinishedAt()).isAfterOrEqualTo(beforeTest);
        assertThat(result.getFinishedAt()).isBeforeOrEqualTo(afterTest);
    }

    @Test
    @DisplayName("Should call repository save with correct parameters")
    void processAndSaveResult_shouldCallRepositorySaveWithCorrectResult() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(5, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);
        
        state.incrementScore();
        state.moveToNextQuestion();

        testResultService = new TestResultService(testResultRepository);
        ArgumentCaptor<TestResult> captor = ArgumentCaptor.forClass(TestResult.class);

        // Act
        testResultService.processAndSaveResult(user, state);

        // Assert
        verify(testResultRepository).save(captor.capture());
        TestResult savedResult = captor.getValue();
        
        assertThat(savedResult.getUser()).isEqualTo(user);
        assertThat(savedResult.getTopic()).isEqualTo(topic);
        assertThat(savedResult.getTotalQuestions()).isEqualTo(5);
        assertThat(savedResult.getCorrectAnswers()).isEqualTo(1);
        assertThat(savedResult.isPassed()).isFalse();
    }

    @Test
    @DisplayName("Should handle perfect score")
    void processAndSaveResult_withPerfectScore_shouldPass() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(10, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);
        
        // Set score to 10 out of 10 (100%)
        for (int i = 0; i < 10; i++) {
            state.incrementScore();
            state.moveToNextQuestion();
        }

        testResultService = new TestResultService(testResultRepository);

        // Act
        TestResult result = testResultService.processAndSaveResult(user, state);

        // Assert
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getCorrectAnswers()).isEqualTo(10);
        assertThat(result.getTotalQuestions()).isEqualTo(10);

        verify(testResultRepository).save(any(TestResult.class));
    }

    @Test
    @DisplayName("Should handle zero score")
    void processAndSaveResult_withZeroScore_shouldFail() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(10, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);
        
        // Don't increment score - 0 out of 10 (0%)

        testResultService = new TestResultService(testResultRepository);

        // Act
        TestResult result = testResultService.processAndSaveResult(user, state);

        // Assert
        assertThat(result.isPassed()).isFalse();
        assertThat(result.getCorrectAnswers()).isEqualTo(0);
        assertThat(result.getTotalQuestions()).isEqualTo(10);

        verify(testResultRepository).save(any(TestResult.class));
    }

    // Helper methods

    private List<Question> createQuestionList(int count, Topic topic) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Question question = new Question();
            question.setQuestionText("Question " + i);
            question.setTopic(topic);
            question.setAnswers(createAnswers(4));
            question.setCorrectAnswerIndex(i % 4);
            questions.add(question);
        }
        return questions;
    }

    private List<Answer> createAnswers(int count) {
        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Answer answer = new Answer();
            answer.setAnswerText("Answer " + i);
            answer.setAnswerIndex(i);
            answers.add(answer);
        }
        return answers;
    }
}
