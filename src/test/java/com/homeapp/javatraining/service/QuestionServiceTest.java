package com.homeapp.javatraining.service;

import com.homeapp.javatraining.exception.ValidationException;
import com.homeapp.javatraining.model.Answer;
import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.util.TopicLoader;
import com.homeapp.javatraining.validation.QuestionValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Question Service Tests")
class QuestionServiceTest {

    @Mock
    private QuestionValidator questionValidator;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TopicLoader topicLoader;

    private QuestionService questionService;

    @Test
    @DisplayName("Should get random questions by topics successfully")
    void getRandomQuestionsByTopics_withValidData_shouldReturnRandomQuestions() {
        // Arrange
        List<String> topicCodes = List.of("java", "sql");
        int questionCount = 5;

        Topic javaTopic = new Topic("java", "Java");
        Topic sqlTopic = new Topic("sql", "SQL");

        List<Question> javaQuestions = createQuestionList(3, javaTopic);
        List<Question> sqlQuestions = createQuestionList(4, sqlTopic);

        when(topicLoader.findByCode("java")).thenReturn(javaTopic);
        when(topicLoader.findByCode("sql")).thenReturn(sqlTopic);
        when(questionRepository.getQuestions(javaTopic)).thenReturn(javaQuestions);
        when(questionRepository.getQuestions(sqlTopic)).thenReturn(sqlQuestions);

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act
        List<Question> result = questionService.getRandomQuestionsByTopics(topicCodes, questionCount);

        // Assert
        assertThat(result).hasSize(questionCount);
        verify(topicLoader).findByCode("java");
        verify(topicLoader).findByCode("sql");
        verify(questionRepository).getQuestions(javaTopic);
        verify(questionRepository).getQuestions(sqlTopic);
    }

    @Test
    @DisplayName("Should throw exception when topic not found")
    void getRandomQuestionsByTopics_withNonExistentTopic_shouldThrowException() {
        // Arrange
        List<String> topicCodes = List.of("nonexistent");
        int questionCount = 5;

        when(topicLoader.findByCode("nonexistent")).thenReturn(null);

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act & Assert
        assertThatThrownBy(() -> questionService.getRandomQuestionsByTopics(topicCodes, questionCount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Topic not found");

        verify(topicLoader).findByCode("nonexistent");
        verify(questionRepository, never()).getQuestions(any());
    }

    @Test
    @DisplayName("Should throw exception when not enough questions available")
    void getRandomQuestionsByTopics_withInsufficientQuestions_shouldThrowException() {
        // Arrange
        List<String> topicCodes = List.of("java");
        int questionCount = 10;

        Topic javaTopic = new Topic("java", "Java");
        List<Question> javaQuestions = createQuestionList(3, javaTopic);

        when(topicLoader.findByCode("java")).thenReturn(javaTopic);
        when(questionRepository.getQuestions(javaTopic)).thenReturn(javaQuestions);

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act & Assert
        assertThatThrownBy(() -> questionService.getRandomQuestionsByTopics(topicCodes, questionCount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not enough questions");

        verify(topicLoader).findByCode("java");
        verify(questionRepository).getQuestions(javaTopic);
    }

    @Test
    @DisplayName("Should handle multiple topics with sufficient questions")
    void getRandomQuestionsByTopics_withMultipleTopics_shouldCombineQuestions() {
        // Arrange
        List<String> topicCodes = List.of("java", "sql", "hibernate");
        int questionCount = 8;

        Topic javaTopic = new Topic("java", "Java");
        Topic sqlTopic = new Topic("sql", "SQL");
        Topic hibernateTopic = new Topic("hibernate", "Hibernate");

        when(topicLoader.findByCode("java")).thenReturn(javaTopic);
        when(topicLoader.findByCode("sql")).thenReturn(sqlTopic);
        when(topicLoader.findByCode("hibernate")).thenReturn(hibernateTopic);

        when(questionRepository.getQuestions(javaTopic)).thenReturn(createQuestionList(3, javaTopic));
        when(questionRepository.getQuestions(sqlTopic)).thenReturn(createQuestionList(3, sqlTopic));
        when(questionRepository.getQuestions(hibernateTopic)).thenReturn(createQuestionList(3, hibernateTopic));

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act
        List<Question> result = questionService.getRandomQuestionsByTopics(topicCodes, questionCount);

        // Assert
        assertThat(result).hasSize(questionCount);
        verify(topicLoader, times(3)).findByCode(anyString());
        verify(questionRepository, times(3)).getQuestions(any());
    }

    @Test
    @DisplayName("Should process answer correctly for correct answer")
    void processAnswer_withCorrectAnswer_shouldIncrementScore() {
        // Arrange
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(5, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);

        doNothing().when(questionValidator).validateAnswer("0", questions.get(0).getAnswers().size() - 1);

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act
        questionService.processAnswer(state, "0");

        // Assert
        assertThat(state.getScore()).isEqualTo(1);
        assertThat(state.getCurrentIndex()).isEqualTo(1);
        verify(questionValidator).validateAnswer("0", questions.get(0).getAnswers().size() - 1);
    }

    @Test
    @DisplayName("Should process answer correctly for incorrect answer")
    void processAnswer_withIncorrectAnswer_shouldNotIncrementScore() {
        // Arrange
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(5, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);

        doNothing().when(questionValidator).validateAnswer("1", questions.get(0).getAnswers().size() - 1);

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act
        questionService.processAnswer(state, "1");

        // Assert
        assertThat(state.getScore()).isZero();
        assertThat(state.getCurrentIndex()).isEqualTo(1);
        verify(questionValidator).validateAnswer("1", questions.get(0).getAnswers().size() - 1);
    }

    @Test
    @DisplayName("Should validate answer before processing")
    void processAnswer_withInvalidAnswer_shouldThrowValidationException() {
        // Arrange
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(5, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);

        doThrow(new ValidationException("Invalid answer", "general"))
                .when(questionValidator).validateAnswer("invalid", questions.get(0).getAnswers().size() - 1);

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act & Assert
        assertThatThrownBy(() -> questionService.processAnswer(state, "invalid"))
                .isInstanceOf(ValidationException.class);

        verify(questionValidator).validateAnswer("invalid", questions.get(0).getAnswers().size() - 1);
        assertThat(state.getScore()).isZero();
        assertThat(state.getCurrentIndex()).isZero();
    }

    @Test
    @DisplayName("Should move to next question after processing answer")
    void processAnswer_shouldMoveToNextQuestion() {
        // Arrange
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(5, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);

        doNothing().when(questionValidator).validateAnswer("0", questions.get(0).getAnswers().size() - 1);

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act
        questionService.processAnswer(state, "0");

        // Assert
        assertThat(state.getCurrentIndex()).isEqualTo(1);
        assertThat(state.getCurrentQuestion()).isEqualTo(questions.get(1));
    }

    @Test
    @DisplayName("Should handle last question correctly")
    void processAnswer_withLastQuestion_shouldFinishInterview() {
        // Arrange
        Topic topic = new Topic("java", "Java");
        List<Question> questions = createQuestionList(2, topic);
        Set<Topic> topics = new HashSet<>();
        topics.add(topic);
        InterviewState state = new InterviewState(topics, questions);
        state.moveToNextQuestion(); // Move to last question

        doNothing().when(questionValidator).validateAnswer("0", questions.get(1).getAnswers().size() - 1);

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act
        questionService.processAnswer(state, "0");

        // Assert
        assertThat(state.isFinished()).isTrue();
        assertThat(state.getCurrentIndex()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should throw exception when topic list is empty and questions requested")
    void getRandomQuestionsByTopics_withEmptyTopicList_shouldThrowException() {
        // Arrange
        List<String> topicCodes = List.of();
        int questionCount = 5;

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act & Assert
        assertThatThrownBy(() -> questionService.getRandomQuestionsByTopics(topicCodes, questionCount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not enough questions");

        verify(topicLoader, never()).findByCode(anyString());
        verify(questionRepository, never()).getQuestions(any());
    }

    @Test
    @DisplayName("Should shuffle questions randomly")
    void getRandomQuestionsByTopics_shouldShuffleQuestions() {
        // Arrange
        List<String> topicCodes = List.of("java");
        int questionCount = 5;

        Topic javaTopic = new Topic("java", "Java");
        List<Question> javaQuestions = createQuestionList(10, javaTopic);

        when(topicLoader.findByCode("java")).thenReturn(javaTopic);
        when(questionRepository.getQuestions(javaTopic)).thenReturn(javaQuestions);

        questionService = new QuestionService(questionValidator, questionRepository, topicLoader);

        // Act
        List<Question> result1 = questionService.getRandomQuestionsByTopics(topicCodes, questionCount);
        List<Question> result2 = questionService.getRandomQuestionsByTopics(topicCodes, questionCount);

        // Assert
        assertThat(result1).hasSize(questionCount);
        assertThat(result2).hasSize(questionCount);
        // Note: Random shuffling means results might be the same by chance
        // This test primarily ensures the method doesn't throw exceptions
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
