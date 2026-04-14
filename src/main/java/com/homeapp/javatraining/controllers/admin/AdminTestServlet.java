package com.homeapp.javatraining.controllers.admin;

import com.homeapp.javatraining.controllers.BaseServlet;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.service.AdminTestService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/tests")
@MultipartConfig
public class AdminTestServlet extends BaseServlet {

    private AdminTestService adminTestService;

    @Override
    protected void initializeSpecificServices() {
        this.adminTestService = (AdminTestService) getServletContext().getAttribute("adminTestService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log.debug("GET /admin/tests");

        String action = req.getParameter("action");
        if (action == null) {
            showTopicsList(req, resp);
        } else if ("edit".equals(action)) {
            showQuestionsList(req, resp);
        } else if ("edit-question".equals(action)) {
            showEditQuestionForm(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        boolean isMultipart = req.getContentType() != null && req.getContentType().contains("multipart/form-data");

        String action;
        if (isMultipart) {
            // For multipart requests, extract action from Part
            try {
                Part actionPart = req.getPart("action");
                if (actionPart != null) {
                    action = new String(actionPart.getInputStream().readAllBytes());
                } else {
                    action = null;
                }
            } catch (Exception e) {
                log.error("Error reading action from multipart", e);
                action = null;
            }
        } else {
            action = req.getParameter("action");
        }
        if ("add-topic".equals(action)) {
            addTopic(req, resp);
        } else if ("delete-topic".equals(action)) {
            deleteTopic(req, resp);
        } else if ("upload-json".equals(action)) {
            uploadJson(req, resp);
        } else if ("add-question".equals(action)) {
            addQuestion(req, resp);
        } else if ("delete-question".equals(action)) {
            deleteQuestion(req, resp);
        } else if ("update-question".equals(action)) {
            updateQuestion(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
        }
    }

    private void showTopicsList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Topic> topics = adminTestService.getAllTopics();
        req.setAttribute("topics", topics);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/tests.jsp").forward(req, resp);
    }

    private void showQuestionsList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String topicCode = req.getParameter("topicCode");
        if (topicCode == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
            return;
        }

        try {
            Topic topic = adminTestService.getTopicByCode(topicCode);
            List<Question> questions = adminTestService.getQuestionsByTopic(topicCode);
            req.setAttribute("topic", topic);
            req.setAttribute("questions", questions);
            req.setAttribute("editMode", false);
            req.getRequestDispatcher("/WEB-INF/jsp/admin/test-questions.jsp").forward(req, resp);
        } catch (IllegalArgumentException e) {
            log.error("Topic not found: {}", topicCode);
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
        }
    }

    private void showEditQuestionForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String questionIdStr = req.getParameter("questionId");
        String topicCode = req.getParameter("topicCode");

        if (questionIdStr == null || topicCode == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
            return;
        }

        try {
            Long questionId = Long.parseLong(questionIdStr);
            Question question = adminTestService.getQuestionById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Question not found"));

            Topic topic = adminTestService.getTopicByCode(topicCode);
            req.setAttribute("topic", topic);
            req.setAttribute("question", question);
            req.setAttribute("editMode", true);
            req.getRequestDispatcher("/WEB-INF/jsp/admin/test-questions.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            log.error("Invalid question ID", e);
            req.setAttribute("error", "Invalid question ID");
            showQuestionsList(req, resp);
        } catch (IllegalArgumentException e) {
            log.error("Question or topic not found", e);
            req.setAttribute("error", e.getMessage());
            showQuestionsList(req, resp);
        }
    }

    private void addTopic(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String code = req.getParameter("code");
        String displayName = req.getParameter("displayName");

        if (code == null || code.trim().isEmpty() || displayName == null || displayName.trim().isEmpty()) {
            req.setAttribute("error", "Code and Display Name are required");
            showTopicsList(req, resp);
            return;
        }

        try {
            adminTestService.createTopic(code, displayName);
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
        } catch (Exception e) {
            log.error("Error adding topic", e);
            req.setAttribute("error", "Error adding topic: " + e.getMessage());
            showTopicsList(req, resp);
        }
    }

    private void deleteTopic(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String topicCode = req.getParameter("topicCode");
        if (topicCode == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
            return;
        }

        try {
            adminTestService.deleteTopic(topicCode);
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
        } catch (IllegalArgumentException e) {
            log.error("Topic not found: {}", topicCode);
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
        } catch (Exception e) {
            log.error("Error deleting topic", e);
            req.setAttribute("error", "Error deleting topic: " + e.getMessage());
            showTopicsList(req, resp);
        }
    }

    private void uploadJson(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // For multipart requests, use getPart() instead of getParameter()
        String topicCode = null;

        try {
            Part topicCodePart = req.getPart("topicCode");
            if (topicCodePart != null) {
                topicCode = new String(topicCodePart.getInputStream().readAllBytes());
            }
        } catch (Exception e) {
            log.error("Error reading multipart parameters", e);
        }

        if (topicCode == null) {
            log.error("topicCode is null, redirecting");
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
            return;
        }

        try {
            Part filePart = req.getPart("jsonFile");
            if (filePart == null || filePart.getSize() == 0) {
                req.setAttribute("error", "Please select a JSON file");
                showQuestionsList(req, resp);
                return;
            }

            List<Question> questions = adminTestService.importQuestionsFromJson(
                    topicCode, filePart.getInputStream());
            req.setAttribute("success", "JSON file uploaded successfully. Added " + questions.size() + " questions.");
            showQuestionsList(req, resp);

        } catch (IllegalArgumentException e) {
            log.error("Topic not found: {}", topicCode);
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
        } catch (RuntimeException e) {
            log.error("Error uploading JSON", e);
            req.setAttribute("error", "Error uploading JSON: " + e.getMessage());
            showQuestionsList(req, resp);
        }
    }

    private void addQuestion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String topicCode = req.getParameter("topicCode");
        String questionText = req.getParameter("questionText");
        String correctAnswerIndexStr = req.getParameter("correctAnswerIndex");

        if (topicCode == null || questionText == null || questionText.trim().isEmpty() || correctAnswerIndexStr == null) {
            req.setAttribute("error", "All fields are required");
            showQuestionsList(req, resp);
            return;
        }

        try {
            int correctAnswerIndex = Integer.parseInt(correctAnswerIndexStr);
            List<String> answerTexts = extractAnswerTexts(req);

            if (answerTexts.size() < 2) {
                req.setAttribute("error", "At least 2 answers are required");
                showQuestionsList(req, resp);
                return;
            }

            if (correctAnswerIndex >= answerTexts.size()) {
                req.setAttribute("error", "Correct answer index must be less than number of answers");
                showQuestionsList(req, resp);
                return;
            }

            adminTestService.createQuestion(topicCode, questionText, correctAnswerIndex, answerTexts);
            req.setAttribute("success", "Question added successfully");
            showQuestionsList(req, resp);

        } catch (NumberFormatException e) {
            log.error("Invalid correct answer index", e);
            req.setAttribute("error", "Correct answer index must be a number");
            showQuestionsList(req, resp);
        } catch (IllegalArgumentException e) {
            log.error("Topic not found", e);
            req.setAttribute("error", e.getMessage());
            showQuestionsList(req, resp);
        } catch (Exception e) {
            log.error("Error adding question", e);
            req.setAttribute("error", "Error adding question: " + e.getMessage());
            showQuestionsList(req, resp);
        }
    }

    private void deleteQuestion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String questionIdStr = req.getParameter("questionId");
        String topicCode = req.getParameter("topicCode");

        if (questionIdStr == null || topicCode == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
            return;
        }

        try {
            Long questionId = Long.parseLong(questionIdStr);
            adminTestService.deleteQuestion(questionId);
            req.setAttribute("success", "Question deleted successfully");
            showQuestionsList(req, resp);

        } catch (NumberFormatException e) {
            log.error("Invalid question ID", e);
            req.setAttribute("error", "Invalid question ID");
            showQuestionsList(req, resp);
        } catch (IllegalArgumentException e) {
            log.error("Question not found", e);
            req.setAttribute("error", e.getMessage());
            showQuestionsList(req, resp);
        } catch (Exception e) {
            log.error("Error deleting question", e);
            req.setAttribute("error", "Error deleting question: " + e.getMessage());
            showQuestionsList(req, resp);
        }
    }

    private void updateQuestion(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String questionIdStr = req.getParameter("questionId");
        String topicCode = req.getParameter("topicCode");
        String questionText = req.getParameter("questionText");
        String correctAnswerIndexStr = req.getParameter("correctAnswerIndex");

        if (questionIdStr == null || topicCode == null || questionText == null || questionText.trim().isEmpty() || correctAnswerIndexStr == null) {
            req.setAttribute("error", "All fields are required");
            showQuestionsList(req, resp);
            return;
        }

        try {
            Long questionId = Long.parseLong(questionIdStr);
            int correctAnswerIndex = Integer.parseInt(correctAnswerIndexStr);
            List<String> answerTexts = extractAnswerTexts(req);

            if (answerTexts.size() < 2) {
                req.setAttribute("error", "At least 2 answers are required");
                showQuestionsList(req, resp);
                return;
            }

            if (correctAnswerIndex >= answerTexts.size()) {
                req.setAttribute("error", "Correct answer index must be less than number of answers");
                showQuestionsList(req, resp);
                return;
            }

            adminTestService.updateQuestion(questionId, topicCode, questionText, correctAnswerIndex, answerTexts);
            req.setAttribute("success", "Question updated successfully");
            showQuestionsList(req, resp);

        } catch (NumberFormatException e) {
            log.error("Invalid correct answer index or question ID", e);
            req.setAttribute("error", "Invalid number format");
            showQuestionsList(req, resp);
        } catch (IllegalArgumentException e) {
            log.error("Question or topic not found", e);
            req.setAttribute("error", e.getMessage());
            showQuestionsList(req, resp);
        } catch (Exception e) {
            log.error("Error updating question", e);
            req.setAttribute("error", "Error updating question: " + e.getMessage());
            showQuestionsList(req, resp);
        }
    }

    private List<String> extractAnswerTexts(HttpServletRequest req) {
        List<String> answerTexts = new ArrayList<>();
        int answerIndex = 0;
        while (req.getParameter("answer" + answerIndex) != null) {
            String answerText = req.getParameter("answer" + answerIndex);
            if (answerText != null && !answerText.trim().isEmpty()) {
                answerTexts.add(answerText.trim());
            }
            answerIndex++;
        }
        return answerTexts;
    }
}
