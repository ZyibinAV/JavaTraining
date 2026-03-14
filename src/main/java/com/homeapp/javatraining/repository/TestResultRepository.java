package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.TestResult;

import java.util.List;

public interface TestResultRepository {

    void save(TestResult result);

    List<TestResult> findByUserId(long userId);

    List<TestResult> findAll();
}
