package com.homeapp.javatraining.repository;

import com.homeapp.javatraining.config.hibernate.HibernateUtil;
import com.homeapp.javatraining.model.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

@Slf4j
public class HibernateUserRepository implements UserRepository {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    @Override
    public void save(User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Error saving user", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByUserName(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "FROM User u WHERE u.username = :username", User.class)
                            .setParameter("username", username)
                            .uniqueResultOptional();

        }
    }

    @Override
    public Optional<User> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(User.class, id));
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).list();
        }
    }
}
