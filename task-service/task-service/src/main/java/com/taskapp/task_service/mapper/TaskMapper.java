package com.taskapp.task_service.mapper;

import com.taskapp.task_service.dto.request.CreateTaskRequest;
import com.taskapp.task_service.dto.response.TaskResponse;
import com.taskapp.task_service.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public TaskResponse toResponseDto(Task task){
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .assigneeUsername(task.getAssignee() != null ? task.getAssignee().getUsername() : null)
                .createdAt(task.getCreatedAt())
                .description(task.getDescription())
                .status(task.getStatus())
                .projectId(task.getProject().getId())
                .updatedAt(task.getUpdatedAt())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .build();
    }

    public Task toEntity(CreateTaskRequest request){

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        return task;
    }
}
