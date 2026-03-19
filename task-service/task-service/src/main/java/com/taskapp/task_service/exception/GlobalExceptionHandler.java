package com.taskapp.task_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        // Determine specific error title based on exception type
        String errorTitle;
        if (ex instanceof UserNotFoundException) {
            errorTitle = "USER NOT FOUND";
        } else if (ex instanceof ProjectNotFoundException) {
            errorTitle = "PROJECT NOT FOUND";
        } else if (ex instanceof TaskNotFoundException) {
            errorTitle = "TASK NOT FOUND";
        } else if (ex instanceof CommentNotFoundException) {
            errorTitle = "COMMENT NOT FOUND";
        } else {
            errorTitle = "NOT FOUND";
        }

        ErrorResponse error = new ErrorResponse(
                404,
                errorTitle,
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedAccessException ex,
                                                               HttpServletRequest request){

        ErrorResponse error = new ErrorResponse(
                403,
                "Forbidden",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                403,
                "FORBIDDEN",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                500,
                "INTERNAL SERVER ERROR",
                "Something went wrong",  // never expose real error to frontend
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // Extract first validation error message
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        ErrorResponse error = new ErrorResponse(
                400,
                "BAD REQUEST",
                message,
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                400,
                "BAD REQUEST",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
