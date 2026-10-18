package com.focusflow.controller;

import com.focusflow.dto.ScheduleRequest;
import com.focusflow.dto.SessionStatusUpdateRequest;
import com.focusflow.entity.StudySession;
import com.focusflow.service.ScheduleService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Study Schedule generation and Session tracking.
 */
@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "http://localhost:4200")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * Trigger greedy schedule generation for a specific date and available study hours.
     */
    @PostMapping("/generate")
    public ResponseEntity<List<StudySession>> generateSchedule(@Valid @RequestBody ScheduleRequest request) {
        List<StudySession> sessions = scheduleService.generateSchedule(request);
        return new ResponseEntity<>(sessions, HttpStatus.CREATED);
    }

    /**
     * Fetch study sessions scheduled for a particular date (defaults to today if omitted).
     */
    @GetMapping
    public ResponseEntity<List<StudySession>> getSessionsByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate queryDate = (date != null) ? date : LocalDate.now();
        return ResponseEntity.ok(scheduleService.getSessionsByDate(queryDate));
    }

    /**
     * Update session status (PLANNED, COMPLETED, INCOMPLETE) and actual minutes studied.
     */
    @PatchMapping("/sessions/{id}/status")
    public ResponseEntity<StudySession> updateSessionStatus(
            @PathVariable Long id,
            @Valid @RequestBody SessionStatusUpdateRequest request) {
        return ResponseEntity.ok(scheduleService.updateSessionStatus(id, request));
    }

    /**
     * Delete an individual study session.
     */
    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        scheduleService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
