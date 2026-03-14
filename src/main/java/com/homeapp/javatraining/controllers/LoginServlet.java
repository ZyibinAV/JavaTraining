package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.AuthenticationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private AuthenticationService authService;

    @Override
    protected void initializeSpecificServices() {
        this.authService = new AuthenticationService(userRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("GET /login");
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        requestHandler.handleRequest(req, resp, () -> {
            User user = authService.authenticate(username, password);

            setCurrentUser(req, user);

            log.info("User {} successfully logged in", username);
            resp.sendRedirect(req.getContextPath() + "/home");
        }, "/WEB-INF/jsp/login.jsp");
    }
}
