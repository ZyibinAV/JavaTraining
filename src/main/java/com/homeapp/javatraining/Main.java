package com.homeapp.javatraining;

import com.homeapp.javatraining.config.hibernate.HibernateUtil;
import org.hibernate.Session;

public class Main {

    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("Hibernate connected successfully");
        }
    }
}
