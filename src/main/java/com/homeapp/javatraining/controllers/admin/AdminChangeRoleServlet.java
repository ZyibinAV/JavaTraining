package com.homeapp.javatraining.controllers.admin;


import com.homeapp.javatraining.controllers.BaseServlet;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.service.AdminUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/users/role")
public class AdminChangeRoleServlet extends BaseServlet {

    private AdminUserService adminService;

    @Override
    protected void initializeSpecificServices() {
        this.adminService = (AdminUserService) getServletContext().getAttribute("adminUserService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("POST /admin/users/role");

        User admin = getCurrentUser(req);
        long userId = Long.parseLong(req.getParameter("userId"));
        Role newRole = Role.valueOf(req.getParameter("role"));

        log.info("Admin {} requested role change for userId={} to role={}",
                admin.getUsername(),
                userId,
                newRole);

        adminService.changeUserRole(admin.getId(), userId, newRole);
        resp.sendRedirect(req.getContextPath() + "/admin/users");
    }
}
