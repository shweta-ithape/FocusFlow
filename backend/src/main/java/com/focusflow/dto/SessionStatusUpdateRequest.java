package com.focusflow.dto;

import com.focusflow.entity.SessionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for updating the status and actual study duration of a session.
 */
public class SessionStatusUpdateRequest {

    @NotNull(message = "Session status is required")
    private SessionStatus status;

    @Min(value = 0, message = "Actual minutes cannot be negative")
    private int actualMinutes;

    public SessionStatusUpdateRequest() {
    }

    public SessionStatusUpdateRequest(SessionStatus status, int actualMinutes) {
        this.status = status;
        this.actualMinutes = actualMinutes;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public int getActualMinutes() {
        return actualMinutes;
    }

    public void setActualMinutes(int actualMinutes) {
        this.actualMinutes = actualMinutes;
    }
}
