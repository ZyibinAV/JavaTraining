package com.homeapp.javatraining.util;

import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.TopicRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Topic Loader Tests")
class TopicLoaderTest {

    @Mock
    private TopicRepository topicRepository;

    private TopicLoader topicLoader;

    @Test
    @DisplayName("Should load all topics successfully")
    void loadAllTopics_withExistingTopics_shouldReturnAllTopics() {
        // Arrange
        List<Topic> expectedTopics = List.of(
                new Topic("java", "Java"),
                new Topic("sql", "SQL")
        );
        when(topicRepository.findAll()).thenReturn(expectedTopics);

        topicLoader = new TopicLoader(topicRepository);

        // Act
        List<Topic> result = topicLoader.loadAllTopics();

        // Assert
        assertThat(result).isEqualTo(expectedTopics);
        verify(topicRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no topics exist")
    void loadAllTopics_withNoTopics_shouldReturnEmptyList() {
        // Arrange
        List<Topic> emptyList = List.of();
        when(topicRepository.findAll()).thenReturn(emptyList);

        topicLoader = new TopicLoader(topicRepository);

        // Act
        List<Topic> result = topicLoader.loadAllTopics();

        // Assert
        assertThat(result).isEmpty();
        verify(topicRepository).findAll();
    }

    @Test
    @DisplayName("Should find topic by code successfully")
    void findByCode_withExistingCode_shouldReturnTopic() {
        // Arrange
        String code = "java";
        Topic expectedTopic = new Topic(code, "Java");
        when(topicRepository.findByCode(code)).thenReturn(Optional.of(expectedTopic));

        topicLoader = new TopicLoader(topicRepository);

        // Act
        Topic result = topicLoader.findByCode(code);

        // Assert
        assertThat(result).isEqualTo(expectedTopic);
        verify(topicRepository).findByCode(code);
    }

    @Test
    @DisplayName("Should return null when topic not found by code")
    void findByCode_withNonExistentCode_shouldReturnNull() {
        // Arrange
        String code = "nonexistent";
        when(topicRepository.findByCode(code)).thenReturn(Optional.empty());

        topicLoader = new TopicLoader(topicRepository);

        // Act
        Topic result = topicLoader.findByCode(code);

        // Assert
        assertThat(result).isNull();
        verify(topicRepository).findByCode(code);
    }

    @Test
    @DisplayName("Should handle null code")
    void findByCode_withNullCode_shouldReturnNull() {
        // Arrange
        when(topicRepository.findByCode(null)).thenReturn(Optional.empty());

        topicLoader = new TopicLoader(topicRepository);

        // Act
        Topic result = topicLoader.findByCode(null);

        // Assert
        assertThat(result).isNull();
        verify(topicRepository).findByCode(null);
    }

    @Test
    @DisplayName("Should delegate to repository correctly")
    void loadAllTopics_shouldDelegateToRepository() {
        // Arrange
        List<Topic> expectedTopics = List.of(new Topic("spring", "Spring"));
        when(topicRepository.findAll()).thenReturn(expectedTopics);

        topicLoader = new TopicLoader(topicRepository);

        // Act
        List<Topic> result = topicLoader.loadAllTopics();

        // Assert
        verify(topicRepository, times(1)).findAll();
        assertThat(result).isSameAs(expectedTopics);
    }
}
