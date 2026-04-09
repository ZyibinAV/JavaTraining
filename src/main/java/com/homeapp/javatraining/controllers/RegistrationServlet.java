package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.RegistrationService;
import com.homeapp.javatraining.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegistrationServlet extends BaseServlet {


    @Override
    protected void initializeSpecificServices() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("GET /register");
        req.getRequestDispatcher("/WEB-INF/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        requestHandler.handleRequest(req, resp, () -> {
            User user = registrationService.registerUser(username, password, email);
            log.info("User {} successfully registered", user.getUsername());
            resp.sendRedirect(req.getContextPath() + "/login");
        }, "/WEB-INF/jsp/register.jsp");
    }
}
