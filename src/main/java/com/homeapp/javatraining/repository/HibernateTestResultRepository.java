package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.config.hibernate.HibernateUtil;
import com.homeapp.javatraining.model.TestResult;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class HibernateTestResultRepository implements TestResultRepository {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public void save(TestResult result) {
        Transaction transaction = null;

        try(Session session = sessionFactory.openSession()){
            transaction= session.beginTransaction();
            session.merge(result);
            transaction.commit();

            log.info("TestResult saved: id={}, userId={}, topic={}, passed={}",
                    result.getId(),
                    result.getUser() != null ? result.getUser().getId() : null,
                    result.getTopic() != null ? result.getTopic().getId() : null,
                    result.isPassed());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error saving TestResult", e);
            throw new RuntimeException("Failed to save TestResult", e);
        }
    }

    @Override
    public List<TestResult> findByUserId(long userId) {
        try (Session session = sessionFactory.openSession()) {
            List<TestResult> results = session.createQuery(
                    "FROM TestResult tr WHERE tr.user.id = :userId", TestResult.class)
                    .setParameter("userId", userId).list();
            log.debug("Loaded {} test results for userId={}", results.size(), userId);
            return  results;
        }
    }

    @Override
    public List<TestResult> findAll() {
        try (Session session = sessionFactory.openSession()) {
            List<TestResult> results = session.createQuery(
                    "FROM TestResult",
                    TestResult.class).list();
            log.debug("Loaded all test results, count={}", results.size());
            return   results;
        }
    }
}
