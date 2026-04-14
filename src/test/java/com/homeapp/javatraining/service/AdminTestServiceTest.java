package com.homeapp.javatraining.service;

import com.homeapp.javatraining.model.Answer;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.repository.TopicRepository;
import com.homeapp.javatraining.util.TopicLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Test Service Tests")
class AdminTestServiceTest {

    @Mock
    private TopicLoader topicLoader;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private QuestionRepository questionRepository;

    private AdminTestService adminTestService;

    @Test
    @DisplayName("Should get all topics successfully")
    void getAllTopics_shouldReturnAllTopics() {
        // Arrange
        List<Topic> expectedTopics = List.of(
                new Topic("java", "Java"),
                new Topic("sql", "SQL")
        );
        when(topicLoader.loadAllTopics()).thenReturn(expectedTopics);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        List<Topic> result = adminTestService.getAllTopics();

        // Assert
        assertThat(result).isEqualTo(expectedTopics);
        verify(topicLoader).loadAllTopics();
    }

    @Test
    @DisplayName("Should get topic by code successfully")
    void getTopicByCode_withValidCode_shouldReturnTopic() {
        // Arrange
        String code = "java";
        Topic expectedTopic = new Topic(code, "Java");
        when(topicLoader.findByCode(code)).thenReturn(expectedTopic);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        Topic result = adminTestService.getTopicByCode(code);

        // Assert
        assertThat(result).isEqualTo(expectedTopic);
        verify(topicLoader).findByCode(code);
    }

    @Test
    @DisplayName("Should return null when topic not found by code")
    void getTopicByCode_withNonExistentCode_shouldReturnNull() {
        // Arrange
        String code = "nonexistent";
        when(topicLoader.findByCode(code)).thenReturn(null);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        Topic result = adminTestService.getTopicByCode(code);

        // Assert
        assertThat(result).isNull();
        verify(topicLoader).findByCode(code);
    }

    @Test
    @DisplayName("Should create topic successfully")
    void createTopic_withValidData_shouldCreateTopic() {
        // Arrange
        String code = "spring";
        String displayName = "Spring Framework";

        Topic newTopic = new Topic(code, displayName);
        doNothing().when(topicRepository).save(any(Topic.class));

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        Topic result = adminTestService.createTopic(code, displayName);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(code.trim());
        assertThat(result.getDisplayName()).isEqualTo(displayName.trim());
        verify(topicRepository).save(any(Topic.class));
    }

    @Test
    @DisplayName("Should delete topic successfully")
    void deleteTopic_withValidCode_shouldDeleteTopic() {
        // Arrange
        String code = "java";
        Topic topic = new Topic(code, "Java");
        when(topicLoader.findByCode(code)).thenReturn(topic);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        adminTestService.deleteTopic(code);

        // Assert
        verify(topicLoader).findByCode(code);
        verify(topicRepository).delete(topic);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent topic")
    void deleteTopic_withNonExistentTopic_shouldThrowException() {
        // Arrange
        String code = "nonexistent";
        when(topicLoader.findByCode(code)).thenReturn(null);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act & Assert
        assertThatThrownBy(() -> adminTestService.deleteTopic(code))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic not found");

        verify(topicLoader).findByCode(code);
        verify(topicRepository, never()).delete(any(Topic.class));
    }

    @Test
    @DisplayName("Should get questions by topic successfully")
    void getQuestionsByTopic_withValidTopic_shouldReturnQuestions() {
        // Arrange
        String code = "java";
        Topic topic = new Topic(code, "Java");
        List<Question> expectedQuestions = createQuestionList(3, topic);

        when(topicLoader.findByCode(code)).thenReturn(topic);
        when(questionRepository.getQuestions(topic)).thenReturn(expectedQuestions);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        List<Question> result = adminTestService.getQuestionsByTopic(code);

        // Assert
        assertThat(result).isEqualTo(expectedQuestions);
        verify(topicLoader).findByCode(code);
        verify(questionRepository).getQuestions(topic);
    }

    @Test
    @DisplayName("Should throw exception when getting questions for non-existent topic")
    void getQuestionsByTopic_withNonExistentTopic_shouldThrowException() {
        // Arrange
        String code = "nonexistent";
        when(topicLoader.findByCode(code)).thenReturn(null);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act & Assert
        assertThatThrownBy(() -> adminTestService.getQuestionsByTopic(code))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic not found");

        verify(topicLoader).findByCode(code);
        verify(questionRepository, never()).getQuestions(any(Topic.class));
    }

    @Test
    @DisplayName("Should get question by id successfully")
    void getQuestionById_withValidId_shouldReturnQuestion() {
        // Arrange
        Long questionId = 1L;
        Question expectedQuestion = new Question();
        expectedQuestion.setId(questionId);
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(expectedQuestion));

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        var result = adminTestService.getQuestionById(questionId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedQuestion);
        verify(questionRepository).findById(questionId);
    }

    @Test
    @DisplayName("Should return empty when question not found by id")
    void getQuestionById_withNonExistentId_shouldReturnEmpty() {
        // Arrange
        Long questionId = 999L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        var result = adminTestService.getQuestionById(questionId);

        // Assert
        assertThat(result).isEmpty();
        verify(questionRepository).findById(questionId);
    }

    @Test
    @DisplayName("Should create question successfully")
    void createQuestion_withValidData_shouldCreateQuestion() {
        // Arrange
        String code = "java";
        String questionText = "What is Java?";
        int correctAnswerIndex = 0;
        List<String> answerTexts = List.of("A language", "A framework", "A database", "A tool");

        Topic topic = new Topic(code, "Java");
        when(topicLoader.findByCode(code)).thenReturn(topic);
        doAnswer(invocation -> invocation.getArgument(0)).when(questionRepository).save(any(Question.class));

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        Question result = adminTestService.createQuestion(code, questionText, correctAnswerIndex, answerTexts);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getQuestionText()).isEqualTo(questionText.trim());
        assertThat(result.getTopic()).isEqualTo(topic);
        assertThat(result.getAnswers()).hasSize(answerTexts.size());
        assertThat(result.getCorrectAnswerIndex()).isEqualTo(correctAnswerIndex);
        verify(topicLoader).findByCode(code);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("Should throw exception when creating question for non-existent topic")
    void createQuestion_withNonExistentTopic_shouldThrowException() {
        // Arrange
        String code = "nonexistent";
        String questionText = "What is Java?";
        int correctAnswerIndex = 0;
        List<String> answerTexts = List.of("A language", "A framework", "A database", "A tool");

        when(topicLoader.findByCode(code)).thenReturn(null);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act & Assert
        assertThatThrownBy(() -> adminTestService.createQuestion(code, questionText, correctAnswerIndex, answerTexts))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic not found");

        verify(topicLoader).findByCode(code);
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    @DisplayName("Should update question successfully")
    void updateQuestion_withValidData_shouldUpdateQuestion() {
        // Arrange
        Long questionId = 1L;
        String topicCode = "java";
        String newQuestionText = "Updated question text";
        int newCorrectAnswerIndex = 1;
        List<String> newAnswerTexts = List.of("Answer 1", "Answer 2", "Answer 3", "Answer 4");

        Question existingQuestion = new Question();
        existingQuestion.setId(questionId);

        Topic topic = new Topic(topicCode, "Java");

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(existingQuestion));
        when(topicLoader.findByCode(topicCode)).thenReturn(topic);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        adminTestService.updateQuestion(questionId, topicCode, newQuestionText, newCorrectAnswerIndex, newAnswerTexts);

        // Assert
        verify(questionRepository).findById(questionId);
        verify(topicLoader).findByCode(topicCode);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent question")
    void updateQuestion_withNonExistentQuestion_shouldThrowException() {
        // Arrange
        Long questionId = 999L;
        String topicCode = "java";
        String newQuestionText = "Updated question text";
        int newCorrectAnswerIndex = 1;
        List<String> newAnswerTexts = List.of("Answer 1", "Answer 2", "Answer 3", "Answer 4");

        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act & Assert
        assertThatThrownBy(() -> adminTestService.updateQuestion(questionId, topicCode, newQuestionText, newCorrectAnswerIndex, newAnswerTexts))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question not found");

        verify(questionRepository).findById(questionId);
        verify(topicLoader, never()).findByCode(anyString());
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    @DisplayName("Should throw exception when updating question with non-existent topic")
    void updateQuestion_withNonExistentTopic_shouldThrowException() {
        // Arrange
        Long questionId = 1L;
        String topicCode = "nonexistent";
        String newQuestionText = "Updated question text";
        int newCorrectAnswerIndex = 1;
        List<String> newAnswerTexts = List.of("Answer 1", "Answer 2", "Answer 3", "Answer 4");

        Question existingQuestion = new Question();
        existingQuestion.setId(questionId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(existingQuestion));
        when(topicLoader.findByCode(topicCode)).thenReturn(null);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act & Assert
        assertThatThrownBy(() -> adminTestService.updateQuestion(questionId, topicCode, newQuestionText, newCorrectAnswerIndex, newAnswerTexts))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic not found");

        verify(questionRepository).findById(questionId);
        verify(topicLoader).findByCode(topicCode);
        verify(questionRepository, never()).save(any(Question.class));
    }

    @Test
    @DisplayName("Should delete question successfully")
    void deleteQuestion_withValidId_shouldDeleteQuestion() {
        // Arrange
        Long questionId = 1L;
        Question question = new Question();
        question.setId(questionId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        adminTestService.deleteQuestion(questionId);

        // Assert
        verify(questionRepository).findById(questionId);
        verify(questionRepository).delete(question);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent question")
    void deleteQuestion_withNonExistentQuestion_shouldThrowException() {
        // Arrange
        Long questionId = 999L;
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act & Assert
        assertThatThrownBy(() -> adminTestService.deleteQuestion(questionId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Question not found");

        verify(questionRepository).findById(questionId);
        verify(questionRepository, never()).delete(any(Question.class));
    }

    @Test
    @DisplayName("Should import questions from JSON successfully")
    void importQuestionsFromJson_withValidData_shouldImportQuestions() {
        // Arrange
        String topicCode = "java";
        String json = "[{\"questionText\":\"What is Java?\",\"correctAnswerIndex\":0,\"answers\":[\"A language\",\"A framework\",\"A database\",\"A tool\"]}]";
        InputStream jsonStream = new ByteArrayInputStream(json.getBytes());

        Topic topic = new Topic(topicCode, "Java");
        when(topicLoader.findByCode(topicCode)).thenReturn(topic);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act
        List<Question> result = adminTestService.importQuestionsFromJson(topicCode, jsonStream);

        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getQuestionText()).isEqualTo("What is Java?");
        verify(topicLoader).findByCode(topicCode);
        verify(questionRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw exception when importing for non-existent topic")
    void importQuestionsFromJson_withNonExistentTopic_shouldThrowException() {
        // Arrange
        String topicCode = "nonexistent";
        String json = "[{\"questionText\":\"What is Java?\",\"correctAnswerIndex\":0,\"answers\":[\"A language\",\"A framework\",\"A database\",\"A tool\"]}]";
        InputStream jsonStream = new ByteArrayInputStream(json.getBytes());

        when(topicLoader.findByCode(topicCode)).thenReturn(null);

        adminTestService = new AdminTestService(topicLoader, topicRepository, questionRepository);

        // Act & Assert
        assertThatThrownBy(() -> adminTestService.importQuestionsFromJson(topicCode, jsonStream))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic not found");

        verify(topicLoader).findByCode(topicCode);
        verify(questionRepository, never()).saveAll(anyList());
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
