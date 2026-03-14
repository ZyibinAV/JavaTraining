package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends BaseServlet {

    @Override
    protected void initializeSpecificServices() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        performLogout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        performLogout(req, resp);
    }

    private void performLogout(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        User currentUser = getCurrentUser(req);
        if (currentUser != null) {
            log.info("User {} logged out", currentUser.getUsername());
        }

        clearCurrentUser(req);
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
