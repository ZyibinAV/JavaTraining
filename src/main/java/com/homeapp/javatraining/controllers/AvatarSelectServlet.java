package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.UserRepository;
import com.homeapp.javatraining.service.AvatarService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/profile/avatar")
public class AvatarSelectServlet extends BaseServlet {

    private AvatarService avatarService;
    private UserRepository userRepository;

    @Override
    protected void initializeSpecificServices() {
        this.avatarService = (AvatarService) getServletContext().getAttribute("avatarService");
        this.userRepository = (UserRepository) getServletContext().getAttribute("userRepository");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("GET /profile/avatar");

        User user = getCurrentUser(req);
        List<String> avatars = avatarService.getAvailableAvatars(user.getAvatarPath());
        log.debug("Loaded {} available avatars", avatars.size());

        req.setAttribute("avatars", avatars);
        req.getRequestDispatcher("/WEB-INF/jsp/avatar-select.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("POST /profile/avatar");

        User user = getCurrentUser(req);
        String selectedAvatar = req.getParameter("avatarPath");
        log.debug("Selected avatar path: {}", selectedAvatar);

        List<String> availableAvatars = avatarService.getAvailableAvatars(user.getAvatarPath());
        if (availableAvatars.contains(selectedAvatar)) {
            log.info("User {} changed avatar to {}", user.getUsername(), selectedAvatar);
            user.setAvatarPath(selectedAvatar);
            userRepository.save(user);
        } else {
            log.warn("User {} attempted to select invalid avatar: {}",
                    user.getUsername(),
                    selectedAvatar);
        }
        resp.sendRedirect(req.getContextPath() + "/profile");
    }
}
