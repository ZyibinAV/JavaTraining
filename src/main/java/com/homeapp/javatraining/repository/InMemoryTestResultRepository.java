package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryTestResultRepository implements TestResultRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryTestResultRepository.class);

    private ConcurrentHashMap<Long, TestResult> results = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);


    @Override
    public void save(TestResult result) {
        if (result.getId() == 0) {
            result.setId(idGenerator.getAndIncrement());
        }
        results.put(result.getId(), result);
        log.info("TestResult saved: id={}, userId={}, topics={}, passed={}",
                result.getId(),
                result.getUserId(),
                result.getTopicCode(),
                result.isPassed()
        );
    }

    @Override
    public List<TestResult> findByUserId(long userId) {
        List<TestResult> list = new ArrayList<>();
        for (TestResult result : results.values()) {
            if (result.getUserId() == userId) {
                list.add(result);
            }
        }
        log.debug("Loaded {} test results for userId={}", list.size(), userId);
        return list;
    }

    @Override
    public List<TestResult> findAll() {
        List<TestResult> all = new ArrayList<>(results.values());
        log.debug("Loaded all test results, count={}", all.size());
        return new ArrayList<>(results.values());
    }
}
