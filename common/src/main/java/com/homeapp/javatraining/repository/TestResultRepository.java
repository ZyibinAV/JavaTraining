package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.model.TestResult;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    @EntityGraph(attributePaths = {"user", "topic"})
    List<TestResult> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "topic"})
    List<TestResult> findAll();
}
