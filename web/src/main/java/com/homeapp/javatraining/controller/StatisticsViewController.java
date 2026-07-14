package com.homeapp.javatraining.controller;

import com.homeapp.javatraining.dto.UserTopicStats;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.repository.TestResultRepository;
import com.homeapp.javatraining.service.CurrentUserService;
import com.homeapp.javatraining.service.UserStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StatisticsViewController {

    private final TestResultRepository testResultRepository;
    private final CurrentUserService currentUserService;
    private final UserStatisticsService userStatisticsService;

    @GetMapping("/my-stats")
    public String myStats(Authentication authentication, Model model) {
        Long userId = currentUserService.getCurrentUserId(authentication);
        List<TestResult> results = testResultRepository.findByUserId(userId);

        int total = results.size();
        long passed = results.stream().filter(TestResult::isPassed).count();
        long failed = total - passed;

        model.addAttribute("totalTests", total);
        model.addAttribute("passedTests", passed);
        model.addAttribute("failedTests", failed);

        List<UserTopicStats> topicStats = userStatisticsService.calculateUserTopicStats(results);
        model.addAttribute("topicStats", topicStats);

        List<TestResult> recentResults = results.stream()
                .sorted(Comparator.comparing(TestResult::getFinishedAt).reversed())
                .limit(20)
                .toList();
        model.addAttribute("recentResults", recentResults);

        return "my-statistics";
    }
}
