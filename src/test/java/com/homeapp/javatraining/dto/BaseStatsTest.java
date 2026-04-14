package com.homeapp.javatraining.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Base Stats Tests")
class BaseStatsTest {

    @Test
    @DisplayName("Should increment total correctly")
    void incrementTotal_shouldIncreaseTotal() {
        // Arrange
        BaseStats stats = createBaseStats(5, 3);

        // Act
        stats.incrementTotal();

        // Assert
        assertThat(stats.getTotal()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should increment passed correctly")
    void incrementPassed_shouldIncreasePassed() {
        // Arrange
        BaseStats stats = createBaseStats(5, 3);

        // Act
        stats.incrementPassed();

        // Assert
        assertThat(stats.getPassed()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should calculate success rate correctly")
    void getSuccessRate_withMixedResults_shouldReturnCorrectRate() {
        // Arrange
        BaseStats stats = createBaseStats(10, 7);

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(70);
    }

    @Test
    @DisplayName("Should return 0 success rate when total is 0")
    void getSuccessRate_withZeroTotal_shouldReturn0() {
        // Arrange
        BaseStats stats = createBaseStats(0, 0);

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isZero();
    }

    @Test
    @DisplayName("Should return 100 success rate when all passed")
    void getSuccessRate_whenAllPassed_shouldReturn100() {
        // Arrange
        BaseStats stats = createBaseStats(10, 10);

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(100);
    }

    @Test
    @DisplayName("Should return 0 success rate when none passed")
    void getSuccessRate_whenNonePassed_shouldReturn0() {
        // Arrange
        BaseStats stats = createBaseStats(10, 0);

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isZero();
    }

    @Test
    @DisplayName("Should calculate success rate with rounding down")
    void getSuccessRate_withRoundingDown_shouldRoundDown() {
        // Arrange
        BaseStats stats = createBaseStats(3, 1);

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(33); // 1/3 = 33.33%, rounds down
    }

    @Test
    @DisplayName("Should calculate success rate with rounding up")
    void getSuccessRate_withRoundingUp_shouldRoundUp() {
        // Arrange
        BaseStats stats = createBaseStats(3, 2);

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(66); // 2/3 = 66.66%, rounds down
    }

    @Test
    @DisplayName("Should handle large numbers correctly")
    void getSuccessRate_withLargeNumbers_shouldCalculateCorrectly() {
        // Arrange
        BaseStats stats = createBaseStats(1000, 750);

        // Act
        int successRate = stats.getSuccessRate();

        // Assert
        assertThat(successRate).isEqualTo(75);
    }

    @Test
    @DisplayName("Should increment total multiple times")
    void incrementTotal_multipleTimes_shouldIncreaseCorrectly() {
        // Arrange
        BaseStats stats = createBaseStats(5, 3);

        // Act
        stats.incrementTotal();
        stats.incrementTotal();
        stats.incrementTotal();

        // Assert
        assertThat(stats.getTotal()).isEqualTo(8);
    }

    @Test
    @DisplayName("Should increment passed multiple times")
    void incrementPassed_multipleTimes_shouldIncreaseCorrectly() {
        // Arrange
        BaseStats stats = createBaseStats(5, 3);

        // Act
        stats.incrementPassed();
        stats.incrementPassed();
        stats.incrementPassed();

        // Assert
        assertThat(stats.getPassed()).isEqualTo(6);
    }

    @Test
    @DisplayName("Should handle incrementing passed beyond total")
    void incrementPassed_beyondTotal_shouldAllow() {
        // Arrange
        BaseStats stats = createBaseStats(5, 3);

        // Act
        stats.incrementPassed();
        stats.incrementPassed();
        stats.incrementPassed();

        // Assert
        assertThat(stats.getPassed()).isEqualTo(6);
        assertThat(stats.getTotal()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should return correct total value")
    void getTotal_shouldReturnCorrectValue() {
        // Arrange
        BaseStats stats = createBaseStats(15, 10);

        // Act & Assert
        assertThat(stats.getTotal()).isEqualTo(15);
    }

    @Test
    @DisplayName("Should return correct passed value")
    void getPassed_shouldReturnCorrectValue() {
        // Arrange
        BaseStats stats = createBaseStats(15, 10);

        // Act & Assert
        assertThat(stats.getPassed()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should handle initial zero values")
    void constructor_withDefaultValues_shouldStartAtZero() {
        // Arrange
        BaseStats stats = createBaseStats(0, 0);

        // Act & Assert
        assertThat(stats.getTotal()).isZero();
        assertThat(stats.getPassed()).isZero();
    }

    // Helper method

    private BaseStats createBaseStats(int total, int passed) {
        BaseStats stats = new BaseStats() {};
        stats.total = total;
        stats.passed = passed;
        return stats;
    }
}
