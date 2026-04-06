package com.homeapp.javatraining.service;

import com.homeapp.javatraining.dto.UserTestStats;
import com.homeapp.javatraining.model.TestResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserTestStatisticsService {

    public List<UserTestStats> calculate(List<TestResult> results) {
        log.debug("Calculating user test statistics, results count={}", results.size());
        Map<String, UserTestStats> map = new HashMap<>();

        for (TestResult result : results) {
            if (result.getTopic() == null) continue;
            String testKey = result.getTopic().getDisplayName();
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

}
