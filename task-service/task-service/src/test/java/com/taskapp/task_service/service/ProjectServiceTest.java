package com.taskapp.task_service.service;

import com.taskapp.task_service.dto.request.CreateProjectRequest;
import com.taskapp.task_service.dto.response.ProjectResponse;
import com.taskapp.task_service.entity.Project;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.exception.ProjectNotFoundException;
import com.taskapp.task_service.exception.UnauthorizedAccessException;
import com.taskapp.task_service.exception.UserNotFoundException;
import com.taskapp.task_service.mapper.ProjectMapper;
import com.taskapp.task_service.repository.ProjectRepository;
import com.taskapp.task_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProjectMapper projectMapper;
    @InjectMocks
    private ProjectService projectService;

    private CreateProjectRequest createProjectRequest;

    private User user;
    private Project project;

    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp(){
        createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("Project 1");
        createProjectRequest.setDescription("On-premise to AWS");

        user = new User();
        user.setId(1L);
        user.setEmail("vatika@gmail.com");

        project = new Project();
        project.setId(1L);
        project.setName("Project 1");
        project.setDescription("On-premise to AWS");
        project.setOwner(user);

        projectResponse = new ProjectResponse(1L, "Project 1","On-premise to AWS", 1L,
                "vatika", LocalDateTime.now() );
    }

    @Test
    void createProject_ShouldReturnProject_WhenValidRequest(){
        when(userRepository.findByEmail("vatika@gmail.com"))
                .thenReturn(Optional.of(user));
        when(projectMapper.toEntity(any(CreateProjectRequest .class)))
                .thenReturn(project);
        when(projectMapper.toResponseDto(any(Project.class)))
                .thenReturn(projectResponse);
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        var response = projectService.createProject(createProjectRequest, user.getEmail());

        assertNotNull(response);
        assertEquals(projectResponse.getCreatedAt(), response.getCreatedAt());
        assertEquals(projectResponse.getId(), response.getId());
        assertEquals(projectResponse.getDescription(), response.getDescription());
        assertEquals(projectResponse.getName(), response.getName());
        assertEquals(projectResponse.getOwnerId(), response.getOwnerId());
        assertEquals(projectResponse.getOwnerUsername(), response.getOwnerUsername());

        verify(userRepository, times(1)).findByEmail("vatika@gmail.com");
        verify(projectMapper, times(1)).toEntity(createProjectRequest);
        verify(projectMapper, times(1)).toResponseDto(project);
        verify(projectRepository, times(1)).save(project);
    }

    @Test
    void createProject_ShouldThrowException_WhenUserNotFound(){
        when(userRepository.findByEmail("vatika@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            projectService.createProject(createProjectRequest, user.getEmail());
        });

        verify(userRepository, times(1)).findByEmail("vatika@gmail.com");
    }

    @Test
    void getProjectById_ShouldReturnProject_WhenValidRequest(){
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));
        when(projectMapper.toResponseDto(any(Project.class)))
                .thenReturn(projectResponse);

        var response = projectService.getProjectById(project.getId(), user.getEmail());

        assertNotNull(response);
        assertEquals(projectResponse.getCreatedAt(), response.getCreatedAt());
        assertEquals(projectResponse.getId(), response.getId());
        assertEquals(projectResponse.getDescription(), response.getDescription());
        assertEquals(projectResponse.getName(), response.getName());
        assertEquals(projectResponse.getOwnerId(), response.getOwnerId());
        assertEquals(projectResponse.getOwnerUsername(), response.getOwnerUsername());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectMapper, times(1)).toResponseDto(project);

    }
    @Test
    void getProjectById_ShouldThrowException_WhenProjectNotFound(){
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> {
            projectService.getProjectById(project.getId(), user.getEmail());
        });

        verify(projectRepository, times(1)).findById(project.getId());
    }
    @Test
    void getProjectById_ShouldThrowException_WhenUnauthorizedUser(){
        // create a different user
        User otherUser = new User();
        otherUser.setId(2L);  // ← different id from current user (1L)
        // make project owned by otherUser
        project.setOwner(otherUser);
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        assertThrows(UnauthorizedAccessException.class, () -> {
            projectService.getProjectById(project.getId(), user.getEmail());
        });

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(projectRepository, times(1)).findById(project.getId());

    }
    @Test
    void getAllProjects_ShouldReturnProjects_WhenValidRequest(){
        Page<Project> projectPage = new PageImpl<>(List.of(project));

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(projectRepository.findByOwnerId(any(), any(Pageable.class)))
                .thenReturn(projectPage);
        when(projectMapper.toResponseDto(any(Project.class)))
                .thenReturn(projectResponse);

        var response = projectService.getAllProjectsPage(user.getEmail(), Pageable.unpaged());

        assertNotNull(response);
        assertEquals(1, response.getTotalElements()); // ← 1 project in page
        assertEquals(projectResponse.getName(), response.getContent().get(0).getName());

        verify(projectRepository, times(1)).findByOwnerId(project.getId(), Pageable.unpaged());
        verify(projectMapper, times(1)).toResponseDto(project);
    }
    @Test
    void deleteProject_ShouldDeleteProject_WhenValidRequest(){
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        projectService.deleteProject(project.getId(), user.getEmail());

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectRepository, times(1)).delete(project);
    }
    @Test
    void deleteProject_ShouldThrowException_WhenProjectNotFound(){
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> {
            projectService.deleteProject(project.getId(), user.getEmail());
        });

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(projectRepository, times(1)).findById(project.getId());
    }
    @Test
    void deleteProject_ShouldThrowException_WhenUnauthorizedUser(){
        User otherUser = new User();
        otherUser.setId(2L);
        project.setOwner(otherUser);

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));
        when(projectRepository.findById(project.getId()))
                .thenReturn(Optional.of(project));

        assertThrows(UnauthorizedAccessException.class, () -> {
            projectService.deleteProject(project.getId(), user.getEmail());
        });

        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(projectRepository, times(1)).findById(project.getId());
        verify(projectRepository, never()).delete(any());
    }
}
