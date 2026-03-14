package com.homeapp.javatraining.service;

import com.homeapp.javatraining.dto.UserTopicStats;
import com.homeapp.javatraining.model.TestResult;

import java.util.List;

public interface UserStatisticsService {

    List<UserTopicStats> calculateUserTopicStats(List<TestResult> results);
}
