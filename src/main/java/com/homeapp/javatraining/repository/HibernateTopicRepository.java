package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.config.hibernate.HibernateUtil;
import com.homeapp.javatraining.model.Topic;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

@Slf4j
public class HibernateTopicRepository implements TopicRepository {

    @Override
    public List<Topic> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Topic", Topic.class).list();
        } catch (Exception e) {
            log.error("Error loading all topics", e);
            throw new RuntimeException("Failed to load topics", e);
        }
    }

    @Override
    public Optional<Topic> findByCode(String code) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Topic topic = session.createQuery(
                            "FROM Topic t WHERE t.code = :code", Topic.class)
                    .setParameter("code", code)
                    .uniqueResult();
            return Optional.ofNullable(topic);
        } catch (Exception e) {
            log.error("Error finding topic by code: {}", code, e);
            throw new RuntimeException("Failed to find topic by code: " + code, e);
        }
    }

    @Override
    public void save(Topic topic) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(topic);
            transaction.commit();
            log.info("Topic saved: {}", topic.getCode());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error saving topic", e);
            throw new RuntimeException("Failed to save topic", e);
        }
    }

    @Override
    public void delete(Topic topic) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(topic);
            transaction.commit();
            log.info("Topic deleted: {}", topic.getCode());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error deleting topic", e);
            throw new RuntimeException("Failed to delete topic", e);
        }
    }
}
