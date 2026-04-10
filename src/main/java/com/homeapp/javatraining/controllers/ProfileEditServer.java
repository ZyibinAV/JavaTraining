package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.util.PasswordUtil;
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
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        user.setNickname(nickname);
        user.setAbout(about);

        // Handle password change if provided
        if (currentPassword != null && !currentPassword.isEmpty()) {
            String currentPasswordHash = PasswordUtil.hashPassword(currentPassword);
            if (!user.getPasswordHash().equals(currentPasswordHash)) {
                log.warn("User {} failed to change password - current password incorrect", user.getUsername());
                req.setAttribute("error", "Неверный текущий пароль");
                req.getRequestDispatcher("/WEB-INF/jsp/profile-edit.jsp").forward(req, resp);
                return;
            }

            if (newPassword == null || newPassword.isEmpty()) {
                log.warn("User {} failed to change password - new password empty", user.getUsername());
                req.setAttribute("error", "Введите новый пароль");
                req.getRequestDispatcher("/WEB-INF/jsp/profile-edit.jsp").forward(req, resp);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                log.warn("User {} failed to change password - passwords do not match", user.getUsername());
                req.setAttribute("error", "Новый пароль и подтверждение не совпадают");
                req.getRequestDispatcher("/WEB-INF/jsp/profile-edit.jsp").forward(req, resp);
                return;
            }

            String newPasswordHash = PasswordUtil.hashPassword(newPassword);
            user.setPasswordHash(newPasswordHash);
            log.info("User {} changed password", user.getUsername());
        }

        userRepository.save(user);
        log.info("User {} updated profile data", user.getUsername());

        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
