package com.homeapp.javatraining.dto;

import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DTO Stats Tests")
class DtoStatsTest {

    // TopicStats Tests

    @Test
    @DisplayName("TopicStats - Should create with topic code and display name")
    void topicStats_constructor_shouldCreateWithCodeAndDisplayName() {
        // Act
        TopicStats stats = new TopicStats("java", "Java Programming");

        // Assert
        assertThat(stats.getTopicCode()).isEqualTo("java");
        assertThat(stats.getTopicDisplayName()).isEqualTo("Java Programming");
    }

    @Test
    @DisplayName("TopicStats - Should increment total correctly")
    void topicStats_incrementTotal_shouldIncreaseTotal() {
        // Arrange
        TopicStats stats = new TopicStats("java", "Java");

        // Act
        stats.incrementTotal();

        // Assert
        assertThat(stats.getTotal()).isEqualTo(1);
    }

    @Test
    @DisplayName("TopicStats - Should increment passed correctly")
    void topicStats_incrementPassed_shouldIncreasePassed() {
        // Arrange
        TopicStats stats = new TopicStats("java", "Java");

        // Act
        stats.incrementPassed();

        // Assert
        assertThat(stats.getPassed()).isEqualTo(1);
    }

    @Test
    @DisplayName("TopicStats - Should calculate success rate correctly")
    void topicStats_getSuccessRate_shouldCalculateCorrectly() {
        // Arrange
        TopicStats stats = new TopicStats("java", "Java");
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementPassed();
        stats.incrementPassed();
        stats.incrementPassed();

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(60); // 3/5 = 60%
    }

    @Test
    @DisplayName("TopicStats - Should return 0 success rate when total is 0")
    void topicStats_getSuccessRate_withZeroTotal_shouldReturnZero() {
        // Arrange
        TopicStats stats = new TopicStats("java", "Java");

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(0);
    }

    // UserStats Tests

    @Test
    @DisplayName("UserStats - Should create with username from user")
    void userStats_constructor_shouldCreateWithUsername() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);

        // Act
        UserStats stats = new UserStats(user);

        // Assert
        assertThat(stats.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("UserStats - Should increment total correctly")
    void userStats_incrementTotal_shouldIncreaseTotal() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        UserStats stats = new UserStats(user);

        // Act
        stats.incrementTotal();

        // Assert
        assertThat(stats.getTotal()).isEqualTo(1);
    }

    @Test
    @DisplayName("UserStats - Should increment passed correctly")
    void userStats_incrementPassed_shouldIncreasePassed() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        UserStats stats = new UserStats(user);

        // Act
        stats.incrementPassed();

        // Assert
        assertThat(stats.getPassed()).isEqualTo(1);
    }

    @Test
    @DisplayName("UserStats - Should calculate success rate correctly")
    void userStats_getSuccessRate_shouldCalculateCorrectly() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        UserStats stats = new UserStats(user);
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementPassed();
        stats.incrementPassed();

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(50); // 2/4 = 50%
    }

    // UserTopicStats Tests

    @Test
    @DisplayName("UserTopicStats - Should create with topic display name")
    void userTopicStats_constructor_shouldCreateWithTopicDisplayName() {
        // Act
        UserTopicStats stats = new UserTopicStats("Java Programming");

        // Assert
        assertThat(stats.getTopicDisplayName()).isEqualTo("Java Programming");
    }

    @Test
    @DisplayName("UserTopicStats - Should increment total correctly")
    void userTopicStats_incrementTotal_shouldIncreaseTotal() {
        // Arrange
        UserTopicStats stats = new UserTopicStats("Java");

        // Act
        stats.incrementTotal();

        // Assert
        assertThat(stats.getTotal()).isEqualTo(1);
    }

    @Test
    @DisplayName("UserTopicStats - Should increment passed correctly")
    void userTopicStats_incrementPassed_shouldIncreasePassed() {
        // Arrange
        UserTopicStats stats = new UserTopicStats("Java");

        // Act
        stats.incrementPassed();

        // Assert
        assertThat(stats.getPassed()).isEqualTo(1);
    }

    @Test
    @DisplayName("UserTopicStats - Should calculate success rate correctly")
    void userTopicStats_getSuccessRate_shouldCalculateCorrectly() {
        // Arrange
        UserTopicStats stats = new UserTopicStats("Java");
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementPassed();
        stats.incrementPassed();
        stats.incrementPassed();

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(100); // 3/3 = 100%
    }

    @Test
    @DisplayName("UserTopicStats - Should return 0 success rate when total is 0")
    void userTopicStats_getSuccessRate_withZeroTotal_shouldReturnZero() {
        // Arrange
        UserTopicStats stats = new UserTopicStats("Java");

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(0);
    }

    // BaseStats inherited behavior tests

    @Test
    @DisplayName("BaseStats - Should handle multiple increments")
    void baseStats_multipleIncrements_shouldWorkCorrectly() {
        // Arrange
        TopicStats stats = new TopicStats("java", "Java");

        // Act
        for (int i = 0; i < 10; i++) {
            stats.incrementTotal();
        }
        for (int i = 0; i < 7; i++) {
            stats.incrementPassed();
        }

        // Assert
        assertThat(stats.getTotal()).isEqualTo(10);
        assertThat(stats.getPassed()).isEqualTo(7);
        assertThat(stats.getSuccessRate()).isEqualTo(70);
    }

    @Test
    @DisplayName("BaseStats - Should handle 100% success rate")
    void baseStats_withPerfectScore_shouldReturn100Percent() {
        // Arrange
        UserStats stats = new UserStats(new User("user", "hash", "test@example.com", Role.USER));
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementPassed();
        stats.incrementPassed();
        stats.incrementPassed();

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(100);
    }

    @Test
    @DisplayName("BaseStats - Should handle 0% success rate")
    void baseStats_withZeroScore_shouldReturn0Percent() {
        // Arrange
        UserTopicStats stats = new UserTopicStats("Java");
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(0);
    }

    @Test
    @DisplayName("BaseStats - Should handle integer division")
    void baseStats_integerDivision_shouldRoundDown() {
        // Arrange
        TopicStats stats = new TopicStats("java", "Java");
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementPassed();

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(33); // 1/3 = 33.33% -> 33%
    }
}
