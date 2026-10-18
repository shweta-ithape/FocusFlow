package com.focusflow.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Summary analytics for weekly study activity.
 */
public class WeeklyProgressDto {

    private int totalCompletedMinutes;
    private long totalCompletedSessions;
    private int progressPercentage;
    private List<SubjectProgressDto> subjectSummaries = new ArrayList<>();

    public WeeklyProgressDto() {
    }

    public WeeklyProgressDto(int totalCompletedMinutes, long totalCompletedSessions, int progressPercentage, List<SubjectProgressDto> subjectSummaries) {
        this.totalCompletedMinutes = totalCompletedMinutes;
        this.totalCompletedSessions = totalCompletedSessions;
        this.progressPercentage = progressPercentage;
        this.subjectSummaries = subjectSummaries;
    }

    public int getTotalCompletedMinutes() {
        return totalCompletedMinutes;
    }

    public void setTotalCompletedMinutes(int totalCompletedMinutes) {
        this.totalCompletedMinutes = totalCompletedMinutes;
    }

    public long getTotalCompletedSessions() {
        return totalCompletedSessions;
    }

    public void setTotalCompletedSessions(long totalCompletedSessions) {
        this.totalCompletedSessions = totalCompletedSessions;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public List<SubjectProgressDto> getSubjectSummaries() {
        return subjectSummaries;
    }

    public void setSubjectSummaries(List<SubjectProgressDto> subjectSummaries) {
        this.subjectSummaries = subjectSummaries;
    }
}
