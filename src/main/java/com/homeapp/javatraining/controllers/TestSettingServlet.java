package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.util.TopicLoader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/test/settings")
public class TestSettingServlet extends BaseServlet {

    private TopicLoader topicLoader;

    @Override
    protected void initializeSpecificServices() {
        this.topicLoader = (TopicLoader) getServletContext().getAttribute("topicLoader");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET /test/settings");

        List<Topic> topics = topicLoader.loadAllTopics();
        req.setAttribute("topics", topics);
        req.getRequestDispatcher("/WEB-INF/jsp/test-settings.jsp").forward(req, resp);
    }
}
