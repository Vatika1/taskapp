package com.taskapp.task_service.controller;

import com.taskapp.task_service.dto.response.UserResponse;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.mapper.UserMapper;
import com.taskapp.task_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id){

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
}
