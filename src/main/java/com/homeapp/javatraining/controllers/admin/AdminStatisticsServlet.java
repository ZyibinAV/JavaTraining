package com.homeapp.javatraining.controllers.admin;

import com.homeapp.javatraining.controllers.BaseServlet;
import com.homeapp.javatraining.dto.TopicStats;
import com.homeapp.javatraining.dto.UserStats;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.model.User;
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

    @Override
    protected void initializeSpecificServices() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.debug("GET /admin/statistics");

        List<TestResult> results = testResultRepository.findAll();
        List<User> users = userRepository.findAll();

        int totalTests = results.size();
        long passedTests = results.stream()
                .filter(TestResult::isPassed)
                .count();

        Map<Long, UserStats> userStats = new HashMap<>();
        for (User user : users) {
            userStats.put(user.getId(), new UserStats(user));
        }

        for (TestResult r : results) {
            if (r.getUser() == null) continue;

            Long userId = r.getUser().getId();
            UserStats stats = userStats.get(userId);

            if (stats != null) {
                stats.incrementTotal();
                if (r.isPassed()) {
                    stats.incrementPassed();
                }
            }
        }

        Map<String, TopicStats> topicStats = new HashMap<>();

        for (TestResult r : results) {
            if (r.getTopic() == null) continue;

            Topic topic = r.getTopic();
            String topicCode = topic.getCode();

            // ✅ FIX: передаём displayName
            TopicStats stats = topicStats.computeIfAbsent(
                    topicCode,
                    code -> new TopicStats(code, topic.getDisplayName())
            );

            stats.incrementTotal();
            if (r.isPassed()) {
                stats.incrementPassed();
            }
        }

        log.info("Admin statistics prepared: totalTests={}, passedTests={}, users={}",
                totalTests,
                passedTests,
                users.size());

        req.setAttribute("totalTests", totalTests);
        req.setAttribute("passedTests", passedTests);
        req.setAttribute("userStats", userStats.values());
        req.setAttribute("topicStats", topicStats.values());

        req.getRequestDispatcher("/WEB-INF/jsp/admin/statistics.jsp")
                .forward(req, resp);
    }
}