package com.homeapp.javatraining.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Test Result Model Tests")
class TestResultTest {

    @Test
    @DisplayName("Should create test result with constructor")
    void constructor_shouldCreateTestResultWithAllFields() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        LocalDateTime finishedAt = LocalDateTime.now();

        // Act
        TestResult result = new TestResult(user, topic, 10, 8, true, finishedAt);

        // Assert
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getTopic()).isEqualTo(topic);
        assertThat(result.getTotalQuestions()).isEqualTo(10);
        assertThat(result.getCorrectAnswers()).isEqualTo(8);
        assertThat(result.isPassed()).isTrue();
        assertThat(result.getFinishedAt()).isEqualTo(finishedAt);
    }

    @Test
    @DisplayName("Should return topic display name")
    void getTopicDisplayName_shouldReturnTopicDisplayName() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java Programming");
        TestResult result = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());

        // Act
        String displayName = result.getTopicDisplayName();

        // Assert
        assertThat(displayName).isEqualTo("Java Programming");
    }

    @Test
    @DisplayName("Should format finished at timestamp")
    void getFormattedFinishedAt_shouldReturnFormattedDate() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        LocalDateTime finishedAt = LocalDateTime.of(2026, 4, 14, 15, 30, 45);
        TestResult result = new TestResult(user, topic, 10, 8, true, finishedAt);

        // Act
        String formatted = result.getFormattedFinishedAt();

        // Assert
        assertThat(formatted).isEqualTo("14.04.2026 15:30:45");
    }

    @Test
    @DisplayName("Should be equal when ids are equal")
    void equals_withSameId_shouldReturnTrue() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result1 = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());
        TestResult result2 = new TestResult(user, topic, 5, 3, false, LocalDateTime.now());

        // Set ids via reflection for testing
        try {
            java.lang.reflect.Field idField = TestResult.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(result1, 1L);
            idField.set(result2, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Act & Assert
        assertThat(result1).isEqualTo(result2);
    }

    @Test
    @DisplayName("Should not be equal when ids are different")
    void equals_withDifferentId_shouldReturnFalse() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result1 = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());
        TestResult result2 = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());

        // Set ids via reflection for testing
        try {
            java.lang.reflect.Field idField = TestResult.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(result1, 1L);
            idField.set(result2, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Act & Assert
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    @DisplayName("Should not be equal when one id is null")
    void equals_withOneNullId_shouldReturnFalse() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result1 = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());
        TestResult result2 = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());

        // Set id only for result1
        try {
            java.lang.reflect.Field idField = TestResult.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(result1, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Act & Assert
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    @DisplayName("Should have same hash code when ids are equal")
    void hashCode_withSameId_shouldReturnSameHashCode() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result1 = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());
        TestResult result2 = new TestResult(user, topic, 5, 3, false, LocalDateTime.now());

        // Set ids via reflection for testing
        try {
            java.lang.reflect.Field idField = TestResult.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(result1, 1L);
            idField.set(result2, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Act & Assert
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("Should have different hash codes when ids are different")
    void hashCode_withDifferentId_shouldReturnDifferentHashCode() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result1 = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());
        TestResult result2 = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());

        // Set ids via reflection for testing
        try {
            java.lang.reflect.Field idField = TestResult.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(result1, 1L);
            idField.set(result2, 2L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Act & Assert
        assertThat(result1.hashCode()).isNotEqualTo(result2.hashCode());
    }

    @Test
    @DisplayName("Should be equal to itself")
    void equals_withSameInstance_shouldReturnTrue() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());

        // Act & Assert
        assertThat(result).isEqualTo(result);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void equals_withNull_shouldReturnFalse() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());

        // Act & Assert
        assertThat(result).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void equals_withDifferentType_shouldReturnFalse() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());

        // Act & Assert
        assertThat(result).isNotEqualTo("test");
    }

    @Test
    @DisplayName("Should handle null id in hash code")
    void hashCode_withNullId_shouldReturnIdentityHashCode() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());

        // Act
        int hashCode = result.hashCode();

        // Assert
        assertThat(hashCode).isEqualTo(System.identityHashCode(result));
    }

    @Test
    @DisplayName("Should have consistent equals and hashCode")
    void equalsAndHashCode_shouldBeConsistent() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result1 = new TestResult(user, topic, 10, 8, true, LocalDateTime.now());
        TestResult result2 = new TestResult(user, topic, 5, 3, false, LocalDateTime.now());

        // Set ids via reflection for testing
        try {
            java.lang.reflect.Field idField = TestResult.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(result1, 1L);
            idField.set(result2, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Act & Assert
        assertThat(result1).isEqualTo(result2);
        assertThat(result1.hashCode()).isEqualTo(result2.hashCode());
    }
}
