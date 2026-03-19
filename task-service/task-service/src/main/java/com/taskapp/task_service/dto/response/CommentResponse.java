package com.taskapp.task_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private Long taskId;
    private Long authorId;
    private String authorUsername;
    private LocalDateTime createdAt;
}
