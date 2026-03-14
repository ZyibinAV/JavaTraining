package com.homeapp.javatraining.controllers;

import com.homeapp.javatraining.model.InterviewState;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.session.SessionUtils;
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

    @Override
    protected void initializeSpecificServices() {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("POST /start");

        String[] topicParams = req.getParameterValues("topics");
        if (topicParams == null || topicParams.length == 0) {
            log.warn("Interview start failed: no topics selected");
            throw new IllegalArgumentException("No topics selected");
        }

        Set<Topic> selectedTopics = Arrays.stream(topicParams)
                .map(Topic::valueOf)
                .collect(Collectors.toSet());

        log.debug("Selected topics: {}", selectedTopics.stream()
                .map(Topic::getCode)
                .collect(Collectors.joining(", ")));

        int questionCount = Integer.parseInt(req.getParameter("questionCount"));

        List<Question> allQuestions = new ArrayList<>();
        for (Topic topic : selectedTopics) {
            allQuestions.addAll(questionRepository.getQuestions(topic));
        }

        if (allQuestions.size() < questionCount) {
            log.warn("Not enough questions: requested={}, available={}",
                    questionCount, allQuestions.size());
            throw new IllegalStateException("Not enough questions for the topic");
        }

        Collections.shuffle(allQuestions);
        List<Question> selectedQuestions = allQuestions.subList(0, questionCount);

        InterviewState interviewState = new InterviewState(selectedTopics, selectedQuestions);
        HttpSession session = req.getSession(true);
        SessionUtils.setInterviewState(session, interviewState);

        log.info("Interview started: topics={}, questions={}",
                selectedTopics.size(), questionCount);

        resp.sendRedirect(req.getContextPath() + "/question");
    }

}
