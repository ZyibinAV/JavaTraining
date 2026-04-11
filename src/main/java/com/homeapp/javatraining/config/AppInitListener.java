package com.homeapp.javatraining.config;


import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
@Slf4j
public class AppInitListener implements ServletContextListener {


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

        context.setAttribute("topicRepository", config.getTopicRepository());
        log.info("TopicRepository initialized");

        context.setAttribute("userService", config.getUserService());
        log.info("UserService initialized");

        context.setAttribute("authenticationService", config.getAuthenticationService());
        log.info("AuthenticationService initialized");

        context.setAttribute("registrationService", config.getRegistrationService());
        log.info("RegistrationService initialized");

        context.setAttribute("questionService", config.getQuestionService());
        log.info("QuestionService initialized");

        context.setAttribute("testResultService", config.getTestResultService());
        log.info("TestResultService initialized");

        context.setAttribute("adminStatisticsService", config.getAdminStatisticsService());
        log.info("AdminStatisticsService initialized");

        context.setAttribute("userStatisticsService", config.getUserStatisticsService());
        log.info("UserStatisticsService initialized");

        context.setAttribute("avatarService", config.getAvatarService());
        log.info("AvatarService initialized");

        context.setAttribute("adminUserService", config.getAdminUserService());
        log.info("AdminUserService initialized");

        context.setAttribute("topicLoader", config.getTopicLoader());
        log.info("TopicLoader initialized");

        log.info("Application context initialization completed successfully");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("Application context destroyed");
    }
}

