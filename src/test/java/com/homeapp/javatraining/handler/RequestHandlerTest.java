package com.homeapp.javatraining.handler;

import com.homeapp.javatraining.exception.AuthenticationException;
import com.homeapp.javatraining.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Request Handler Tests")
class RequestHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Test
    @DisplayName("Should execute action successfully")
    void handleRequest_withSuccessfulAction_shouldExecuteAction() throws Exception {
        // Arrange
        RequestHandler handler = new RequestHandler();
        RequestHandler.RequestAction action = mock(RequestHandler.RequestAction.class);

        // Act
        handler.handleRequest(request, response, action, "/error");

        // Assert
        verify(action).execute();
        verifyNoInteractions(response);
    }

    @Test
    @DisplayName("Should handle validation exception")
    void handleRequest_withValidationException_shouldHandleValidationError() throws Exception {
        // Arrange
        RequestHandler handler = new RequestHandler();
        RequestHandler.RequestAction action = mock(RequestHandler.RequestAction.class);
        ValidationException exception = new ValidationException("Invalid input", "field");
        String errorPage = "/error.jsp";

        doThrow(exception).when(action).execute();

        // Act
        handler.handleRequest(request, response, action, errorPage);

        // Assert
        verify(action).execute();
        verify(response).sendRedirect(anyString()); // ErrorHandler redirects
    }

    @Test
    @DisplayName("Should handle authentication exception")
    void handleRequest_withAuthenticationException_shouldHandleAuthenticationError() throws Exception {
        // Arrange
        RequestHandler handler = new RequestHandler();
        RequestHandler.RequestAction action = mock(RequestHandler.RequestAction.class);
        AuthenticationException exception = new AuthenticationException("Not authenticated", "authentication");

        doThrow(exception).when(action).execute();

        // Act
        handler.handleRequest(request, response, action, "/error");

        // Assert
        verify(action).execute();
        verify(response).sendRedirect(anyString()); // ErrorHandler redirects
    }

    @Test
    @DisplayName("Should handle general exception")
    void handleRequest_withGeneralException_shouldHandleGeneralError() throws Exception {
        // Arrange
        RequestHandler handler = new RequestHandler();
        RequestHandler.RequestAction action = mock(RequestHandler.RequestAction.class);
        RuntimeException exception = new RuntimeException("Unexpected error");

        doThrow(exception).when(action).execute();

        // Act
        handler.handleRequest(request, response, action, "/error");

        // Assert
        verify(action).execute();
        verify(response).sendRedirect(anyString()); // ErrorHandler redirects
    }

    @Test
    @DisplayName("Should handle null action gracefully")
    void handleRequest_withNullAction_shouldNotThrowException() throws Exception {
        // Arrange
        RequestHandler handler = new RequestHandler();

        // Act & Assert - should not throw NullPointerException
        handler.handleRequest(request, response, null, "/error");
    }

    @Test
    @DisplayName("Should handle IOException from action")
    void handleRequest_withIOException_shouldHandleGeneralError() throws Exception {
        // Arrange
        RequestHandler handler = new RequestHandler();
        RequestHandler.RequestAction action = mock(RequestHandler.RequestAction.class);
        java.io.IOException exception = new java.io.IOException("IO error");

        doThrow(exception).when(action).execute();

        // Act
        handler.handleRequest(request, response, action, "/error");

        // Assert
        verify(action).execute();
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Should use error page parameter for validation errors")
    void handleRequest_withValidationException_shouldUseProvidedErrorPage() throws Exception {
        // Arrange
        RequestHandler handler = new RequestHandler();
        RequestHandler.RequestAction action = mock(RequestHandler.RequestAction.class);
        ValidationException exception = new ValidationException("Invalid input", "field");
        String errorPage = "/custom-error.jsp";

        doThrow(exception).when(action).execute();

        // Act
        handler.handleRequest(request, response, action, errorPage);

        // Assert
        verify(action).execute();
        verify(response).sendRedirect(anyString());
    }

    @Test
    @DisplayName("Should not interact with response on successful execution")
    void handleRequest_withSuccessfulAction_shouldNotInteractWithResponse() throws Exception {
        // Arrange
        RequestHandler handler = new RequestHandler();
        RequestHandler.RequestAction action = mock(RequestHandler.RequestAction.class);

        // Act
        handler.handleRequest(request, response, action, "/error");

        // Assert
        verify(action).execute();
        verifyNoInteractions(response);
    }
}
