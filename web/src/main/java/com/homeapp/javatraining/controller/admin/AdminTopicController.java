package com.homeapp.javatraining.controller.admin;

import com.homeapp.javatraining.dto.*;
import com.homeapp.javatraining.model.Answer;
import com.homeapp.javatraining.model.Question;
import com.homeapp.javatraining.model.Topic;
import com.homeapp.javatraining.service.AdminTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/topics")
@RequiredArgsConstructor
@Slf4j
public class AdminTopicController {

    private final AdminTestService adminTestService;

    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {
        log.debug("GET /api/admin/topics");
        return ResponseEntity.ok(adminTestService.getAllTopics());
    }

    @PostMapping
    public ResponseEntity<Topic> createTopic(@RequestBody TopicRequest request) {
        log.debug("POST /api/admin/topics: {} - {}", request.code(), request.displayName());
        Topic topic = adminTestService.createTopic(request.code(), request.displayName());
        return ResponseEntity.status(HttpStatus.CREATED).body(topic);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteTopic(@PathVariable String code) {
        log.debug("DELETE /api/admin/topics/{}", code);
        adminTestService.deleteTopic(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{code}/questions")
    public ResponseEntity<List<QuestionResponse>> getQuestions(@PathVariable String code) {
        log.debug("GET /api/admin/topics/{}/questions", code);
        List<Question> questions = adminTestService.getQuestionsByTopic(code);
        List<QuestionResponse> response = questions.stream()
                .map(this::toQuestionResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{code}/questions")
    public ResponseEntity<Question> createQuestion(@PathVariable String code, @RequestBody QuestionCreateRequest request) {
        log.debug("POST /api/admin/topics/{}/questions", code);
        Question question = adminTestService.createQuestion(code, request.questionText(), request.correctAnswerIndex(), request.answers());
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }

    @PutMapping("/{code}/questions/{id}")
    public ResponseEntity<Void> updateQuestion(@PathVariable String code, @PathVariable Long id, @RequestBody QuestionUpdateRequest request) {
        log.debug("PUT /api/admin/topics/{}/questions/{}", code, id);
        adminTestService.updateQuestion(id, code, request.questionText(), request.correctAnswerIndex(), request.answers());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{code}/questions/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        log.debug("DELETE /api/admin/topics/questions/{}", id);
        adminTestService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{code}/import")
    public ResponseEntity<List<Question>> importQuestions(@PathVariable String code, @RequestParam("file") MultipartFile file) throws IOException {
        log.debug("POST /api/admin/topics/{}/import", code);
        List<Question> questions = adminTestService.importQuestionsFromJson(code, file.getInputStream());
        return ResponseEntity.ok(questions);
    }

    private QuestionResponse toQuestionResponse(Question q) {
        return new QuestionResponse(
                q.getId(),
                q.getQuestionText(),
                toAnswerItems(q.getAnswers()),
                0, 0, 0, false
        );
    }

    private List<AnswerItem> toAnswerItems(List<Answer> answers) {
        return answers.stream()
                .map(a -> new AnswerItem(a.getAnswerIndex(), a.getAnswerText()))
                .collect(Collectors.toList());
    }
}