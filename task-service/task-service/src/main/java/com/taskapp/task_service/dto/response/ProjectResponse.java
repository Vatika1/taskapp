package com.taskapp.task_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private String ownerUsername;
    private LocalDateTime createdAt;

}
