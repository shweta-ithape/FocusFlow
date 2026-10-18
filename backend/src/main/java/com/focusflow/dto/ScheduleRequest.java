package com.focusflow.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request payload for generating a daily study schedule.
 */
public class ScheduleRequest {

    @NotNull(message = "Schedule date is required")
    private LocalDate date;

    @DecimalMin(value = "0.25", message = "Available study time must be at least 15 minutes (0.25 hours)")
    private double availableHours;

    public ScheduleRequest() {
    }

    public ScheduleRequest(LocalDate date, double availableHours) {
        this.date = date;
        this.availableHours = availableHours;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAvailableHours() {
        return availableHours;
    }

    public void setAvailableHours(double availableHours) {
        this.availableHours = availableHours;
    }
}
