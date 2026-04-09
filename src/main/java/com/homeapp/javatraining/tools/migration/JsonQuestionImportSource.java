package com.homeapp.javatraining.tools.migration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeapp.javatraining.model.Answer;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.util.ValidationFactory;
import com.homeapp.javatraining.validation.QuestionValidator;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonQuestionImportSource implements JsonQuestionSource {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String QUESTIONS_FOLDER = "questions/";


    @Override
    public List<Question> loadQuestions(Topic topic) {
        String fileName = QUESTIONS_FOLDER + topic.getCode() + ".json";

        log.debug("Loading questions from file={}", fileName);

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);

        if (inputStream == null) {
            log.error("Questions file not found: {}", fileName);
            throw new IllegalStateException("File not found in resources: " + fileName);
        }

        try {
            List<Map<String, Object>> rawQuestions = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Map<String, Object>>>() {
                    }
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
            QuestionValidator questionValidator = ValidationFactory.createQuestionValidator();
            questionValidator.validate(questions);
            log.info("Questions loaded successfully: topic={}, count={}", topic, questions.size());
            return questions;
        } catch (Exception e) {
            log.error("Error reading questions file: {}", fileName, e);
            throw new IllegalStateException("Error reading: " + fileName, e);
        }
    }
}
