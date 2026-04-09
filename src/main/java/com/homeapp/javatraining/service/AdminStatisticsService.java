package com.homeapp.javatraining.service;

import com.homeapp.javatraining.dto.TopicStats;
import com.homeapp.javatraining.dto.UserStats;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.TestResultRepository;
import com.homeapp.javatraining.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class AdminStatisticsService {

    private final TestResultRepository testResultRepository;
    private final UserRepository userRepository;

    public AdminStatisticsData getStatistics() {

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

            UserStats stats = userStats.get(r.getUser().getId());
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

            TopicStats stats = topicStats.computeIfAbsent(
                    topic.getCode(),
                    code -> new TopicStats(code, topic.getDisplayName())
            );

            stats.incrementTotal();
            if (r.isPassed()) {
                stats.incrementPassed();
            }
        }

        return new AdminStatisticsData(
                totalTests,
                passedTests,
                userStats.values(),
                topicStats.values()
        );
    }

    @Getter
    public static class AdminStatisticsData {
        private final int totalTests;
        private final long passedTests;
        private final Collection<UserStats> userStats;
        private final Collection<TopicStats> topicStats;

        public AdminStatisticsData(int totalTests,
                                   long passedTests,
                                   Collection<UserStats> userStats,
                                   Collection<TopicStats> topicStats) {
            this.totalTests = totalTests;
            this.passedTests = passedTests;
            this.userStats = userStats;
            this.topicStats = topicStats;
        }
    }
}
