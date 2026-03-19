package com.taskapp.auth_service.exception;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException() {
        super("Invalid Token");
    }
}
