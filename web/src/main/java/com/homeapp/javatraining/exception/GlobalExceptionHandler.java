package com.homeapp.javatraining.exception;


import com.homeapp.javatraining.exception.question.NotEnoughQuestionsException;
import com.homeapp.javatraining.exception.question.QuestionImportException;
import com.homeapp.javatraining.exception.question.QuestionNotFoundException;
import com.homeapp.javatraining.exception.topic.TopicNotFoundException;
import com.homeapp.javatraining.exception.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                exception.getField()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        String field = Objects.requireNonNull(
                exception.getBindingResult().getFieldError()).getField();
        String message = Objects.requireNonNull(
                exception.getBindingResult().getFieldError()).getDefaultMessage();
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                field
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({
            UserNotFoundException.class,
            TopicNotFoundException.class,
            QuestionNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(ApiException exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({
            DuplicateUsernameException.class,
            DuplicateEmailException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(ApiException exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler({
            InvalidCredentialsException.class,
            InvalidRefreshTokenException.class
    })
    public ResponseEntity<ErrorResponse> handleUnauthorized(ApiException exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler({CannotChangeOwnRoleException.class,
            CannotBlockSelfException.class
    })
    public ResponseEntity<ErrorResponse> handleForbidden(ApiException exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler({
            NotEnoughQuestionsException.class,
            QuestionImportException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(ApiException exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                exception.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error",
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
