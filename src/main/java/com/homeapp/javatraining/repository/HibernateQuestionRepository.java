package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.config.hibernate.HibernateUtil;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

@Slf4j
public class HibernateQuestionRepository implements QuestionRepository {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();


    @Override
    public List<Question> getQuestions(Topic topic) {
        try (Session session = sessionFactory.openSession()) {
            List<Question> questions = session.createQuery(
                    "SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.topic LEFT JOIN FETCH q.answers WHERE q.topic = :topic", Question.class)
                    .setParameter("topic", topic).list();

            log.info("Questions loaded from DB: topic={}, count={}",
                    topic.getCode(),
                    questions.size());
            return questions;
        }
    }

    @Override
    public Optional<Question> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            Question question = session.createQuery(
                    "SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.topic LEFT JOIN FETCH q.answers WHERE q.id = :id", Question.class)
                    .setParameter("id", id)
                    .uniqueResult();
            return Optional.ofNullable(question);
        }
    }

    @Override
    public List<Question> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.topic LEFT JOIN FETCH q.answers", Question.class)
                    .list();
        }
    }

    @Override
    public void save(Question question) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(question);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error saving question", e);
            throw e;
        }
    }

    @Override
    public boolean existsByTextAndTopic(String questionText, Topic topic) {
        try (Session session = sessionFactory.openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(q.id) FROM Question q WHERE q.questionText = :text AND q.topic = :topic",
                    Long.class)
                    .setParameter("text", questionText)
                    .setParameter("topic", topic)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    @Override
    public void saveAll(List<Question> questions) {
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            int batchSize = 50;
            int i = 0;

            for (Question question : questions) {
                session.persist(question);
                i++;
                if (i % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }
            transaction.commit();
            log.info("Batch save completed: count={}", questions.size());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error in batch save", e);
            throw e;
        }
    }


}
