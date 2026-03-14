package com.homeapp.javatraining.handler;

import com.homeapp.javatraining.exception.AuthenticationException;
import com.homeapp.javatraining.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class RequestHandler {

    private final ErrorHandler errorHandler;

    public RequestHandler() {
        this.errorHandler = new ErrorHandler();
    }

    public void handleRequest(HttpServletRequest request, HttpServletResponse response,
                              RequestAction action, String errorPage) throws IOException {
        try {
            action.execute();
        } catch (ValidationException e) {
            errorHandler.handleValidationError(request, response, e, errorPage);
        } catch (AuthenticationException e) {
            errorHandler.handleAuthenticationError(request, response, e);
        } catch (Exception e) {
            errorHandler.handleGeneralError(request, response, e);
        }
    }

    @FunctionalInterface
    public interface RequestAction {
        void execute() throws Exception;
    }
}