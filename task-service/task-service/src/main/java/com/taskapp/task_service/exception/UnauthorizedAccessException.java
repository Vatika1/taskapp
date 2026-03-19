package com.taskapp.task_service.exception;

public class UnauthorizedAccessException extends RuntimeException{

    public UnauthorizedAccessException(Long id) {
        super("Unauthorized action: " + id);
    }
}
