package com.focusflow.repository;

import com.focusflow.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Topic entity.
 * Provides custom query methods for filtering by completion and subject.
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    // Find all topics belonging to a specific subject
    List<Topic> findBySubjectId(Long subjectId);

    // Find all incomplete topics (used by the scheduling engine)
    List<Topic> findByCompletedFalse();

    // Count topics by completion status
    long countByCompletedTrue();
    long countByCompletedFalse();
}
