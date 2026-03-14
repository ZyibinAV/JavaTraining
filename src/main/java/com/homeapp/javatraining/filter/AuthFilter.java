package com.homeapp.javatraining.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/profile/*",
        "/test/*",
        "/admin/*",
        "/question/*",
        "/result"
})
public class AuthFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String path = req.getRequestURI();
        log.debug("Auth check for path={}", path);

        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("currentUser") == null) {
            log.warn(
                    "Unauthorized access attempt, redirecting to login. path={}", path);
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        log.debug("Auth check passed for path={}", path);
        filterChain.doFilter(servletRequest, servletResponse);
    }
}