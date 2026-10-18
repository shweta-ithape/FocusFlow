package com.focusflow.service;

import com.focusflow.dto.ScheduleRequest;
import com.focusflow.entity.*;
import com.focusflow.repository.StudySessionRepository;
import com.focusflow.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private StudySessionRepository studySessionRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    private Subject subjectUrgent;
    private Subject subjectLater;
    private Topic hardTopicUrgent;
    private Topic easyTopicLater;

    @BeforeEach
    void setUp() {
        LocalDate today = LocalDate.now();

        // Subject with urgent exam (2 days away, HIGH importance)
        subjectUrgent = new Subject("Algorithms", today.plusDays(2), Importance.HIGH);
        subjectUrgent.setId(1L);

        // Subject with exam later (25 days away, LOW importance)
        subjectLater = new Subject("Web Basics", today.plusDays(25), Importance.LOW);
        subjectLater.setId(2L);

        hardTopicUrgent = new Topic("Dynamic Programming", subjectUrgent, Difficulty.HARD, 90, false);
        hardTopicUrgent.setId(101L);

        easyTopicLater = new Topic("HTML Elements", subjectLater, Difficulty.EASY, 30, false);
        easyTopicLater.setId(102L);
    }

    @Test
    @DisplayName("Priority Score: Urgent hard topic should score higher than later easy topic")
    void testCalculatePriorityScore() {
        LocalDate today = LocalDate.now();

        double scoreUrgent = scheduleService.calculatePriorityScore(hardTopicUrgent, today);
        double scoreLater = scheduleService.calculatePriorityScore(easyTopicLater, today);

        // Urgent: (10 * 3) + (3 * 2) + 3 = 30 + 6 + 3 = 39
        // Later:  (3 * 3) + (1 * 2) + 1  = 9 + 2 + 1 = 12
        assertTrue(scoreUrgent > scoreLater, "Urgent subject topic must have a higher priority score");
    }

    @Test
    @DisplayName("Generate Schedule: Respects daily limit and splits topic when necessary")
    void testGenerateSchedule_WithTopicSplitting() {
        LocalDate today = LocalDate.now();

        when(topicRepository.findByCompletedFalse()).thenReturn(Arrays.asList(easyTopicLater, hardTopicUrgent));
        when(studySessionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Request 1 hour (60 minutes)
        ScheduleRequest request = new ScheduleRequest(today, 1.0);

        List<StudySession> sessions = scheduleService.generateSchedule(request);

        assertNotNull(sessions);
        assertFalse(sessions.isEmpty());

        // First topic scheduled should be the higher priority one (hardTopicUrgent)
        assertEquals(hardTopicUrgent.getId(), sessions.get(0).getTopic().getId());

        // Hard topic needs 90 mins, but only 60 mins available -> split into 60 mins session
        assertEquals(60, sessions.get(0).getPlannedMinutes());

        // Total planned minutes must equal the 60 min limit
        int totalPlanned = sessions.stream().mapToInt(StudySession::getPlannedMinutes).sum();
        assertEquals(60, totalPlanned);

        verify(studySessionRepository, times(1)).deleteByScheduledDate(today);
        verify(studySessionRepository, times(1)).saveAll(anyList());
    }
}
