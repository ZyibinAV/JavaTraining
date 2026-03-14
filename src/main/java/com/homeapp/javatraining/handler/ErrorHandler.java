package com.homeapp.javatraining.handler;

import com.homeapp.javatraining.exception.AuthenticationException;
import com.homeapp.javatraining.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ErrorHandler {
    private final Logger log = LoggerFactory.getLogger(ErrorHandler.class);


    public void handleValidationError(HttpServletRequest req, HttpServletResponse resp,
                                      ValidationException e, String returnPage) throws IOException {
        log.warn("Validation error: field={}, message={}", e.getField(), e.getMessage());

        req.setAttribute("error", e.getMessage());
        req.setAttribute("errorField", e.getField());
        req.setAttribute("errorCode", e.getErrorCode());

        preserveFormData(req);

        try {
            req.getRequestDispatcher(returnPage).forward(req, resp);
        } catch (Exception ex) {
            log.error("Error forwarding to error page", ex);
            resp.sendRedirect(req.getContextPath() + "/error");
        }
    }


    public void handleAuthenticationError(HttpServletRequest req, HttpServletResponse resp,
                                          AuthenticationException e) throws IOException {
        log.warn("Authentication error: reason={}, message={}", e.getReason(), e.getMessage());

        String errorMessage = getAuthenticationErrorMessage(e);
        req.setAttribute("error", errorMessage);

        preserveFormData(req);

        try {
            req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
        } catch (Exception ex) {
            log.error("Error forwarding to login page", ex);
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }


    public void handleGeneralError(HttpServletRequest req, HttpServletResponse resp,
                                   Exception e) throws IOException {
        log.error("General error occurred", e);

        req.setAttribute("error", "An unexpected error occurred. Please try again later.");
        req.setAttribute("errorCode", "INTERNAL_ERROR");

        try {
            req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
        } catch (Exception ex) {
            log.error("Error forwarding to error page", ex);
            resp.sendRedirect(req.getContextPath() + "/error");
        }
    }


    private void preserveFormData(HttpServletRequest req) {
        Map<String, String> formData = new HashMap<>();

        req.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                formData.put(key, values[0]);
            }
        });

        req.setAttribute("formData", formData);
    }


    private String getAuthenticationErrorMessage(AuthenticationException e) {
        switch (e.getReason()) {
            case "INVALID_CREDENTIALS":
                return "Invalid login or password";
            case "USER_BLOCKED":
                return "User blocked by administrator";
            case "USER_NOT_FOUND":
                return "User not found";
            default:
                return e.getMessage();
        }
    }
}
