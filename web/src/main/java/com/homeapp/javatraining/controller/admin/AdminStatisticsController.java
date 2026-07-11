package com.homeapp.javatraining.controller.admin;

import com.homeapp.javatraining.service.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
@Slf4j
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;

    @GetMapping
    public ResponseEntity<AdminStatisticsService.AdminStatisticsData> getStatistics() {
        log.debug("GET /api/admin/statistics");
        return ResponseEntity.ok(adminStatisticsService.getStatistics());
    }
}
