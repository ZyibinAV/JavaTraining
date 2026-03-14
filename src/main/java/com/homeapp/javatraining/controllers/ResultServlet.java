package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@WebServlet("/result")
public class ResultServlet extends BaseServlet {

    @Override
    protected void initializeSpecificServices() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("GET /result");

        HttpSession session = req.getSession(false);

        if (session == null) {
            log.debug("No session found, redirecting to /home");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        User user = getCurrentUser(req);

        InterviewState interviewState = (InterviewState) session.getAttribute("interviewState");
        if (interviewState == null) {
            log.debug("No interview state found, redirecting to /home");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        int totalQuestions = interviewState.getTotalQuestions();
        int correctAnswers = interviewState.getScore();
        boolean passed = correctAnswers * 2 >= totalQuestions;

        String topicCodes = interviewState.getTopics()
                .stream()
                .map(Topic::getCode)
                .collect(Collectors.joining(", "));

        log.info("Interview finished for user {}, passed={}, score={}/{}",
                user.getUsername(),
                passed,
                correctAnswers,
                totalQuestions);

        TestResult result = new TestResult(
                user.getId(),
                topicCodes,
                totalQuestions,
                correctAnswers,
                passed,
                LocalDateTime.now()
        );

        testResultRepository.save(result);

        log.info("Test result saved for user {}", user.getUsername());

        req.setAttribute("topics", topicCodes);
        req.setAttribute("total", totalQuestions);
        req.setAttribute("correct", correctAnswers);
        req.setAttribute("passed", passed);

        session.removeAttribute("interviewState");

        req.getRequestDispatcher("/WEB-INF/jsp/result.jsp").forward(req, resp);
    }
}
