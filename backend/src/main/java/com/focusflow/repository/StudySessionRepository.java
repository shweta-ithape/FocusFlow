package com.focusflow.repository;

import com.focusflow.entity.SessionStatus;
import com.focusflow.entity.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for StudySession entity.
 * Provides query methods for daily plans, weekly progress, and date-based cleanup.
 */
@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    // Retrieve sessions scheduled for a particular date
    List<StudySession> findByScheduledDateOrderByIdAsc(LocalDate scheduledDate);

    // Retrieve sessions between two dates (used for weekly progress analytics)
    List<StudySession> findByScheduledDateBetween(LocalDate startDate, LocalDate endDate);

    // Count sessions by date and status
    long countByScheduledDateAndStatus(LocalDate scheduledDate, SessionStatus status);

    // Delete existing sessions on a date (used when regenerating a schedule to prevent duplicates)
    void deleteByScheduledDate(LocalDate scheduledDate);
}
