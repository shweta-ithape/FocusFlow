package com.focusflow.service;

import com.focusflow.dto.ScheduleRequest;
import com.focusflow.dto.SessionStatusUpdateRequest;
import com.focusflow.entity.Difficulty;
import com.focusflow.entity.Importance;
import com.focusflow.entity.SessionStatus;
import com.focusflow.entity.StudySession;
import com.focusflow.entity.Topic;
import com.focusflow.exception.BadRequestException;
import com.focusflow.exception.ResourceNotFoundException;
import com.focusflow.repository.StudySessionRepository;
import com.focusflow.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ScheduleService implements the core Greedy Scheduling Algorithm for FocusFlow.
 * 
 * ALGORITHM EXPLANATION (Great for Fresher Interviews & Viva):
 * 1. Filtering: Retrieve all incomplete topics whose subject's exam date hasn't passed.
 * 2. Scoring: Calculate a weighted Priority Score for each topic based on:
 *       - Urgency (how soon the exam is)
 *       - Importance (Subject priority: High, Medium, Low)
 *       - Difficulty (Topic complexity: Hard, Medium, Easy)
 * 3. Greedy Sorting: Sort topics in descending order of Priority Score.
 * 4. Time Allocation & Splitting: Sequentially allocate available daily study minutes
 *    to top-priority topics without exceeding the student's daily study limit.
 * 5. Deduplication: Clean up any previous plan for that date before generating fresh sessions.
 */
@Service
@Transactional
public class ScheduleService {

    private final TopicRepository topicRepository;
    private final StudySessionRepository studySessionRepository;

    public ScheduleService(TopicRepository topicRepository, StudySessionRepository studySessionRepository) {
        this.topicRepository = topicRepository;
        this.studySessionRepository = studySessionRepository;
    }

    /**
     * Generates a study schedule for the given date and available study hours.
     */
    public List<StudySession> generateSchedule(ScheduleRequest request) {
        LocalDate targetDate = request.getDate();
        if (targetDate == null) {
            throw new BadRequestException("Target schedule date is required");
        }

        int totalAvailableMinutes = (int) Math.round(request.getAvailableHours() * 60);
        if (totalAvailableMinutes <= 0) {
            throw new BadRequestException("Available study hours must be greater than zero");
        }

        // Step 1: Clean existing plan for this date to avoid duplicate sessions on regeneration
        studySessionRepository.deleteByScheduledDate(targetDate);
        studySessionRepository.flush();

        // Step 2: Retrieve all incomplete topics from MySQL
        List<Topic> incompleteTopics = topicRepository.findByCompletedFalse();
        if (incompleteTopics.isEmpty()) {
            return new ArrayList<>(); // Safe return when all topics are completed
        }

        // Step 3: Filter out topics whose subject exam date has already passed
        List<Topic> validTopics = new ArrayList<>();
        for (Topic topic : incompleteTopics) {
            if (topic.getSubject() != null && topic.getSubject().getExamDate() != null) {
                if (!topic.getSubject().getExamDate().isBefore(targetDate)) {
                    validTopics.add(topic);
                }
            }
        }

        if (validTopics.isEmpty()) {
            return new ArrayList<>(); // No topics with upcoming exam dates
        }

        // Step 4: Sort topics descending by priority score (Greedy Choice Property)
        validTopics.sort(Comparator.comparingDouble((Topic t) -> calculatePriorityScore(t, targetDate)).reversed());

        // Step 5: Allocate available study time
        List<StudySession> plannedSessions = new ArrayList<>();
        int remainingMinutes = totalAvailableMinutes;

        for (Topic topic : validTopics) {
            if (remainingMinutes <= 0) {
                break; // Student's daily study limit reached
            }

            int estimatedDuration = topic.getEstimatedMinutes();
            if (estimatedDuration <= 0) {
                continue; // Skip invalid topic duration safely
            }

            int sessionDuration;
            if (estimatedDuration <= remainingMinutes) {
                // Topic fits fully into the remaining daily study time
                sessionDuration = estimatedDuration;
            } else {
                // Topic Splitting: topic takes longer than remaining time,
                // so we allocate whatever remaining study time is available.
                sessionDuration = remainingMinutes;
            }

            StudySession session = new StudySession(
                    topic,
                    targetDate,
                    sessionDuration,
                    0,
                    SessionStatus.PLANNED
            );
            plannedSessions.add(session);

            remainingMinutes -= sessionDuration;
        }

        // Step 6: Save and return the generated sessions
        return studySessionRepository.saveAll(plannedSessions);
    }

    /**
     * Calculates the Priority Score for a topic.
     * Formula: (Urgency Score * 3) + (Importance Score * 2) + Difficulty Score
     */
    public double calculatePriorityScore(Topic topic, LocalDate currentDate) {
        double urgencyScore = calculateUrgencyScore(topic.getSubject().getExamDate(), currentDate);
        double importanceScore = calculateImportanceScore(topic.getSubject().getImportance());
        double difficultyScore = calculateDifficultyScore(topic.getDifficulty());

        // Weighted combination: Urgency is prioritized most, followed by importance and difficulty
        return (urgencyScore * 3.0) + (importanceScore * 2.0) + difficultyScore;
    }

    /**
     * Urgency Score: Subjects with closer exam dates receive higher priority.
     */
    private double calculateUrgencyScore(LocalDate examDate, LocalDate currentDate) {
        long daysUntilExam = ChronoUnit.DAYS.between(currentDate, examDate);

        if (daysUntilExam < 0) {
            return 0.0; // Exam already passed
        } else if (daysUntilExam <= 3) {
            return 10.0; // Urgent: within 3 days
        } else if (daysUntilExam <= 7) {
            return 7.0;  // High urgency: within 1 week
        } else if (daysUntilExam <= 14) {
            return 5.0;  // Moderate urgency: within 2 weeks
        } else if (daysUntilExam <= 30) {
            return 3.0;  // Normal: within 1 month
        } else {
            return 1.0;  // Long term: more than 1 month away
        }
    }

    /**
     * Importance Score: High importance subjects receive higher weight.
     */
    private double calculateImportanceScore(Importance importance) {
        if (importance == null) return 1.0;
        return switch (importance) {
            case HIGH -> 3.0;
            case MEDIUM -> 2.0;
            case LOW -> 1.0;
        };
    }

    /**
     * Difficulty Score: Hard topics receive higher weight.
     */
    private double calculateDifficultyScore(Difficulty difficulty) {
        if (difficulty == null) return 1.0;
        return switch (difficulty) {
            case HARD -> 3.0;
            case MEDIUM -> 2.0;
            case EASY -> 1.0;
        };
    }

    /**
     * Fetch all sessions for a specific date.
     */
    public List<StudySession> getSessionsByDate(LocalDate date) {
        return studySessionRepository.findByScheduledDateOrderByIdAsc(date);
    }

    /**
     * Update session status and actual study duration.
     */
    public StudySession updateSessionStatus(Long sessionId, SessionStatusUpdateRequest request) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found with id: " + sessionId));

        session.setStatus(request.getStatus());
        if (request.getActualMinutes() > 0) {
            session.setActualMinutes(request.getActualMinutes());
        } else if (request.getStatus() == SessionStatus.COMPLETED && session.getActualMinutes() == 0) {
            // Default actual minutes to planned minutes if marked completed without explicit duration
            session.setActualMinutes(session.getPlannedMinutes());
        }

        return studySessionRepository.save(session);
    }

    /**
     * Delete a study session.
     */
    public void deleteSession(Long sessionId) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found with id: " + sessionId));
        studySessionRepository.delete(session);
    }
}
