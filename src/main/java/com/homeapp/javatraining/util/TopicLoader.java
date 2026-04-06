package com.homeapp.javatraining.util;

import com.homeapp.javatraining.config.hibernate.HibernateUtil;
import com.homeapp.javatraining.model.Topic;
import org.hibernate.Session;

import java.util.List;

public class TopicLoader {

    public static List<Topic> loadAllTopics() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Topic", Topic.class).list();
        }
    }

    public static Topic findByCode(String code) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "FROM Topic t WHERE t.code = :code", Topic.class)
                    .setParameter("code", code).uniqueResult();
        }
    }
}
