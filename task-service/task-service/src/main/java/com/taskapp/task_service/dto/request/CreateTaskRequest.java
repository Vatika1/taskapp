package com.taskapp.task_service.dto.request;

import com.taskapp.task_service.enums.TaskStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "Task title is required")
    private String title;

    @NotBlank(message = "Task description is required")
    private String description;

    private TaskStatus status;

    @NotNull(message = "Project id is required")
    private Long projectId;

    private Long assigneeId;
}
