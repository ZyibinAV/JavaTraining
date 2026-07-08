package com.homeapp.javatraining.controller;

import com.homeapp.javatraining.dto.*;
import com.homeapp.javatraining.model.Answer;
import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.service.CurrentUserService;
import com.homeapp.javatraining.service.TestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final TestService testService;
    private final CurrentUserService currentUserService;

    private static final String SESSION_ATTR = "interviewState";

    @PostMapping("/start")
    public ResponseEntity<QuestionResponse> startTest(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody TestStartRequest request,
            HttpSession session) {

        log.debug("POST /api/test/start by user {}", currentUserService.getCurrentUserId(jwt));

        InterviewState state = testService.startTest(request);
        session.setAttribute(SESSION_ATTR, state);

        return ResponseEntity.ok(toQuestionResponse(state));
    }

    @GetMapping("/question")
    public ResponseEntity<QuestionResponse> getQuestion(
            @AuthenticationPrincipal Jwt jwt,
            HttpSession session) {

        log.debug("GET /api/test/question by user {}", currentUserService.getCurrentUserId(jwt));

        InterviewState state = getState(session);
        if (state == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(toQuestionResponse(state));
    }

    @PostMapping("/question")
    public ResponseEntity<AnswerResultResponse> answerQuestion(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AnswerRequest request,
            HttpSession session) {

        log.debug("POST /api/test/question by user {}", currentUserService.getCurrentUserId(jwt));

        InterviewState state = getState(session);
        if (state == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        AnswerResult result = testService.processAnswer(state, request.answerIndex());

        QuestionResponse nextQuestion = null;
        if (!result.finished()) {
            nextQuestion = toQuestionResponse(state);
        }

        AnswerResultResponse response = new AnswerResultResponse(
                result.correct(),
                result.correctAnswerIndex(),
                result.finished(),
                result.score(),
                result.totalQuestions(),
                nextQuestion
        );

        return ResponseEntity.ok(response);
    }

    private QuestionResponse toQuestionResponse(InterviewState state) {
        if (state.isFinished()) {
            return new QuestionResponse(null, null, null, 0,
                    state.getTotalQuestions(), state.getScore(), true);
        }
        Question q = state.getCurrentQuestion();
        return new QuestionResponse(
                q.getId(),
                q.getQuestionText(),
                toAnswerItems(q.getAnswers()),
                state.getCurrentIndex() + 1,
                state.getTotalQuestions(),
                state.getScore(),
                false
        );
    }

    private List<AnswerItem> toAnswerItems(List<Answer> answers) {
        return answers.stream()
                .map(a -> new AnswerItem(a.getAnswerIndex(), a.getAnswerText()))
                .collect(Collectors.toList());
    }

    private InterviewState getState(HttpSession session) {
        InterviewState state = (InterviewState) session.getAttribute(SESSION_ATTR);
        if (state == null || state.isExpired()) {
            if (state != null) {
                session.removeAttribute(SESSION_ATTR);
            }
            return null;
        }
        return state;
    }
}