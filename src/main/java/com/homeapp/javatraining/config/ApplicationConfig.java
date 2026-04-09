package com.homeapp.javatraining.config;


import com.homeapp.javatraining.repository.*;
import com.homeapp.javatraining.service.*;
import com.homeapp.javatraining.validation.QuestionValidator;
import lombok.Getter;

/**
 * Manual DI container
 */

@Getter
public class ApplicationConfig {
    // ===== Repositories =====
    private final UserRepository userRepository;
    private final TestResultRepository testResultRepository;
    private final QuestionRepository questionRepository;

    // ===== Services =====
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;
    private final QuestionService questionService;
    private final AdminStatisticsService adminStatisticsService;

    public ApplicationConfig() {
        this.userRepository = createUserRepository();
        this.testResultRepository = createTestResultRepository();
        this.questionRepository = createQuestionRepository();

        this.userService = createdUserService();
        this.authenticationService = createdAuthenticationService();
        this.registrationService = createdRegistrationService();
        this.questionService = createdQuestionService();
        this.adminStatisticsService = createAdminStatisticsService();
    }

    private AdminStatisticsService createAdminStatisticsService() {
        return new AdminStatisticsService(testResultRepository, userRepository);
    }

    private TestResultService createTestResultService() {
        return new TestResultService(testResultRepository);
    }

    private QuestionService createdQuestionService() {
        return new QuestionService(
                new QuestionValidator(),
                questionRepository
        );
    }

    private RegistrationService createdRegistrationService() {
        return new RegistrationService(userService);
    }

    private AuthenticationService createdAuthenticationService() {
        return new AuthenticationService(userRepository);
    }

    private UserService createdUserService() {
        return new UserServiceImpl(userRepository);
    }

    private UserRepository createUserRepository() {
        return new HibernateUserRepository();
    }

    private TestResultRepository createTestResultRepository() {
        return new HibernateTestResultRepository();
    }

    private QuestionRepository createQuestionRepository() {
        return new HibernateQuestionRepository();
    }


}
