package com.taskapp.task_service.dto.response;

import com.taskapp.task_service.enums.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Long projectId;
    private Long assigneeId;
    private String assigneeUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
