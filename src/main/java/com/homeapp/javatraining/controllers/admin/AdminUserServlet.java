package com.homeapp.javatraining.controllers.admin;

import com.homeapp.javatraining.controllers.BaseServlet;
import com.homeapp.javatraining.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/users")
public class AdminUserServlet extends BaseServlet {

    @Override
    protected void initializeSpecificServices() {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("GET /admin/users");

        User admin = getCurrentUser(req);
        List<User> users = userRepository.findAll();

        log.info("Admin {} loaded users list, count={}", admin.getUsername(), users.size());

        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("POST /admin/users");

        String action = req.getParameter("action");
        if ("delete-user".equals(action)) {
            deleteUser(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
        }
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userIdStr = req.getParameter("userId");
        if (userIdStr == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/users");
            return;
        }

        try {
            Long userId = Long.parseLong(userIdStr);
            User user = userRepository.findById(userId).orElse(null);

            if (user == null) {
                log.error("User not found: {}", userId);
                req.setAttribute("error", "User not found");
                showUsersList(req, resp);
                return;
            }

            userRepository.delete(user);
            log.info("User deleted: id={}, username={}", userId, user.getUsername());
            req.setAttribute("success", "User deleted successfully");
            showUsersList(req, resp);

        } catch (NumberFormatException e) {
            log.error("Invalid user ID", e);
            req.setAttribute("error", "Invalid user ID");
            showUsersList(req, resp);
        } catch (Exception e) {
            log.error("Error deleting user", e);
            req.setAttribute("error", "Error deleting user: " + e.getMessage());
            showUsersList(req, resp);
        }
    }

    private void showUsersList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<User> users = userRepository.findAll();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/users.jsp").forward(req, resp);
    }
}
