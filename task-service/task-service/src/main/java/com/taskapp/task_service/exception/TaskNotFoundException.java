package com.taskapp.task_service.exception;

public class TaskNotFoundException extends ResourceNotFoundException{
    public TaskNotFoundException(Long id) {
        super("Task not found: " + id);
    }
}
