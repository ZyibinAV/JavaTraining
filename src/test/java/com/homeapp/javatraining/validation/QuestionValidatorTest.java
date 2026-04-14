package com.homeapp.javatraining.validation;

import com.homeapp.javatraining.exception.ValidationException;
import com.homeapp.javatraining.model.Answer;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Question Validation Tests")
class QuestionValidatorTest {

    private final QuestionValidator questionValidator = new QuestionValidator();

    @Test
    @DisplayName("Should validate successful question list with valid data")
    void validate_withValidQuestionList_shouldNotThrow() {
        // Arrange
        List<Question> questions = createValidQuestionList(3);

        // Act & Assert
        QuestionValidator.validate(questions);
    }

    @Test
    @DisplayName("Should throw exception when question list is null")
    void validate_withNullList_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("list of questions is empty");
    }

    @Test
    @DisplayName("Should throw exception when question list is empty")
    void validate_withEmptyList_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(new ArrayList<>()))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("list of questions is empty");
    }

    @Test
    @DisplayName("Should throw exception when question in list is null")
    void validate_withNullQuestionInList_shouldThrowException() {
        // Arrange
        List<Question> questions = new ArrayList<>();
        questions.add(null);

        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(questions))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Index issue 0 equals null");
    }

    @Test
    @DisplayName("Should throw exception when question text is null")
    void validate_withNullQuestionText_shouldThrowException() {
        // Arrange
        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        question.setQuestionText(null);
        question.setAnswers(createValidAnswers(2));
        question.setCorrectAnswerIndex(0);
        questions.add(question);

        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(questions))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Blank question text");
    }

    @Test
    @DisplayName("Should throw exception when question text is blank")
    void validate_withBlankQuestionText_shouldThrowException() {
        // Arrange
        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        question.setQuestionText("   ");
        question.setAnswers(createValidAnswers(2));
        question.setCorrectAnswerIndex(0);
        questions.add(question);

        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(questions))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Blank question text");
    }

    @Test
    @DisplayName("Should throw exception when question text is empty")
    void validate_withEmptyQuestionText_shouldThrowException() {
        // Arrange
        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        question.setQuestionText("");
        question.setAnswers(createValidAnswers(2));
        question.setCorrectAnswerIndex(0);
        questions.add(question);

        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(questions))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Blank question text");
    }

    @Test
    @DisplayName("Should throw exception when answers list is null")
    void validate_withNullAnswers_shouldThrowException() {
        // Arrange
        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        question.setQuestionText("Test question");
        question.setAnswers(null);
        question.setCorrectAnswerIndex(0);
        questions.add(question);

        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(questions))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Answers list is empty");
    }

    @Test
    @DisplayName("Should throw exception when answers list is empty")
    void validate_withEmptyAnswers_shouldThrowException() {
        // Arrange
        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        question.setQuestionText("Test question");
        question.setAnswers(new ArrayList<>());
        question.setCorrectAnswerIndex(0);
        questions.add(question);

        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(questions))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Answers list is empty");
    }

    @Test
    @DisplayName("Should throw exception when correct answer index is negative")
    void validate_withNegativeCorrectAnswerIndex_shouldThrowException() {
        // Arrange
        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        question.setQuestionText("Test question");
        question.setAnswers(createValidAnswers(2));
        question.setCorrectAnswerIndex(-1);
        questions.add(question);

        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(questions))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Incorrect correctAnswerIndex");
    }

    @Test
    @DisplayName("Should throw exception when correct answer index is too large")
    void validate_withTooLargeCorrectAnswerIndex_shouldThrowException() {
        // Arrange
        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        question.setQuestionText("Test question");
        question.setAnswers(createValidAnswers(2));
        question.setCorrectAnswerIndex(2);
        questions.add(question);

        // Act & Assert
        assertThatThrownBy(() -> QuestionValidator.validate(questions))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Incorrect correctAnswerIndex");
    }

    @Test
    @DisplayName("Should validate when correct answer index is at boundary (last answer)")
    void validate_withCorrectAnswerIndexAtBoundary_shouldNotThrow() {
        // Arrange
        List<Question> questions = new ArrayList<>();
        Question question = new Question();
        question.setQuestionText("Test question");
        question.setAnswers(createValidAnswers(3));
        question.setCorrectAnswerIndex(2);
        questions.add(question);

        // Act & Assert
        QuestionValidator.validate(questions);
    }

    @Test
    @DisplayName("Should validate topics with valid array")
    void validateTopics_withValidArray_shouldNotThrow() {
        // Arrange
        String[] topics = {"java", "sql", "hibernate"};

        // Act & Assert
        questionValidator.validateTopics(topics);
    }

    @Test
    @DisplayName("Should throw exception when topics array is null")
    void validateTopics_withNullArray_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> questionValidator.validateTopics(null))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("select at least one topic");
    }

    @Test
    @DisplayName("Should throw exception when topics array is empty")
    void validateTopics_withEmptyArray_shouldThrowException() {
        // Arrange
        String[] topics = {};

        // Act & Assert
        assertThatThrownBy(() -> questionValidator.validateTopics(topics))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("select at least one topic");
    }

    @Test
    @DisplayName("Should throw exception when more than 5 topics selected")
    void validateTopics_withTooManyTopics_shouldThrowException() {
        // Arrange
        String[] topics = {"java", "sql", "hibernate", "spring", "junit", "mockito"};

        // Act & Assert
        assertThatThrownBy(() -> questionValidator.validateTopics(topics))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("no more than 5 topics");
    }

    @Test
    @DisplayName("Should validate when exactly 5 topics selected")
    void validateTopics_withExactly5Topics_shouldNotThrow() {
        // Arrange
        String[] topics = {"java", "sql", "hibernate", "spring", "junit"};

        // Act & Assert
        questionValidator.validateTopics(topics);
    }

    @Test
    @DisplayName("Should validate answer with valid index")
    void validateAnswer_withValidIndex_shouldNotThrow() {
        // Act & Assert
        questionValidator.validateAnswer("1", 2);
    }

    @Test
    @DisplayName("Should validate answer with index 0")
    void validateAnswer_withIndex0_shouldNotThrow() {
        // Act & Assert
        questionValidator.validateAnswer("0", 2);
    }

    @Test
    @DisplayName("Should throw exception when answer index is null")
    void validateAnswer_withNullIndex_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> questionValidator.validateAnswer(null, 2))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("select an answer");
    }

    @Test
    @DisplayName("Should throw exception when answer index is empty")
    void validateAnswer_withEmptyIndex_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> questionValidator.validateAnswer("", 2))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("select an answer");
    }

    @Test
    @DisplayName("Should throw exception when answer index is whitespace")
    void validateAnswer_withWhitespaceIndex_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> questionValidator.validateAnswer("   ", 2))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("select an answer");
    }

    @Test
    @DisplayName("Should throw exception when answer index is not a number")
    void validateAnswer_withNonNumericIndex_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> questionValidator.validateAnswer("abc", 2))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid answer format");
    }

    @Test
    @DisplayName("Should throw exception when answer index is negative")
    void validateAnswer_withNegativeIndex_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> questionValidator.validateAnswer("-1", 2))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid answer selected");
    }

    @Test
    @DisplayName("Should throw exception when answer index is too large")
    void validateAnswer_withTooLargeIndex_shouldThrowException() {
        // Act & Assert
        assertThatThrownBy(() -> questionValidator.validateAnswer("3", 2))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid answer selected");
    }

    @Test
    @DisplayName("Should validate answer at boundary (max index)")
    void validateAnswer_atBoundaryIndex_shouldNotThrow() {
        // Act & Assert
        questionValidator.validateAnswer("2", 2);
    }

    // Helper methods

    private List<Question> createValidQuestionList(int count) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Question question = new Question();
            question.setQuestionText("Question " + i);
            question.setAnswers(createValidAnswers(3));
            question.setCorrectAnswerIndex(1);
            questions.add(question);
        }
        return questions;
    }

    private List<Answer> createValidAnswers(int count) {
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
