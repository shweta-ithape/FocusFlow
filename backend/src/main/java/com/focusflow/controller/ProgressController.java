package com.focusflow.controller;

import com.focusflow.dto.DashboardStatsDto;
import com.focusflow.dto.WeeklyProgressDto;
import com.focusflow.service.ProgressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Dashboard and Weekly Progress analytics.
 */
@RestController
@RequestMapping("/api/progress")
@CrossOrigin(origins = "http://localhost:4200")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    /**
     * Get real-time stats for dashboard KPI cards.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(progressService.getDashboardStats());
    }

    /**
     * Get weekly study session metrics and subject breakdown.
     */
    @GetMapping("/weekly")
    public ResponseEntity<WeeklyProgressDto> getWeeklyProgress() {
        return ResponseEntity.ok(progressService.getWeeklyProgress());
    }
}
