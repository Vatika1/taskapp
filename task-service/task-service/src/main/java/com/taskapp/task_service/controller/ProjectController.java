package com.taskapp.task_service.controller;

import com.taskapp.task_service.dto.request.CreateProjectRequest;
import com.taskapp.task_service.dto.response.ProjectResponse;
import com.taskapp.task_service.dto.response.TaskResponse;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.service.ProjectService;
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
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<Page<ProjectResponse>> getAllProjects(@AuthenticationPrincipal User user,
                                                                @PageableDefault(size = 10) Pageable pageable){
        Page<ProjectResponse> response = projectService.getAllProjectsPage(user.getEmail(), pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<Page<TaskResponse>>getTasksByProject(@PathVariable Long id,
                                                                 @PageableDefault(size = 10) Pageable pageable){
        Page<TaskResponse> response = taskService.getAllTasks(id, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request,
                                                         @AuthenticationPrincipal User user){
        ProjectResponse projectResponse = projectService.createProject(request, user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id, @AuthenticationPrincipal User user){
        ProjectResponse projectResponse = projectService.getProjectById(id, user.getEmail());
        return ResponseEntity.ok(projectResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id,
                                              @AuthenticationPrincipal User user){
        String email = user.getEmail();
        projectService.deleteProject(id,email);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
