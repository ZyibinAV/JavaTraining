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

    @Override
    protected void initializeSpecificServices() {
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

        List<Question> allQuestions = new ArrayList<>();

        for (String code : topicParams) {

            Topic topic = TopicLoader.findByCode(code);

            if (topic == null) {
                throw new IllegalStateException("Topic not found: " + code);
            }

            allQuestions.addAll(questionRepository.getQuestions(topic));
        }

        if (allQuestions.size() < questionCount) {
            throw new IllegalStateException("Not enough questions");
        }

        Collections.shuffle(allQuestions);
        List<Question> selectedQuestions = allQuestions.subList(0, questionCount);

        InterviewState interviewState = new InterviewState(new HashSet<>(), selectedQuestions);

        HttpSession session = req.getSession(true);
        SessionUtils.setInterviewState(session, interviewState);

        log.info("Interview started: questions={}", questionCount);

        resp.sendRedirect(req.getContextPath() + "/question");
    }

}
