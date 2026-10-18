package com.focusflow.service;

import com.focusflow.entity.Subject;
import com.focusflow.exception.ResourceNotFoundException;
import com.focusflow.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class handling business logic for Subject management.
 */
@Service
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with id: " + id));
    }

    public Subject createSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Long id, Subject updatedSubject) {
        Subject existingSubject = getSubjectById(id);
        existingSubject.setName(updatedSubject.getName());
        existingSubject.setExamDate(updatedSubject.getExamDate());
        existingSubject.setImportance(updatedSubject.getImportance());
        return subjectRepository.save(existingSubject);
    }

    public void deleteSubject(Long id) {
        Subject existingSubject = getSubjectById(id);
        subjectRepository.delete(existingSubject);
    }
}
