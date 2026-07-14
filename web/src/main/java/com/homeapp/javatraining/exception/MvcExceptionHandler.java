package com.homeapp.javatraining.exception;

import com.homeapp.javatraining.exception.question.NotEnoughQuestionsException;
import com.homeapp.javatraining.exception.question.QuestionImportException;
import com.homeapp.javatraining.exception.question.QuestionNotFoundException;
import com.homeapp.javatraining.exception.topic.TopicNotFoundException;
import com.homeapp.javatraining.exception.user.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = Controller.class)
public class MvcExceptionHandler {

    @ExceptionHandler({CannotBlockSelfException.class, CannotChangeOwnRoleException.class})
    public String handleAdminActionError(RedirectAttributes redirectAttributes, Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/admin/users";
    }

    @ExceptionHandler({UserNotFoundException.class, TopicNotFoundException.class, QuestionNotFoundException.class})
    public String handleNotFound(RedirectAttributes redirectAttributes, Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/admin";
    }

    @ExceptionHandler({NotEnoughQuestionsException.class, QuestionImportException.class, ValidationException.class})
    public String handleBadRequest(RedirectAttributes redirectAttributes, Exception e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        return "redirect:/admin/tests";
    }
}
