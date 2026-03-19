package com.taskapp.task_service.controller;

import com.taskapp.task_service.dto.request.CreateTaskRequest;
import com.taskapp.task_service.dto.request.UpdateTaskRequest;
import com.taskapp.task_service.dto.response.TaskResponse;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.exception.UserNotFoundException;
import com.taskapp.task_service.repository.UserRepository;
import com.taskapp.task_service.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse>createTask(@Valid @RequestBody CreateTaskRequest request,
                                                  @AuthenticationPrincipal User user){
        TaskResponse response = taskService.createTask(request, user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse>updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request,
                                                      @AuthenticationPrincipal User user){
        TaskResponse response = taskService.updateTask(id, request, user.getEmail());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
                                              @AuthenticationPrincipal User user){
        String email = user.getEmail();
        taskService.deleteTask(id, email);
        return ResponseEntity.noContent().build();
    }
}
