package com.focusflow.exception;

/**
 * Custom exception thrown when a requested resource (Subject, Topic, Session)
 * is not found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
