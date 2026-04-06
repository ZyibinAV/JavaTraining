package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.config.hibernate.HibernateUtil;
import com.homeapp.javatraining.model.Topic;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;

import java.io.IOException;
import java.util.List;

@WebServlet("/test/settings")
public class TestSettingServlet extends BaseServlet {

    @Override
    protected void initializeSpecificServices() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET /test/settings");

        List<Topic> topics;
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            topics = session.createQuery("FROM Topic", Topic.class).list();
        }
        req.setAttribute("topics", topics);
        req.getRequestDispatcher("/WEB-INF/jsp/test-settings.jsp").forward(req, resp);
    }
}
