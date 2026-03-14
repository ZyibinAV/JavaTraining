package com.homeapp.javatraining.config.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private static  SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();
        }catch (Throwable e) {
            throw new ExceptionInInitializerError(e);
        }

    }

    public  static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}
