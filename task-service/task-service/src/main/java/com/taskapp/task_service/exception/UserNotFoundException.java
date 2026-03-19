package com.taskapp.task_service.exception;

public class UserNotFoundException extends ResourceNotFoundException{

    public UserNotFoundException(Long id) {
        super("User not found: " + id);
    }

    public UserNotFoundException(String email) {
        super("User not found: " + email);
    }
}
