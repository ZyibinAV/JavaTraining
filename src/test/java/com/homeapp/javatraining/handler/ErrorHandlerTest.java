package com.homeapp.javatraining.handler;

import com.homeapp.javatraining.exception.AuthenticationException;
import com.homeapp.javatraining.exception.ValidationException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Error Handler Tests")
class ErrorHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestDispatcher requestDispatcher;

    private ErrorHandler errorHandler;

    @Test
    @DisplayName("Should handle validation error and forward to return page")
    void handleValidationError_shouldForwardToReturnPage() throws ServletException, IOException {
        // Arrange
        String returnPage = "/WEB-INF/jsp/register.jsp";
        ValidationException exception = new ValidationException("Username is required", "username");
        
        when(request.getRequestDispatcher(returnPage)).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleValidationError(request, response, exception, returnPage);

        // Assert
        verify(request).setAttribute("error", "Username is required");
        verify(request).setAttribute("errorField", "username");
        verify(request).setAttribute("errorCode", exception.getErrorCode());
        verify(request).setAttribute(eq("formData"), any(Map.class));
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Should handle validation error with general error code")
    void handleValidationError_withGeneralError_shouldSetErrorCode() throws ServletException, IOException {
        // Arrange
        String returnPage = "/WEB-INF/jsp/login.jsp";
        ValidationException exception = new ValidationException("Invalid input", "general");
        
        when(request.getRequestDispatcher(returnPage)).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleValidationError(request, response, exception, returnPage);

        // Assert
        verify(request).setAttribute("error", "Invalid input");
        verify(request).setAttribute("errorField", "general");
        verify(request).setAttribute("errorCode", exception.getErrorCode());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Should preserve form data on validation error")
    void handleValidationError_shouldPreserveFormData() throws ServletException, IOException {
        // Arrange
        String returnPage = "/WEB-INF/jsp/register.jsp";
        ValidationException exception = new ValidationException("Email is required", "email");
        
        when(request.getParameterMap()).thenReturn(Map.of(
            "username", new String[]{"testuser"},
            "email", new String[]{""}
        ));
        when(request.getRequestDispatcher(returnPage)).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleValidationError(request, response, exception, returnPage);

        // Assert
        verify(request).setAttribute(eq("formData"), any(Map.class));
    }

    @Test
    @DisplayName("Should handle authentication error and forward to login page")
    void handleAuthenticationError_shouldForwardToLoginPage() throws ServletException, IOException {
        // Arrange
        AuthenticationException exception = AuthenticationException.invalidCredentials();
        
        when(request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleAuthenticationError(request, response, exception);

        // Assert
        verify(request).setAttribute("error", "Invalid login or password");
        verify(request).setAttribute(eq("formData"), any(Map.class));
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Should handle authentication error for blocked user")
    void handleAuthenticationError_withBlockedUser_shouldShowBlockedMessage() throws ServletException, IOException {
        // Arrange
        AuthenticationException exception = AuthenticationException.userBlocked();
        
        when(request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleAuthenticationError(request, response, exception);

        // Assert
        verify(request).setAttribute("error", "User blocked by administrator");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Should handle authentication error for user not found")
    void handleAuthenticationError_withUserNotFound_shouldShowNotFoundMessage() throws ServletException, IOException {
        // Arrange
        AuthenticationException exception = AuthenticationException.userNotFound();
        
        when(request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleAuthenticationError(request, response, exception);

        // Assert
        verify(request).setAttribute("error", "User not found");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Should handle general error and forward to error page")
    void handleGeneralError_shouldForwardToErrorPage() throws ServletException, IOException {
        // Arrange
        Exception exception = new RuntimeException("Something went wrong");
        
        when(request.getRequestDispatcher("/WEB-INF/jsp/error.jsp")).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleGeneralError(request, response, exception);

        // Assert
        verify(request).setAttribute("error", "An unexpected error occurred. Please try again later.");
        verify(request).setAttribute("errorCode", "INTERNAL_ERROR");
        verify(requestDispatcher).forward(request, response);
        verify(response, never()).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Should set correct error attributes for validation error")
    void handleValidationError_shouldSetCorrectErrorAttributes() throws ServletException, IOException {
        // Arrange
        String returnPage = "/WEB-INF/jsp/register.jsp";
        ValidationException exception = new ValidationException("Email is invalid", "email");
        
        when(request.getRequestDispatcher(returnPage)).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleValidationError(request, response, exception, returnPage);

        // Assert
        verify(request).setAttribute("error", "Email is invalid");
        verify(request).setAttribute("errorField", "email");
        verify(request).setAttribute("errorCode", exception.getErrorCode());
    }

    @Test
    @DisplayName("Should set correct error attributes for authentication error with invalid credentials")
    void handleAuthenticationError_shouldSetCorrectErrorAttributes() throws ServletException, IOException {
        // Arrange
        AuthenticationException exception = AuthenticationException.invalidCredentials();
        
        when(request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleAuthenticationError(request, response, exception);

        // Assert
        verify(request).setAttribute("error", "Invalid login or password");
    }

    @Test
    @DisplayName("Should set correct error attributes for general error")
    void handleGeneralError_shouldSetCorrectErrorAttributes() throws ServletException, IOException {
        // Arrange
        Exception exception = new RuntimeException("Database connection failed");
        
        when(request.getRequestDispatcher("/WEB-INF/jsp/error.jsp")).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleGeneralError(request, response, exception);

        // Assert
        verify(request).setAttribute("error", "An unexpected error occurred. Please try again later.");
        verify(request).setAttribute("errorCode", "INTERNAL_ERROR");
    }

    @Test
    @DisplayName("Should preserve form data with empty parameters")
    void handleValidationError_withEmptyParameterMap_shouldPreserveEmptyData() throws ServletException, IOException {
        // Arrange
        String returnPage = "/WEB-INF/jsp/register.jsp";
        ValidationException exception = new ValidationException("Username is required", "username");
        
        when(request.getParameterMap()).thenReturn(Collections.emptyMap());
        when(request.getRequestDispatcher(returnPage)).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleValidationError(request, response, exception, returnPage);

        // Assert
        verify(request).setAttribute(eq("formData"), any(Map.class));
    }

    @Test
    @DisplayName("Should handle authentication error with custom reason")
    void handleAuthenticationError_withCustomReason_shouldUseCustomMessage() throws ServletException, IOException {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Custom error message", "CUSTOM_REASON");
        
        when(request.getRequestDispatcher("/WEB-INF/jsp/login.jsp")).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleAuthenticationError(request, response, exception);

        // Assert
        verify(request).setAttribute("error", "Custom error message");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Should handle null parameter values in form data preservation")
    void handleValidationError_withNullParameterValues_shouldHandleGracefully() throws ServletException, IOException {
        // Arrange
        String returnPage = "/WEB-INF/jsp/register.jsp";
        ValidationException exception = new ValidationException("Username is required", "username");
        
        when(request.getParameterMap()).thenReturn(Map.of(
            "username", new String[]{null},
            "email", new String[]{"test@example.com"}
        ));
        when(request.getRequestDispatcher(returnPage)).thenReturn(requestDispatcher);
        
        errorHandler = new ErrorHandler();

        // Act
        errorHandler.handleValidationError(request, response, exception, returnPage);

        // Assert
        verify(request).setAttribute(eq("formData"), any(Map.class));
        verify(requestDispatcher).forward(request, response);
    }
}
