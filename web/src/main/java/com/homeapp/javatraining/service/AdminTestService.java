package com.homeapp.javatraining.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeapp.javatraining.model.Answer;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.repository.TopicRepository;
import com.homeapp.javatraining.util.TopicLoader;
import com.homeapp.javatraining.validation.QuestionValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTestService {

    private final TopicLoader topicLoader;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();



    public List<Topic> getAllTopics() {
        return topicLoader.loadAllTopics();
    }

    public Topic getTopicByCode(String topicCode) {
        return topicLoader.findByCode(topicCode);
    }

    public Topic createTopic(String code, String displayName) {
        Topic topic = new Topic(code.trim(), displayName.trim());
        topicRepository.save(topic);
        log.info("Topic created: {}", code);
        return topic;
    }

    public void deleteTopic(String topicCode) {
        Topic topic = topicLoader.findByCode(topicCode);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicCode);
        }
        topicRepository.delete(topic);
        log.info("Topic deleted: {}", topicCode);
    }

    public List<Question> getQuestionsByTopic(String topicCode) {
        Topic topic = topicLoader.findByCode(topicCode);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicCode);
        }
        return questionRepository.findByTopic(topic);
    }

    public Optional<Question> getQuestionById(Long questionId) {
        return questionRepository.findById(questionId);
    }

    public Question createQuestion(String topicCode, String questionText, 
                                  int correctAnswerIndex, List<String> answerTexts) {
        Topic topic = topicLoader.findByCode(topicCode);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicCode);
        }

        Question question = new Question();
        question.setQuestionText(questionText.trim());
        question.setCorrectAnswerIndex(correctAnswerIndex);
        question.setTopic(topic);

        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < answerTexts.size(); i++) {
            Answer answer = new Answer();
            answer.setAnswerText(answerTexts.get(i).trim());
            answer.setAnswerIndex(i);
            answer.setQuestion(question);
            answers.add(answer);
        }
        question.setAnswers(answers);

        validateQuestion(question);
        questionRepository.save(question);
        log.info("Question created for topic: {}", topicCode);
        return question;
    }

    public void updateQuestion(Long questionId, String topicCode, String questionText,
                              int correctAnswerIndex, List<String> answerTexts) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        Topic topic = topicLoader.findByCode(topicCode);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicCode);
        }

        question.setQuestionText(questionText.trim());
        question.setCorrectAnswerIndex(correctAnswerIndex);
        question.setTopic(topic);

        List<Answer> answers = new ArrayList<>();
        for (int i = 0; i < answerTexts.size(); i++) {
            Answer answer = new Answer();
            answer.setAnswerText(answerTexts.get(i).trim());
            answer.setAnswerIndex(i);
            answer.setQuestion(question);
            answers.add(answer);
        }
        question.setAnswers(answers);

        validateQuestion(question);
        questionRepository.save(question);
        log.info("Question updated: id={}", questionId);
    }

    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        questionRepository.delete(question);
        log.info("Question deleted: id={}", questionId);
    }

    public List<Question> importQuestionsFromJson(String topicCode, InputStream jsonStream) {
        Topic topic = topicLoader.findByCode(topicCode);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicCode);
        }

        try {
            List<Map<String, Object>> rawQuestions = objectMapper.readValue(
                    jsonStream,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            List<Question> questions = new ArrayList<>();
            for (Map<String, Object> raw : rawQuestions) {
                Question question = new Question();
                question.setQuestionText((String) raw.get("questionText"));
                question.setCorrectAnswerIndex((Integer) raw.get("correctAnswerIndex"));
                question.setTopic(topic);

                List<String> rawAnswers = (List<String>) raw.get("answers");
                List<Answer> answers = new ArrayList<>();

                for (int i = 0; i < rawAnswers.size(); i++) {
                    Answer answer = new Answer();
                    answer.setAnswerText(rawAnswers.get(i));
                    answer.setAnswerIndex(i);
                    answer.setQuestion(question);
                    answers.add(answer);
                }
                question.setAnswers(answers);
                questions.add(question);
            }

            try {
                validateQuestions(questions);
            } catch (Exception e) {
                log.error("Validation failed for questions", e);
                throw new RuntimeException("Validation failed: " + e.getMessage(), e);
            }
            questionRepository.saveAll(questions);
            log.info("JSON imported for topic: {}, questions count: {}", topicCode, questions.size());
            return questions;

        } catch (Exception e) {
            log.error("Error importing JSON for topic: {}", topicCode, e);
            throw new RuntimeException("Error importing JSON: " + e.getMessage(), e);
        }
    }

    private void validateQuestion(Question question) {
        QuestionValidator.validate(List.of(question));
    }

    private void validateQuestions(List<Question> questions) {
        QuestionValidator.validate(questions);
    }
}
