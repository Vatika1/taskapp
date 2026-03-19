package com.taskapp.task_service.exception;

public class ProjectNotFoundException extends ResourceNotFoundException {

    public ProjectNotFoundException(Long id) {
        super("Project not found: " + id);
    }

    public ProjectNotFoundException(String id) {
        super("Project not found: " + id);
    }
}
