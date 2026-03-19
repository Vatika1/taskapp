package com.taskapp.task_service.mapper;

import com.taskapp.task_service.dto.request.CreateProjectRequest;
import com.taskapp.task_service.dto.response.ProjectResponse;
import com.taskapp.task_service.entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponseDto(Project project){
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .ownerId(project.getOwner().getId())
                .ownerUsername(project.getOwner().getUsername())
                .createdAt(project.getCreatedAt())
                .build();
    }

    public Project toEntity(CreateProjectRequest request){

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());

        return project;
    }
}
