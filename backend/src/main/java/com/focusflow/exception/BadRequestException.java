package com.focusflow.exception;

/**
 * Custom exception thrown when client input fails business validation
 * (e.g., zero available hours, past exam dates, invalid parameters).
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
