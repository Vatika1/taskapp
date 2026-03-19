package com.taskapp.task_service.service;

import com.taskapp.task_service.dto.request.CreateProjectRequest;
import com.taskapp.task_service.dto.response.ProjectResponse;
import com.taskapp.task_service.entity.Project;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.exception.ProjectNotFoundException;
import com.taskapp.task_service.exception.UnauthorizedAccessException;
import com.taskapp.task_service.exception.UserNotFoundException;
import com.taskapp.task_service.mapper.ProjectMapper;
import com.taskapp.task_service.repository.ProjectRepository;
import com.taskapp.task_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    // Get current logged in user helper method
    private User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, String email) {
        User user = getCurrentUser(email);
        Project project = projectMapper.toEntity(request);
        project.setOwner(user);
        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponseDto(savedProject);
    }

    public Page<ProjectResponse> getAllProjectsPage(String email, Pageable pageable) {
        // hint: getCurrentUser() → findByOwnerId() → map to response
        User user = getCurrentUser(email);
        Page<Project> projects = projectRepository.findByOwnerId(user.getId(), pageable);

        return projects.map(projectMapper::toResponseDto);
    }

    public ProjectResponse getProjectById(Long id, String email) {
        // hint: findById() → check owner → return response
        User user = getCurrentUser(email);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        if(!project.getOwner().getId().equals(user.getId())){
            throw new UnauthorizedAccessException(id);
        }
        return projectMapper.toResponseDto(project);
    }

    @Transactional
    public void deleteProject(Long id, String email) {
        User user = getCurrentUser(email);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        if(!project.getOwner().getId().equals(user.getId())){
            throw new UnauthorizedAccessException(id);
        }
        projectRepository.delete(project);
    }
}