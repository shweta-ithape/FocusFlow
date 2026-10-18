package com.focusflow.dto;

/**
 * Top metrics payload for the FocusFlow home dashboard.
 */
public class DashboardStatsDto {

    private int plannedMinutesToday;
    private long totalSessionsToday;
    private long completedSessionsToday;
    private long pendingSessionsToday;
    private int overallProgressPercentage;

    public DashboardStatsDto() {
    }

    public DashboardStatsDto(int plannedMinutesToday, long totalSessionsToday, long completedSessionsToday, long pendingSessionsToday, int overallProgressPercentage) {
        this.plannedMinutesToday = plannedMinutesToday;
        this.totalSessionsToday = totalSessionsToday;
        this.completedSessionsToday = completedSessionsToday;
        this.pendingSessionsToday = pendingSessionsToday;
        this.overallProgressPercentage = overallProgressPercentage;
    }

    public int getPlannedMinutesToday() {
        return plannedMinutesToday;
    }

    public void setPlannedMinutesToday(int plannedMinutesToday) {
        this.plannedMinutesToday = plannedMinutesToday;
    }

    public long getTotalSessionsToday() {
        return totalSessionsToday;
    }

    public void setTotalSessionsToday(long totalSessionsToday) {
        this.totalSessionsToday = totalSessionsToday;
    }

    public long getCompletedSessionsToday() {
        return completedSessionsToday;
    }

    public void setCompletedSessionsToday(long completedSessionsToday) {
        this.completedSessionsToday = completedSessionsToday;
    }

    public long getPendingSessionsToday() {
        return pendingSessionsToday;
    }

    public void setPendingSessionsToday(long pendingSessionsToday) {
        this.pendingSessionsToday = pendingSessionsToday;
    }

    public int getOverallProgressPercentage() {
        return overallProgressPercentage;
    }

    public void setOverallProgressPercentage(int overallProgressPercentage) {
        this.overallProgressPercentage = overallProgressPercentage;
    }
}
