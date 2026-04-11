package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.UserStatisticsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {

    private UserStatisticsService userStatisticsService;

    @Override
    protected void initializeSpecificServices() {
        this.userStatisticsService = (UserStatisticsService) getServletContext().getAttribute("userStatisticsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("GET /profile");

        User user = getCurrentUser(req);
        log.debug("Loading test results for user {}", user.getUsername());

        List<TestResult> results = testResultRepository.findByUserId(user.getId());
        req.setAttribute("results", results);

        req.setAttribute("topicStats", userStatisticsService.calculateUserTopicStats(results));

        log.info("Profile page prepared for user {}", user.getUsername());
        req.getRequestDispatcher("/WEB-INF/jsp/profile.jsp").forward(req, resp);
    }
}
