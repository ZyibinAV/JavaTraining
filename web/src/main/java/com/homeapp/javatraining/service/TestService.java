package com.homeapp.javatraining.service;


import com.homeapp.javatraining.dto.AnswerResult;
import com.homeapp.javatraining.dto.TestStartRequest;
import com.homeapp.javatraining.exception.ValidationException;
import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.util.TopicLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final QuestionService questionService;
    private final TopicLoader topicLoader;

    public InterviewState startTest(TestStartRequest request) {
        if (request.topics() == null || request.topics().isEmpty()) {
            throw new ValidationException("Select at least one topic.", "topics");
        }
        Set<Topic> topics = request.topics().stream()
                .map(topicLoader::findByCode)
                .collect(Collectors.toSet());

        List<Question> questions = questionService.getRandomQuestionsByTopics(
                request.topics(), request.questionCount());
        return new InterviewState(topics, questions);
    }

    public AnswerResult processAnswer(InterviewState state, int answerIndex) {
        Question current = state.getCurrentQuestion();
        boolean correct = answerIndex == current.getCorrectAnswerIndex();
        int correctAnswerIndex = current.getCorrectAnswerIndex();

        questionService.processAnswer(state, String.valueOf(answerIndex));
        return new AnswerResult(correct, correctAnswerIndex, state.isFinished(), state.getScore(), state.getTotalQuestions());
    }
}
