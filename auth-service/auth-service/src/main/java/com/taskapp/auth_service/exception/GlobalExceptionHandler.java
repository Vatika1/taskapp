package com.taskapp.auth_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex,
                                                            HttpServletRequest request){

        ErrorResponse error = new ErrorResponse(
                409,
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex,
                                                                  HttpServletRequest request){

        ErrorResponse error = new ErrorResponse(
                409,
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex,
                                                            HttpServletRequest request){

        ErrorResponse error = new ErrorResponse(
                401,
                "UNAUTHORIZED",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex,
                                                                  HttpServletRequest request){

        ErrorResponse error = new ErrorResponse(
                404,
                "NOT FOUND",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
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

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex,
                                                            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                401,
                "UNAUTHORIZED",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)  // 401
                .body(error);
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

}
