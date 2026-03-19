package com.taskapp.auth_service.controller;

import com.taskapp.auth_service.dto.JwtResponse;
import com.taskapp.auth_service.dto.LoginRequest;
import com.taskapp.auth_service.dto.RegisterRequest;
import com.taskapp.auth_service.exception.InvalidTokenException;
import com.taskapp.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {

        JwtResponse jwtResponse = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(jwtResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse jwtResponse = authService.login(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(jwtResponse);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam(required = false) String token) {

        boolean isValid = authService.validateToken(token);

        if (!isValid) {
            throw new InvalidTokenException(); // ← throw instead of returning false
        }
        return ResponseEntity.ok(true);
    }

}
