package com.taskapp.task_service.dto.request;

import com.taskapp.task_service.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private Long assigneeId;
}
