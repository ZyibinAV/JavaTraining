package com.homeapp.javatraining.controllers.admin;

import com.homeapp.javatraining.controllers.BaseServlet;
import com.homeapp.javatraining.dto.TopicStats;
import com.homeapp.javatraining.dto.UserStats;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.AdminStatisticsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/statistics")
public class AdminStatisticsServlet extends BaseServlet {

    private AdminStatisticsService adminStatisticsService;

    @Override
    protected void initializeSpecificServices() {
        this.adminStatisticsService =
                (AdminStatisticsService) getServletContext().getAttribute("adminStatisticsService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.debug("GET /admin/statistics");

        AdminStatisticsService.AdminStatisticsData data =
                adminStatisticsService.getStatistics();

        req.setAttribute("totalTests", data.getTotalTests());
        req.setAttribute("passedTests", data.getPassedTests());
        req.setAttribute("userStats", data.getUserStats());
        req.setAttribute("topicStats", data.getTopicStats());

        req.getRequestDispatcher("/WEB-INF/jsp/admin/statistics.jsp")
                .forward(req, resp);
    }
}