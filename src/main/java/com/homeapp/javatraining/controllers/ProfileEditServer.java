package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/profile/edit")
public class ProfileEditServer extends BaseServlet {

    @Override
    protected void initializeSpecificServices() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("GET /profile/edit");
        req.getRequestDispatcher("/WEB-INF/jsp/profile-edit.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("POST /profile/edit");

        User user = getCurrentUser(req);
        String nickname = req.getParameter("nickname");
        String about = req.getParameter("about");

        user.setNickname(nickname);
        user.setAbout(about);
        log.info("User {} updated profile data", user.getUsername());

        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
