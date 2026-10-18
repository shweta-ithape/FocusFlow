package com.focusflow.dto;

/**
 * Breakdown of study time and completed sessions per subject.
 */
public class SubjectProgressDto {

    private String subjectName;
    private int completedMinutes;
    private long completedSessions;

    public SubjectProgressDto() {
    }

    public SubjectProgressDto(String subjectName, int completedMinutes, long completedSessions) {
        this.subjectName = subjectName;
        this.completedMinutes = completedMinutes;
        this.completedSessions = completedSessions;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCompletedMinutes() {
        return completedMinutes;
    }

    public void setCompletedMinutes(int completedMinutes) {
        this.completedMinutes = completedMinutes;
    }

    public long getCompletedSessions() {
        return completedSessions;
    }

    public void setCompletedSessions(long completedSessions) {
        this.completedSessions = completedSessions;
    }
}
