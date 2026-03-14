package com.homeapp.javatraining.config;


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class AppInitListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppInitListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("Application context initialization started");

        ApplicationConfig config = new ApplicationConfig();
        ServletContext context = sce.getServletContext();


        context.setAttribute("userRepository", config.getUserRepository());
        log.info("UserRepository initialized");


        context.setAttribute("testResultRepository", config.getTestResultRepository());
        log.info("TestResultRepository initialized");


        context.setAttribute("questionRepository", config.getQuestionRepository());
        log.info("QuestionRepository initialized");

        log.info("Application context initialization completed successfully");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Application context destroyed");
    }
}

