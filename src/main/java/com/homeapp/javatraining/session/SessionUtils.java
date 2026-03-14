package com.homeapp.javatraining.session;

import com.homeapp.javatraining.model.InterviewState;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    public static final String INTERVIEW_STATE = "interviewState";

    public static boolean hasInterview(HttpSession session) {
        return session != null && session.getAttribute(INTERVIEW_STATE) != null;
    }

    public static InterviewState getInterviewState(HttpSession session) {
        return (InterviewState) session.getAttribute(INTERVIEW_STATE);
    }

    public static void setInterviewState(HttpSession session, InterviewState state) {
        session.setAttribute(INTERVIEW_STATE, state);
    }

    public static void clearInterview(HttpSession session) {
        session.removeAttribute(INTERVIEW_STATE);
    }
}
