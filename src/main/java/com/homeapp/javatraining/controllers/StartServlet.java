package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.session.SessionUtils;
import com.homeapp.javatraining.util.TopicLoader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@WebServlet("/start")
public class StartServlet extends BaseServlet {

    private TopicLoader topicLoader;

    @Override
    protected void initializeSpecificServices() {
        this.topicLoader = (TopicLoader) getServletContext().getAttribute("topicLoader");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        log.debug("POST /start");

        String[] topicParams = req.getParameterValues("topics");
        if (topicParams == null || topicParams.length == 0) {
            throw new IllegalArgumentException("No topics selected");
        }

        int questionCount = Integer.parseInt(req.getParameter("questionCount"));

        List<String> topicCodes = Arrays.asList(topicParams);

        Set<Topic> selectedTopics = topicCodes.stream()
                .map(topicLoader::findByCode)
                .collect(Collectors.toSet());

        List<Question> selectedQuestions =
                questionService.getRandomQuestionsByTopics(topicCodes, questionCount);
        InterviewState interviewState = new InterviewState(selectedTopics, selectedQuestions);

        HttpSession session = req.getSession(true);
        SessionUtils.setInterviewState(session, interviewState);

        log.info("Interview started: questions={}", questionCount);

        resp.sendRedirect(req.getContextPath() + "/question");
    }

}
