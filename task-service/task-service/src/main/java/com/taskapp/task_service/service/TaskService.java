package com.taskapp.task_service.service;

import com.taskapp.task_service.dto.request.CreateTaskRequest;
import com.taskapp.task_service.dto.request.UpdateTaskRequest;
import com.taskapp.task_service.dto.response.TaskResponse;
import com.taskapp.task_service.entity.Project;
import com.taskapp.task_service.entity.Task;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.exception.ProjectNotFoundException;
import com.taskapp.task_service.exception.TaskNotFoundException;
import com.taskapp.task_service.exception.UnauthorizedAccessException;
import com.taskapp.task_service.exception.UserNotFoundException;
import com.taskapp.task_service.mapper.TaskMapper;
import com.taskapp.task_service.repository.ProjectRepository;
import com.taskapp.task_service.repository.TaskRepository;
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
public class TaskService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    private User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public Page<TaskResponse> getAllTasks(Long id, Pageable pageable){
        Page<Task> tasks = taskRepository.findByProjectId(id,pageable);
        return tasks.map(taskMapper::toResponseDto);
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request, String email) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(request.getProjectId()));
        // Check if current user owns this project
        User user = getCurrentUser(email);
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException(request.getProjectId());
        }
        Task task = taskMapper.toEntity(request);
        task.setProject(project);
        if(request.getAssigneeId() != null){
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new UserNotFoundException(request.getProjectId()));
            task.setAssignee(assignee);
        }
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDto(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request, String email){
        // Check at least one field is provided
        if (request.getTitle() == null &&
                request.getDescription() == null &&
                request.getStatus() == null &&
                request.getAssigneeId() == null) {
            throw new IllegalArgumentException("At least one field must be provided");
        }
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        // Check ownership
        User user = getCurrentUser(email);
        if (!task.getProject().getOwner().getId().equals(user.getId())) {
            throw new UnauthorizedAccessException(id);
        }

        if(request.getStatus() != null){
            task.setStatus(request.getStatus());
        }
        if(request.getTitle() != null){
            task.setTitle(request.getTitle());
        }
        if(request.getDescription() != null){
            task.setDescription(request.getDescription());
        }
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new UserNotFoundException(request.getAssigneeId()));
            task.setAssignee(assignee);
        }
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponseDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id, String email) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        // Check ownership
        User user = getCurrentUser(email);
        if(!task.getProject().getOwner().getId().equals(user.getId()) ){
            throw new UnauthorizedAccessException(id);
        }
        taskRepository.delete(task);
    }
}
