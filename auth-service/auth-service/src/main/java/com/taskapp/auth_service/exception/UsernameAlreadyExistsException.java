package com.taskapp.auth_service.exception;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String username) {
        super("Username already exists: " + username);
    }
}
