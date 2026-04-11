package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.*;
import com.homeapp.javatraining.service.TestResultService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/result")
public class ResultServlet extends BaseServlet {

    private TestResultService testResultService;

    @Override
    protected void initializeSpecificServices() {
        this.testResultService = (TestResultService) getServletContext().getAttribute("testResultService");
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

        if (interviewState.isExpired()) {
            log.warn("Interview session expired, clearing session and redirecting to /home");
            session.removeAttribute("interviewState");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        TestResult result = testResultService.processAndSaveResult(user, interviewState);

        req.setAttribute("topics", result.getTopic().getDisplayName());
        req.setAttribute("total", result.getTotalQuestions());
        req.setAttribute("correct", result.getCorrectAnswers());
        req.setAttribute("passed", result.isPassed());


        session.removeAttribute("interviewState");

        req.getRequestDispatcher("/WEB-INF/jsp/result.jsp").forward(req, resp);
    }
}
