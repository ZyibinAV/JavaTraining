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
}
