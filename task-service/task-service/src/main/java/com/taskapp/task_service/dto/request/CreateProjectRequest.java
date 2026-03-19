package com.taskapp.task_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProjectRequest {

    private String name;

    @NotBlank(message = "Project description is required")
    private String description;
}
