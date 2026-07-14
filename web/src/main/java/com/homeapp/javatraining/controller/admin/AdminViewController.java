package com.homeapp.javatraining.controller.admin;

import com.homeapp.javatraining.dto.*;
import com.homeapp.javatraining.dto.mapper.QuestionMapper;
import com.homeapp.javatraining.dto.mapper.TopicMapper;
import com.homeapp.javatraining.dto.mapper.UserMapper;
import com.homeapp.javatraining.exception.question.QuestionNotFoundException;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.service.AdminStatisticsService;
import com.homeapp.javatraining.service.AdminTestService;
import com.homeapp.javatraining.service.AdminUserService;
import com.homeapp.javatraining.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminViewController {

    private final AdminUserService adminUserService;
    private final AdminTestService adminTestService;
    private final AdminStatisticsService adminStatisticsService;
    private final UserMapper userMapper;
    private final TopicMapper topicMapper;
    private final QuestionMapper questionMapper;
    private final CurrentUserService currentUserService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("statistics", adminStatisticsService.getStatistics());
        return "admin";
    }

    @GetMapping("/users")
    public String users(Model model) {
        List<UserDTO> users = adminUserService.getAllUsers().stream()
                .map(userMapper::toUserDTO)
                .toList();
        model.addAttribute("users", users);
        return "users";
    }

    @PostMapping("/users/{id}/block")
    public String toggleBlock(Authentication authentication, @PathVariable Long id) {
        Long adminId = currentUserService.getCurrentUserId(authentication);
        adminUserService.toggleBlockUser(adminId, id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(Authentication authentication,
                             @PathVariable Long id,
                             @RequestParam String role) {
        Long adminId = currentUserService.getCurrentUserId(authentication);
        adminUserService.changeUserRole(adminId, id, Role.valueOf(role));
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        adminUserService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/tests")
    public String tests(Model model) {
        List<TopicDTO> topics = topicMapper.toTopicDTOList(adminTestService.getAllTopics());
        model.addAttribute("topics", topics);
        return "tests";
    }

    @PostMapping("/tests/create")
    public String createTopic(@RequestParam String code,
                              @RequestParam String displayName) {
        adminTestService.createTopic(code, displayName);
        return "redirect:/admin/tests";
    }

    @PostMapping("/tests/{code}/delete")
    public String deleteTopic(@PathVariable String code) {
        adminTestService.deleteTopic(code);
        return "redirect:/admin/tests";
    }

    @GetMapping("/tests/{code}/questions")
    public String questions(@PathVariable String code, Model model) {
        List<QuestionDTO> questions = adminTestService.getQuestionsByTopic(code).stream()
                .map(questionMapper::toQuestionDTO)
                .toList();
        model.addAttribute("topicCode", code);
        model.addAttribute("questions", questions);
        return "test-questions";
    }

    @PostMapping("/tests/{code}/questions/{id}/delete")
    public String deleteQuestion(@PathVariable String code, @PathVariable Long id) {
        adminTestService.deleteQuestion(id);
        return "redirect:/admin/tests/" + code + "/questions";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        model.addAttribute("statistics", adminStatisticsService.getStatistics());
        return "statistics";
    }

    @PostMapping("/tests/{code}/questions/create")
    public String createQuestion(@PathVariable String code,
                                 @RequestParam String questionText,
                                 @RequestParam String answers,
                                 @RequestParam(defaultValue = "0") int correctAnswerIndex) {
        List<String> answerList = Arrays.stream(answers.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        adminTestService.createQuestion(code, questionText, correctAnswerIndex, answerList);
        return "redirect:/admin/tests/" + code + "/questions";
    }

    @PostMapping("/tests/{code}/questions/import")
    public String importQuestions(@PathVariable String code,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            adminTestService.importQuestionsFromJson(code, file.getInputStream());
        }
        return "redirect:/admin/tests/" + code + "/questions";
    }

    @GetMapping("/tests/{code}/questions/{id}/edit")
    public String editQuestionForm(@PathVariable String code,
                                   @PathVariable Long id,
                                   Model model) {
        QuestionDTO question = questionMapper.toQuestionDTO(
                adminTestService.getQuestionById(id)
                        .orElseThrow(() -> new QuestionNotFoundException(id)));
        model.addAttribute("question", question);
        model.addAttribute("topicCode", code);
        return "question-edit";
    }

    @PostMapping("/tests/{code}/questions/{id}/edit")
    public String editQuestion(@PathVariable String code,
                               @PathVariable Long id,
                               @RequestParam String questionText,
                               @RequestParam("answerText") List<String> answerTexts,
                               @RequestParam int correctAnswerIndex) {
        List<String> answerList = answerTexts.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        adminTestService.updateQuestion(id, code, questionText, correctAnswerIndex, answerList);
        return "redirect:/admin/tests/" + code + "/questions";
    }
}
