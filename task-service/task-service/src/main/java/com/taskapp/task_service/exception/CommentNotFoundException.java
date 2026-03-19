package com.taskapp.task_service.exception;

public class CommentNotFoundException extends ResourceNotFoundException{
    public CommentNotFoundException(Long id) {
        super("Comment not found: " + id);
    }
}
