package com.homeapp.javatraining.filter;

import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter("/admin/*")
public class AdminFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(AdminFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String path = req.getRequestURI();
        log.debug("Admin access attempt: {}", path);

        HttpSession session = req.getSession(false);

        User user = (User) session.getAttribute("currentUser");

        if (user == null || user.getRole() != Role.ADMIN) {
            log.warn("Admin access denied. user={}, path={}",
                    user != null ? user.getUsername() : "anonymous",
                    path
            );
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        log.debug("Admin access granted. user={}, path={}",
                user.getUsername(),
                path);

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
