package com.homeapp.javatraining.controllers.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homeapp.javatraining.controllers.BaseServlet;
import com.homeapp.javatraining.model.Answer;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.repository.QuestionRepository;
import com.homeapp.javatraining.repository.TopicRepository;
import com.homeapp.javatraining.util.TopicLoader;
import com.homeapp.javatraining.util.ValidationFactory;
import com.homeapp.javatraining.validation.QuestionValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@WebServlet("/admin/tests")
public class AdminTestServlet extends BaseServlet {

    private TopicLoader topicLoader;
    private TopicRepository topicRepository;
    private QuestionRepository questionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void initializeSpecificServices() {
        this.topicLoader = (TopicLoader) getServletContext().getAttribute("topicLoader");
        this.topicRepository = (TopicRepository) getServletContext().getAttribute("topicRepository");
        this.questionRepository = (QuestionRepository) getServletContext().getAttribute("questionRepository");
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
        log.debug("POST /admin/tests");

        String action = req.getParameter("action");
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
        List<Topic> topics = topicLoader.loadAllTopics();
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

        Topic topic = topicLoader.findByCode(topicCode);
        if (topic == null) {
            log.error("Topic not found: {}", topicCode);
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
            return;
        }

        List<Question> questions = questionRepository.getQuestions(topic);
        req.setAttribute("topic", topic);
        req.setAttribute("questions", questions);
        req.getRequestDispatcher("/WEB-INF/jsp/admin/test-questions.jsp").forward(req, resp);
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
            Question question = questionRepository.findById(questionId).orElse(null);

            if (question == null) {
                log.error("Question not found: {}", questionId);
                req.setAttribute("error", "Question not found");
                showQuestionsList(req, resp);
                return;
            }

            Topic topic = topicLoader.findByCode(topicCode);
            if (topic == null) {
                log.error("Topic not found: {}", topicCode);
                resp.sendRedirect(req.getContextPath() + "/admin/tests");
                return;
            }

            req.setAttribute("topic", topic);
            req.setAttribute("question", question);
            req.setAttribute("editMode", true);
            req.getRequestDispatcher("/WEB-INF/jsp/admin/test-questions.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            log.error("Invalid question ID", e);
            req.setAttribute("error", "Invalid question ID");
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

        Topic topic = new Topic(code.trim(), displayName.trim());
        try {
            topicRepository.save(topic);
            log.info("Topic added: {}", code);
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

        Topic topic = topicLoader.findByCode(topicCode);
        if (topic == null) {
            log.error("Topic not found: {}", topicCode);
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
            return;
        }

        try {
            topicRepository.delete(topic);
            log.info("Topic deleted: {}", topicCode);
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
        } catch (Exception e) {
            log.error("Error deleting topic", e);
            req.setAttribute("error", "Error deleting topic: " + e.getMessage());
            showTopicsList(req, resp);
        }
    }

    private void uploadJson(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String topicCode = req.getParameter("topicCode");
        if (topicCode == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
            return;
        }

        Topic topic = topicLoader.findByCode(topicCode);
        if (topic == null) {
            log.error("Topic not found: {}", topicCode);
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

            InputStream inputStream = filePart.getInputStream();
            List<Map<String, Object>> rawQuestions = objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            List<Question> questions = new ArrayList<>();
            for (Map<String, Object> raw : rawQuestions) {
                Question question = new Question();
                question.setQuestionText((String) raw.get("questionText"));
                question.setCorrectAnswerIndex((Integer) raw.get("correctAnswerIndex"));
                question.setTopic(topic);

                List<String> rawAnswers = (List<String>) raw.get("answers");
                List<Answer> answers = new ArrayList<>();

                for (int i = 0; i < rawAnswers.size(); i++) {
                    Answer answer = new Answer();
                    answer.setAnswerText(rawAnswers.get(i));
                    answer.setAnswerIndex(i);
                    answer.setQuestion(question);
                    answers.add(answer);
                }
                question.setAnswers(answers);
                questions.add(question);
            }

            QuestionValidator questionValidator = ValidationFactory.createQuestionValidator();
            questionValidator.validate(questions);

            questionRepository.saveAll(questions);
            log.info("JSON file uploaded for topic: {}, questions count: {}", topicCode, questions.size());
            req.setAttribute("success", "JSON file uploaded successfully. Added " + questions.size() + " questions.");
            showQuestionsList(req, resp);

        } catch (Exception e) {
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

        Topic topic = topicLoader.findByCode(topicCode);
        if (topic == null) {
            log.error("Topic not found: {}", topicCode);
            resp.sendRedirect(req.getContextPath() + "/admin/tests");
            return;
        }

        try {
            int correctAnswerIndex = Integer.parseInt(correctAnswerIndexStr);
            Question question = new Question();
            question.setQuestionText(questionText.trim());
            question.setCorrectAnswerIndex(correctAnswerIndex);
            question.setTopic(topic);

            List<Answer> answers = new ArrayList<>();
            int answerIndex = 0;
            while (req.getParameter("answer" + answerIndex) != null) {
                String answerText = req.getParameter("answer" + answerIndex);
                if (answerText != null && !answerText.trim().isEmpty()) {
                    Answer answer = new Answer();
                    answer.setAnswerText(answerText.trim());
                    answer.setAnswerIndex(answerIndex);
                    answer.setQuestion(question);
                    answers.add(answer);
                }
                answerIndex++;
            }

            if (answers.size() < 2) {
                req.setAttribute("error", "At least 2 answers are required");
                showQuestionsList(req, resp);
                return;
            }

            if (correctAnswerIndex >= answers.size()) {
                req.setAttribute("error", "Correct answer index must be less than number of answers");
                showQuestionsList(req, resp);
                return;
            }

            question.setAnswers(answers);

            QuestionValidator questionValidator = ValidationFactory.createQuestionValidator();
            questionValidator.validate(List.of(question));

            questionRepository.save(question);
            log.info("Question added for topic: {}", topicCode);
            req.setAttribute("success", "Question added successfully");
            showQuestionsList(req, resp);

        } catch (NumberFormatException e) {
            log.error("Invalid correct answer index", e);
            req.setAttribute("error", "Correct answer index must be a number");
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
            Question question = questionRepository.findById(questionId).orElse(null);

            if (question == null) {
                log.error("Question not found: {}", questionId);
                req.setAttribute("error", "Question not found");
                showQuestionsList(req, resp);
                return;
            }

            questionRepository.delete(question);
            log.info("Question deleted: id={}", questionId);
            req.setAttribute("success", "Question deleted successfully");
            showQuestionsList(req, resp);

        } catch (NumberFormatException e) {
            log.error("Invalid question ID", e);
            req.setAttribute("error", "Invalid question ID");
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
            Question question = questionRepository.findById(questionId).orElse(null);

            if (question == null) {
                log.error("Question not found: {}", questionId);
                req.setAttribute("error", "Question not found");
                showQuestionsList(req, resp);
                return;
            }

            Topic topic = topicLoader.findByCode(topicCode);
            if (topic == null) {
                log.error("Topic not found: {}", topicCode);
                resp.sendRedirect(req.getContextPath() + "/admin/tests");
                return;
            }

            int correctAnswerIndex = Integer.parseInt(correctAnswerIndexStr);
            question.setQuestionText(questionText.trim());
            question.setCorrectAnswerIndex(correctAnswerIndex);

            List<Answer> answers = new ArrayList<>();
            int answerIndex = 0;
            while (req.getParameter("answer" + answerIndex) != null) {
                String answerText = req.getParameter("answer" + answerIndex);
                if (answerText != null && !answerText.trim().isEmpty()) {
                    Answer answer = new Answer();
                    answer.setAnswerText(answerText.trim());
                    answer.setAnswerIndex(answerIndex);
                    answer.setQuestion(question);
                    answers.add(answer);
                }
                answerIndex++;
            }

            if (answers.size() < 2) {
                req.setAttribute("error", "At least 2 answers are required");
                showQuestionsList(req, resp);
                return;
            }

            if (correctAnswerIndex >= answers.size()) {
                req.setAttribute("error", "Correct answer index must be less than number of answers");
                showQuestionsList(req, resp);
                return;
            }

            question.setAnswers(answers);

            QuestionValidator questionValidator = ValidationFactory.createQuestionValidator();
            questionValidator.validate(List.of(question));

            questionRepository.save(question);
            log.info("Question updated: id={}", questionId);
            req.setAttribute("success", "Question updated successfully");
            showQuestionsList(req, resp);

        } catch (NumberFormatException e) {
            log.error("Invalid correct answer index or question ID", e);
            req.setAttribute("error", "Invalid number format");
            showQuestionsList(req, resp);
        } catch (Exception e) {
            log.error("Error updating question", e);
            req.setAttribute("error", "Error updating question: " + e.getMessage());
            showQuestionsList(req, resp);
        }
    }
}
