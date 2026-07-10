package com.homeapp.javatraining.service;

import com.homeapp.javatraining.dto.UserTopicStats;
import com.homeapp.javatraining.model.TestResult;
import com.homeapp.javatraining.model.Topic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserStatisticsServiceImpl implements UserStatisticsService {

    @Override
    public List<UserTopicStats> calculateUserTopicStats(List<TestResult> results) {

        log.debug("Calculating user topic statistics, results count={}", results.size());
        Map<String, UserTopicStats> statsByTopic = new HashMap<>();
        for (TestResult result : results) {
            Set<Topic> topics = result.getTopics();
            if (topics == null || topics.isEmpty()) continue;

            for (Topic topic : topics) {
                String displayName = topic.getDisplayName();
                UserTopicStats stats = statsByTopic.computeIfAbsent(
                        displayName,
                        UserTopicStats::new
                );
                stats.incrementTotal();
                if (result.isPassed()) {
                    stats.incrementPassed();
                }
            }
        }

        log.debug("User topic statistics calculated, topics count={}", statsByTopic.size());

        return new ArrayList<>(statsByTopic.values());
    }
}
