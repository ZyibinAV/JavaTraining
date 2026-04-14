package com.homeapp.javatraining.service;

import com.homeapp.javatraining.dto.TopicStats;
import com.homeapp.javatraining.dto.UserStats;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.TestResultRepository;
import com.homeapp.javatraining.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Statistics Service Tests")
class AdminStatisticsServiceTest {

    @Mock
    private TestResultRepository testResultRepository;

    @Mock
    private UserRepository userRepository;

    private AdminStatisticsService adminStatisticsService;

    @Test
    @DisplayName("Should calculate statistics correctly with data")
    void getStatistics_withData_shouldCalculateCorrectly() {
        // Arrange
        List<User> users = createUsers(3);
        List<TestResult> results = createTestResults(users);

        when(userRepository.findAll()).thenReturn(users);
        when(testResultRepository.findAll()).thenReturn(results);

        adminStatisticsService = new AdminStatisticsService(testResultRepository, userRepository);

        // Act
        AdminStatisticsService.AdminStatisticsData stats = adminStatisticsService.getStatistics();

        // Assert
        assertThat(stats.getTotalTests()).isEqualTo(5);
        assertThat(stats.getPassedTests()).isEqualTo(3);
        assertThat(stats.getUserStats()).hasSize(3);
        assertThat(stats.getTopicStats()).hasSize(2);

        verify(userRepository).findAll();
        verify(testResultRepository).findAll();
    }

    @Test
    @DisplayName("Should handle empty data")
    void getStatistics_withEmptyData_shouldReturnEmptyStatistics() {
        // Arrange
        List<User> users = List.of();
        List<TestResult> results = List.of();

        when(userRepository.findAll()).thenReturn(users);
        when(testResultRepository.findAll()).thenReturn(results);

        adminStatisticsService = new AdminStatisticsService(testResultRepository, userRepository);

        // Act
        AdminStatisticsService.AdminStatisticsData stats = adminStatisticsService.getStatistics();

        // Assert
        assertThat(stats.getTotalTests()).isZero();
        assertThat(stats.getPassedTests()).isZero();
        assertThat(stats.getUserStats()).isEmpty();
        assertThat(stats.getTopicStats()).isEmpty();

        verify(userRepository).findAll();
        verify(testResultRepository).findAll();
    }

    @Test
    @DisplayName("Should calculate per-user statistics correctly")
    void getStatistics_shouldCalculatePerUserStatsCorrectly() {
        // Arrange
        List<User> users = createUsers(2);
        List<TestResult> results = createTestResults(users);

        when(userRepository.findAll()).thenReturn(users);
        when(testResultRepository.findAll()).thenReturn(results);

        adminStatisticsService = new AdminStatisticsService(testResultRepository, userRepository);

        // Act
        AdminStatisticsService.AdminStatisticsData stats = adminStatisticsService.getStatistics();

        // Assert
        assertThat(stats.getUserStats()).hasSize(2);

        // Find user stats for user 1
        UserStats user1Stats = stats.getUserStats().stream()
                .filter(s -> s.getUsername().equals("user1"))
                .findFirst()
                .orElse(null);

        assertThat(user1Stats).isNotNull();
        assertThat(user1Stats.getTotal()).isEqualTo(3);
        assertThat(user1Stats.getPassed()).isEqualTo(2);

        verify(userRepository).findAll();
        verify(testResultRepository).findAll();
    }

    @Test
    @DisplayName("Should calculate per-topic statistics correctly")
    void getStatistics_shouldCalculatePerTopicStatsCorrectly() {
        // Arrange
        List<User> users = createUsers(2);
        List<TestResult> results = createTestResults(users);

        when(userRepository.findAll()).thenReturn(users);
        when(testResultRepository.findAll()).thenReturn(results);

        adminStatisticsService = new AdminStatisticsService(testResultRepository, userRepository);

        // Act
        AdminStatisticsService.AdminStatisticsData stats = adminStatisticsService.getStatistics();

        // Assert
        assertThat(stats.getTopicStats()).hasSize(2);

        // Find topic stats for java
        TopicStats javaStats = stats.getTopicStats().stream()
                .filter(s -> s.getTopicCode().equals("java"))
                .findFirst()
                .orElse(null);

        assertThat(javaStats).isNotNull();
        assertThat(javaStats.getTotal()).isEqualTo(3);
        assertThat(javaStats.getPassed()).isEqualTo(1);

        verify(userRepository).findAll();
        verify(testResultRepository).findAll();
    }

    @Test
    @DisplayName("Should handle test results with null user")
    void getStatistics_withNullUser_shouldSkipResult() {
        // Arrange
        List<User> users = createUsers(2);
        List<TestResult> results = new ArrayList<>();

        // Add result with null user
        TestResult nullUserResult = new TestResult();
        nullUserResult.setTopic(new Topic("java", "Java"));
        nullUserResult.setTotalQuestions(5);
        nullUserResult.setCorrectAnswers(3);
        nullUserResult.setPassed(true);
        nullUserResult.setFinishedAt(LocalDateTime.now());
        results.add(nullUserResult);

        // Add normal results
        results.addAll(createTestResults(users));

        when(userRepository.findAll()).thenReturn(users);
        when(testResultRepository.findAll()).thenReturn(results);

        adminStatisticsService = new AdminStatisticsService(testResultRepository, userRepository);

        // Act
        AdminStatisticsService.AdminStatisticsData stats = adminStatisticsService.getStatistics();

        // Assert
        assertThat(stats.getTotalTests()).isEqualTo(6); // 5 normal + 1 null user
        assertThat(stats.getPassedTests()).isEqualTo(4); // 3 normal + 1 null user
        assertThat(stats.getUserStats()).hasSize(2); // Only 2 users have stats

        verify(userRepository).findAll();
        verify(testResultRepository).findAll();
    }

    @Test
    @DisplayName("Should handle test results with null topic")
    void getStatistics_withNullTopic_shouldSkipResult() {
        // Arrange
        List<User> users = createUsers(2);
        List<TestResult> results = new ArrayList<>();

        // Add result with null topic
        TestResult nullTopicResult = new TestResult();
        nullTopicResult.setUser(users.get(0));
        nullTopicResult.setTotalQuestions(5);
        nullTopicResult.setCorrectAnswers(3);
        nullTopicResult.setPassed(true);
        nullTopicResult.setFinishedAt(LocalDateTime.now());
        results.add(nullTopicResult);

        // Add normal results
        results.addAll(createTestResults(users));

        when(userRepository.findAll()).thenReturn(users);
        when(testResultRepository.findAll()).thenReturn(results);

        adminStatisticsService = new AdminStatisticsService(testResultRepository, userRepository);

        // Act
        AdminStatisticsService.AdminStatisticsData stats = adminStatisticsService.getStatistics();

        // Assert
        assertThat(stats.getTotalTests()).isEqualTo(6);
        assertThat(stats.getPassedTests()).isEqualTo(4);
        assertThat(stats.getTopicStats()).hasSize(2); // Only 2 topics have stats

        verify(userRepository).findAll();
        verify(testResultRepository).findAll();
    }

    @Test
    @DisplayName("Should calculate success rate correctly")
    void getStatistics_shouldCalculateSuccessRateCorrectly() {
        // Arrange
        List<User> users = createUsers(2);
        List<TestResult> results = createTestResults(users);

        when(userRepository.findAll()).thenReturn(users);
        when(testResultRepository.findAll()).thenReturn(results);

        adminStatisticsService = new AdminStatisticsService(testResultRepository, userRepository);

        // Act
        AdminStatisticsService.AdminStatisticsData stats = adminStatisticsService.getStatistics();

        // Assert
        assertThat(stats.getTotalTests()).isEqualTo(5);
        assertThat(stats.getPassedTests()).isEqualTo(3);

        // Check user stats success rates
        stats.getUserStats().forEach(userStats -> {
            if (userStats.getTotal() > 0) {
                assertThat(userStats.getSuccessRate()).isBetween(0, 101);
            }
        });

        // Check topic stats success rates
        stats.getTopicStats().forEach(topicStats -> {
            if (topicStats.getTotal() > 0) {
                assertThat(topicStats.getSuccessRate()).isBetween(0, 101);
            }
        });

        verify(userRepository).findAll();
        verify(testResultRepository).findAll();
    }

    @Test
    @DisplayName("Should handle all tests failed")
    void getStatistics_withAllFailed_shouldCalculateCorrectly() {
        // Arrange
        List<User> users = createUsers(2);
        List<TestResult> results = createFailedTestResults(users);

        when(userRepository.findAll()).thenReturn(users);
        when(testResultRepository.findAll()).thenReturn(results);

        adminStatisticsService = new AdminStatisticsService(testResultRepository, userRepository);

        // Act
        AdminStatisticsService.AdminStatisticsData stats = adminStatisticsService.getStatistics();

        // Assert
        assertThat(stats.getTotalTests()).isEqualTo(5);
        assertThat(stats.getPassedTests()).isZero();

        stats.getUserStats().forEach(userStats -> {
            assertThat(userStats.getPassed()).isZero();
            assertThat(userStats.getSuccessRate()).isZero();
        });

        verify(userRepository).findAll();
        verify(testResultRepository).findAll();
    }

    @Test
    @DisplayName("Should handle all tests passed")
    void getStatistics_withAllPassed_shouldCalculateCorrectly() {
        // Arrange
        List<User> users = createUsers(2);
        List<TestResult> results = createPassedTestResults(users);

        when(userRepository.findAll()).thenReturn(users);
        when(testResultRepository.findAll()).thenReturn(results);

        adminStatisticsService = new AdminStatisticsService(testResultRepository, userRepository);

        // Act
        AdminStatisticsService.AdminStatisticsData stats = adminStatisticsService.getStatistics();

        // Assert
        assertThat(stats.getTotalTests()).isEqualTo(5);
        assertThat(stats.getPassedTests()).isEqualTo(5);

        stats.getUserStats().forEach(userStats -> {
            if (userStats.getTotal() > 0) {
                assertThat(userStats.getSuccessRate()).isEqualTo(100);
            }
        });

        verify(userRepository).findAll();
        verify(testResultRepository).findAll();
    }

    // Helper methods

    private List<User> createUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User("user" + (i + 1), "password", "user" + (i + 1) + "@example.com", Role.USER);
            User spyUser = org.mockito.Mockito.spy(user);
            org.mockito.Mockito.doReturn((long) (i + 1)).when(spyUser).getId();
            users.add(spyUser);
        }
        return users;
    }

    private List<TestResult> createTestResults(List<User> users) {
        List<TestResult> results = new ArrayList<>();
        Topic javaTopic = new Topic("java", "Java");
        Topic sqlTopic = new Topic("sql", "SQL");

        // User 1: 3 tests, 2 passed
        results.add(createTestResult(users.get(0), javaTopic, 5, 3, true));
        results.add(createTestResult(users.get(0), sqlTopic, 5, 4, true));
        results.add(createTestResult(users.get(0), javaTopic, 5, 2, false));

        // User 2: 2 tests, 1 passed
        results.add(createTestResult(users.get(1), sqlTopic, 5, 3, true));
        results.add(createTestResult(users.get(1), javaTopic, 5, 2, false));

        return results;
    }

    private List<TestResult> createFailedTestResults(List<User> users) {
        List<TestResult> results = new ArrayList<>();
        Topic javaTopic = new Topic("java", "Java");
        Topic sqlTopic = new Topic("sql", "SQL");

        for (int i = 0; i < 5; i++) {
            User user = users.get(i % users.size());
            Topic topic = i % 2 == 0 ? javaTopic : sqlTopic;
            results.add(createTestResult(user, topic, 5, 2, false));
        }

        return results;
    }

    private List<TestResult> createPassedTestResults(List<User> users) {
        List<TestResult> results = new ArrayList<>();
        Topic javaTopic = new Topic("java", "Java");
        Topic sqlTopic = new Topic("sql", "SQL");

        for (int i = 0; i < 5; i++) {
            User user = users.get(i % users.size());
            Topic topic = i % 2 == 0 ? javaTopic : sqlTopic;
            results.add(createTestResult(user, topic, 5, 5, true));
        }

        return results;
    }

    private TestResult createTestResult(User user, Topic topic, int total, int correct, boolean passed) {
        TestResult result = new TestResult();
        result.setUser(user);
        result.setTopic(topic);
        result.setTotalQuestions(total);
        result.setCorrectAnswers(correct);
        result.setPassed(passed);
        result.setFinishedAt(LocalDateTime.now());
        return result;
    }
}
