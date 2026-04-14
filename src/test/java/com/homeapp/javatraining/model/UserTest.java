package com.homeapp.javatraining.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Model Tests")
class UserTest {

    @Test
    @DisplayName("Should be equal when usernames are equal")
    void equals_withSameUsername_shouldReturnTrue() {
        // Arrange
        User user1 = new User("testuser", "hash1", "test1@example.com", Role.USER);
        User user2 = new User("testuser", "hash2", "test2@example.com", Role.ADMIN);

        // Act & Assert
        assertThat(user1).isEqualTo(user2);
    }

    @Test
    @DisplayName("Should not be equal when usernames are different")
    void equals_withDifferentUsername_shouldReturnFalse() {
        // Arrange
        User user1 = new User("user1", "hash1", "test1@example.com", Role.USER);
        User user2 = new User("user2", "hash2", "test2@example.com", Role.USER);

        // Act & Assert
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void equals_withSameInstance_shouldReturnTrue() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);

        // Act & Assert
        assertThat(user).isEqualTo(user);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void equals_withNull_shouldReturnFalse() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);

        // Act & Assert
        assertThat(user).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different type")
    void equals_withDifferentType_shouldReturnFalse() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);

        // Act & Assert
        assertThat(user).isNotEqualTo("testuser");
    }

    @Test
    @DisplayName("Should have same hash code when usernames are equal")
    void hashCode_withSameUsername_shouldReturnSameHashCode() {
        // Arrange
        User user1 = new User("testuser", "hash1", "test1@example.com", Role.USER);
        User user2 = new User("testuser", "hash2", "test2@example.com", Role.ADMIN);

        // Act & Assert
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    @DisplayName("Should have different hash codes when usernames are different")
    void hashCode_withDifferentUsername_shouldReturnDifferentHashCode() {
        // Arrange
        User user1 = new User("user1", "hash1", "test1@example.com", Role.USER);
        User user2 = new User("user2", "hash2", "test2@example.com", Role.USER);

        // Act & Assert
        assertThat(user1.hashCode()).isNotEqualTo(user2.hashCode());
    }

    @Test
    @DisplayName("Should handle null username in equals")
    void equals_withNullUsername_shouldReturnFalse() {
        // Arrange
        User user1 = new User("testuser", "hash1", "test1@example.com", Role.USER);
        User user2 = new User(null, "hash2", "test2@example.com", Role.USER);

        // Act & Assert
        assertThat(user1).isNotEqualTo(user2);
    }

    @Test
    @DisplayName("Should handle null username in hash code")
    void hashCode_withNullUsername_shouldReturnZero() {
        // Arrange
        User user = new User(null, "hash", "test@example.com", Role.USER);

        // Act & Assert
        assertThat(user.hashCode()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should add test result to user")
    void addTestResult_shouldAddResultToUser() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result = new TestResult(user, topic, 10, 8, true, java.time.LocalDateTime.now());

        // Act
        user.addTestResult(result);

        // Assert
        assertThat(user.getTestResults()).contains(result);
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Should remove test result from user")
    void removeTestResult_shouldRemoveResultFromUser() {
        // Arrange
        User user = new User("testuser", "hash", "test@example.com", Role.USER);
        Topic topic = new Topic("java", "Java");
        TestResult result = new TestResult(user, topic, 10, 8, true, java.time.LocalDateTime.now());
        user.addTestResult(result);

        // Act
        user.removeTestResult(result);

        // Assert
        assertThat(user.getTestResults()).doesNotContain(result);
        assertThat(result.getUser()).isNull();
    }

    @Test
    @DisplayName("Should initialize with default values in constructor")
    void constructor_shouldInitializeDefaultValues() {
        // Arrange & Act
        User user = new User("testuser", "hash", "test@example.com", Role.USER);

        // Assert
        assertThat(user.getNickname()).isEqualTo("testuser");
        assertThat(user.getAbout()).isEqualTo("");
        assertThat(user.getAvatarPath()).isEqualTo("/resources/avatars/default/default.png");
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.isBlocked()).isFalse();
    }

    @Test
    @DisplayName("Should have consistent equals and hashCode")
    void equalsAndHashCode_shouldBeConsistent() {
        // Arrange
        User user1 = new User("testuser", "hash1", "test1@example.com", Role.USER);
        User user2 = new User("testuser", "hash2", "test2@example.com", Role.ADMIN);

        // Act & Assert
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}
