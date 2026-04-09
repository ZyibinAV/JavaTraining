package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.filter.AuthFilter;
import com.homeapp.javatraining.handler.RequestHandler;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.model.User;
import com.homeapp.javatraining.repository.HibernateQuestionRepository;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.repository.TestResultRepository;
import com.homeapp.javatraining.repository.UserRepository;
import com.homeapp.javatraining.service.AuthenticationService;
import com.homeapp.javatraining.service.QuestionService;
import com.homeapp.javatraining.service.RegistrationService;
import com.homeapp.javatraining.service.UserService;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseServlet extends HttpServlet {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected UserRepository userRepository;
    protected TestResultRepository testResultRepository;
    protected QuestionRepository questionRepository;

    protected UserService userService;
    protected AuthenticationService authenticationService;
    protected RegistrationService registrationService;
    protected QuestionService questionService;

    protected RequestHandler requestHandler;

    @Override
    public void init() throws ServletException {
        super.init();
        initializeCommonDependencies();
        initializeSpecificServices();
        log.debug("{} initialized successfully", getClass().getSimpleName());
    }

    private void initializeCommonDependencies() throws ServletException {
        ServletContext context = getServletContext();

        this.userRepository = (UserRepository) context.getAttribute("userRepository");
        this.testResultRepository = (TestResultRepository) context.getAttribute("testResultRepository");
        this.questionRepository = (QuestionRepository) context.getAttribute("questionRepository");

        this.userService = (UserService) context.getAttribute("userService");
        this.authenticationService = (AuthenticationService) context.getAttribute("authenticationService");
        this.registrationService = (RegistrationService) context.getAttribute("registrationService");
        this.questionService = (QuestionService) context.getAttribute("questionService");

        this.requestHandler = new RequestHandler();

        validateDependencies();
    }

    private void validateDependencies() throws ServletException {
        if (userRepository == null) {
            throw new ServletException("UserRepository not found in ServletContext");
        }
        if (testResultRepository == null) {
            throw new ServletException("TestResultRepository not found in ServletContext");
        }
        if (questionRepository == null) {
            throw new ServletException("QuestionRepository not found in ServletContext");
        }
        if(userService == null) {
            throw new ServletException("UserService not found in ServletContext");
        }
        if(authenticationService == null) {
            throw new ServletException("AuthenticationService not found in ServletContext");
        }
        if(registrationService == null) {
            throw new ServletException("RegistrationService not found in ServletContext");
        }
        if(questionService == null) {
            throw new ServletException("QuestionService not found in ServletContext");
        }
        if (requestHandler == null) {
            throw new ServletException("RequestHandler initialization failed");
        }
    }

    protected abstract void initializeSpecificServices();

    protected User getCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("currentUser");
    }

    protected boolean isUserAuthenticated(HttpServletRequest req) {
        return getCurrentUser(req) != null;
    }

    protected boolean isCurrentUserAdmin(HttpServletRequest req) {
        User user = getCurrentUser(req);
        return user != null && user.getRole() == Role.ADMIN;
    }

    protected void setCurrentUser(HttpServletRequest req, User user) {
        HttpSession session = req.getSession(true);
        session.setAttribute("currentUser", user);
        log.debug("User {} set in session", user.getUsername());
    }

    protected void clearCurrentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = getCurrentUser(req);
            session.invalidate();
            if (user != null) {
                log.debug("User {} removed from session", user.getUsername());
            }
        }
    }

    @Override
    public void destroy() {
        log.debug("{} destroyed", getClass().getSimpleName());
        super.destroy();
    }
}
