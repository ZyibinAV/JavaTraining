package com.homeapp.javatraining.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Topic Utils Tests")
class TopicUtilsTest {

    @Test
    @DisplayName("Should convert single topic code to display name")
    void convertTopicCodesToDisplayNames_withSingleCode_shouldReturnDisplayName() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames("java-syntax");

        // Assert
        assertThat(result).isEqualTo("Java Syntax");
    }

    @Test
    @DisplayName("Should convert multiple topic codes to display names")
    void convertTopicCodesToDisplayNames_withMultipleCodes_shouldReturnDisplayNames() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames("java-syntax,java-core");

        // Assert
        assertThat(result).isEqualTo("Java Syntax, Java Core");
    }

    @Test
    @DisplayName("Should handle null input")
    void convertTopicCodesToDisplayNames_withNullInput_shouldReturnEmptyString() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames(null);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty string input")
    void convertTopicCodesToDisplayNames_withEmptyInput_shouldReturnEmptyString() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames("");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle whitespace input")
    void convertTopicCodesToDisplayNames_withWhitespaceInput_shouldReturnEmptyString() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames("   ");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle unknown topic codes")
    void convertTopicCodesToDisplayNames_withUnknownCode_shouldIgnoreUnknown() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames("java-syntax,unknown-topic");

        // Assert
        assertThat(result).isEqualTo("Java Syntax");
    }

    @Test
    @DisplayName("Should handle extra spaces around codes")
    void convertTopicCodesToDisplayNames_withExtraSpaces_shouldTrim() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames(" java-syntax , java-core ");

        // Assert
        assertThat(result).isEqualTo("Java Syntax, Java Core");
    }

    @Test
    @DisplayName("Should handle all known topic codes")
    void convertTopicCodesToDisplayNames_withAllKnownCodes_shouldReturnAllNames() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames(
            "java-syntax,java-core,java-concurrency,servlets,maven,junit5,mockito,logging"
        );

        // Assert
        assertThat(result).isEqualTo("Java Syntax, Java Core, Java Concurrency, Сервлеты, Maven, JUnit 5, Mockito, Logging");
    }

    @Test
    @DisplayName("Should handle mix of known and unknown codes")
    void convertTopicCodesToDisplayNames_withMixedCodes_shouldReturnOnlyKnown() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames("java-syntax,unknown,java-core");

        // Assert
        assertThat(result).isEqualTo("Java Syntax, Java Core");
    }

    @Test
    @DisplayName("Should handle single unknown code")
    void convertTopicCodesToDisplayNames_withSingleUnknownCode_shouldReturnEmptyString() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames("unknown-topic");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle Russian display names")
    void convertTopicCodesToDisplayNames_withRussianDisplayName_shouldReturnRussian() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames("servlets");

        // Assert
        assertThat(result).isEqualTo("Сервлеты");
    }

    @Test
    @DisplayName("Should not add trailing comma when last code is unknown")
    void convertTopicCodesToDisplayNames_withUnknownLastCode_shouldNotAddTrailingComma() {
        // Act
        String result = TopicUtils.convertTopicCodesToDisplayNames("java-syntax,unknown");

        // Assert
        assertThat(result).isEqualTo("Java Syntax");
        assertThat(result).doesNotEndWith(",");
    }
}
