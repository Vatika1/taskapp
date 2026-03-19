package com.taskapp.task_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "Comment is required")
    private String content;

    @NotNull(message = "Task id is required")
    private Long taskId;

}
