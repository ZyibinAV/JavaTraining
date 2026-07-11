package com.homeapp.javatraining.controller.admin;

import com.homeapp.javatraining.dto.*;
import com.homeapp.javatraining.dto.mapper.QuestionMapper;
import com.homeapp.javatraining.dto.mapper.TopicMapper;
import com.homeapp.javatraining.dto.mapper.UserMapper;
import com.homeapp.javatraining.model.Role;
import com.homeapp.javatraining.service.AdminStatisticsService;
import com.homeapp.javatraining.service.AdminTestService;
import com.homeapp.javatraining.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public String toggleBlock(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        Long adminId = Long.parseLong(jwt.getSubject());
        adminUserService.toggleBlockUser(adminId, id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@AuthenticationPrincipal Jwt jwt,
                             @PathVariable Long id,
                             @RequestParam String role) {
        Long adminId = Long.parseLong(jwt.getSubject());
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
}
