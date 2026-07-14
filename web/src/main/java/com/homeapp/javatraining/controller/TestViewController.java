package com.homeapp.javatraining.controller;

import com.homeapp.javatraining.dto.*;
import com.homeapp.javatraining.dto.mapper.TopicMapper;
import com.homeapp.javatraining.model.Answer;
import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestViewController {

    private final TestService testService;
    private final UserService userService;
    private final TestResultService testResultService;
    private final CurrentUserService currentUserService;
    private final AdminTestService adminTestService;
    private final TopicMapper topicMapper;

    private static final String SESSION_ATTR = "interviewState";

    @GetMapping("/settings")
    public String settingsForm(Model model) {
        List<TopicDTO> topics = topicMapper.toTopicDTOList(adminTestService.getAllTopics());
        model.addAttribute("topics", topics);
        return "test-settings";
    }

    @PostMapping("/settings")
    public String startTest(@RequestParam(required = false) List<String> topics,
                            @RequestParam(defaultValue = "10") int questionCount,
                            HttpSession session, Model model) {
        try {
            TestStartRequest request = new TestStartRequest(topics, questionCount);
            InterviewState state = testService.startTest(request);
            session.setAttribute(SESSION_ATTR, state);
            return "redirect:/test/question";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("topics", topicMapper.toTopicDTOList(adminTestService.getAllTopics()));
            return "test-settings";
        }
    }

    @GetMapping("/question")
    public String showQuestion(HttpSession session, Model model) {
        InterviewState state = getState(session);
        if (state == null || state.isFinished()) {
            if (state != null && state.isFinished()) {
                return "redirect:/test/result";
            }
            return "redirect:/test/settings";
        }
        model.addAttribute("question", toQuestionResponse(state));
        return "question";
    }

    @PostMapping("/question")
    public String answerQuestion(@RequestParam int answerIndex,
                                 HttpSession session) {
        InterviewState state = getState(session);
        if (state == null) {
            return "redirect:/test/settings";
        }
        testService.processAnswer(state, answerIndex);
        if (state.isFinished()) {
            return "redirect:/test/result";
        }
        return "redirect:/test/question";
    }

    @GetMapping("/result")
    public String showResult(Authentication authentication,
                             HttpSession session, Model model) {
        InterviewState state = getState(session);
        if (state == null) {
            return "redirect:/test/settings";
        }
        if (!state.isFinished()) {
            return "redirect:/test/settings";
        }
        User user = userService.getProfile(currentUserService.getCurrentUserId(authentication));
        testResultService.saveResult(user, state);
        TestResultResponse result = testResultService.processResult(state);
        session.removeAttribute(SESSION_ATTR);
        model.addAttribute("result", result);
        return "result";
    }

    private QuestionResponse toQuestionResponse(InterviewState state) {
        Question q = state.getCurrentQuestion();
        return new QuestionResponse(
                q.getId(), q.getQuestionText(),
                toAnswerItems(q.getAnswers()),
                state.getCurrentIndex() + 1,
                state.getTotalQuestions(),
                state.getScore(), false
        );
    }

    private List<AnswerDTO> toAnswerItems(List<Answer> answers) {
        return answers.stream()
                .map(a -> new AnswerDTO(a.getAnswerIndex(), a.getAnswerText()))
                .toList();
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