package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.Topic;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/test/settings")
public class TestSettingServlet extends BaseServlet {

    @Override
    protected void initializeSpecificServices() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("GET /test/settings");
        req.setAttribute("topics", Topic.values());
        req.getRequestDispatcher("/WEB-INF/jsp/test-settings.jsp").forward(req, resp);
    }
}
