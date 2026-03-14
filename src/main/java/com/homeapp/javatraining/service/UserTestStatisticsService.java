package com.homeapp.javatraining.service;

import com.homeapp.javatraining.dto.UserTestStats;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.util.TopicUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTestStatisticsService {
    private static final Logger log = LoggerFactory.getLogger(UserTestStatisticsService.class);

    public List<UserTestStats> calculate(List<TestResult> results) {
        log.debug("Calculating user test statistics, results count={}", results.size());
        Map<String, UserTestStats> map = new HashMap<>();

        for (TestResult result : results) {
            String testKey = buildTestName(result.getTopicCode());
            UserTestStats stats =
                    map.computeIfAbsent(testKey, UserTestStats::new);
            stats.incrementTotal();
            if (result.isPassed()) {
                stats.incrementPassed();
            }
        }
        log.debug("User test statistics calculated, tests count={}", map.size());
        return new ArrayList<>(map.values());
    }

    private String buildTestName(String topicCode) {
        return TopicUtils.convertTopicCodesToDisplayNames(topicCode);
    }
}
