package com.focusflow.repository;

import com.focusflow.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Subject entity.
 * Provides standard CRUD operations through Spring Data JPA.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
