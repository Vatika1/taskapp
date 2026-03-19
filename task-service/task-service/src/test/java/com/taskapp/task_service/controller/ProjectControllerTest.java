package com.taskapp.task_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskapp.task_service.dto.request.CreateProjectRequest;
import com.taskapp.task_service.dto.response.ProjectResponse;
import com.taskapp.task_service.dto.response.TaskResponse;
import com.taskapp.task_service.entity.Project;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.enums.TaskStatus;
import com.taskapp.task_service.exception.ProjectNotFoundException;
import com.taskapp.task_service.exception.UnauthorizedAccessException;
import com.taskapp.task_service.exception.UserNotFoundException;
import com.taskapp.task_service.service.JwtService;
import com.taskapp.task_service.service.ProjectService;
import com.taskapp.task_service.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // to convert your request objects to JSON when sending fake HTTP requests.
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtService jwtService;

    private ProjectResponse projectResponse;
    private TaskResponse taskResponse;
    private CreateProjectRequest createProjectRequest;

    private User user;
    private Project project;

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

        projectResponse = new ProjectResponse(1L, "Project 1",
                "On-premise to AWS",1L, "vatika", LocalDateTime.now());

        taskResponse = new TaskResponse(1L, "Task 1", "Task description 1", TaskStatus.TODO,
                1L, 1L, "vatika", LocalDateTime.now(), LocalDateTime.now());
    }
    private RequestPostProcessor mockUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("vatika@gmail.com");
        user.setUsername("vatika");
        user.setRole("ROLE_USER");
        return SecurityMockMvcRequestPostProcessors.user(user);
    }

    @Test
    void createProject_ShouldReturnProject_WhenCreated() throws Exception {
        when(projectService.createProject(any(), any()))
                .thenReturn(projectResponse);

        mockMvc.perform(
                post("/api/projects")
                        .with(mockUser())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProjectRequest))
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Project 1"))
                .andExpect(jsonPath("$.description").value("On-premise to AWS"))
                .andExpect(jsonPath("$.ownerId").value(1L))
                .andExpect(jsonPath("$.ownerUsername").value("vatika"));
    }
    @Test
    void createProject_ShouldReturn404_WhenUserNotFound() throws Exception {
        when(projectService.createProject(any(), any()))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(
                post("/api/projects")
                        .with(mockUser())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createProjectRequest))
                )
                .andExpect(status().isNotFound());
    }
    @Test
    void getAllProjects_ShouldReturn200_WithProjects() throws Exception {
        Page<ProjectResponse> responsePage = new PageImpl<>(List.of(projectResponse));
        when(projectService.getAllProjectsPage(any(), any()))
                .thenReturn(responsePage);
        mockMvc.perform(
                get("/api/projects")
                        .with(mockUser())
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Project 1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
    @Test
    void getProjectById_ShouldReturn200_WhenValidRequest() throws Exception {
        when(projectService.getProjectById(any(), any()))
                .thenReturn(projectResponse);
        mockMvc.perform(
                get("/api/projects/1")
                        .with(mockUser())
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Project 1"))
                .andExpect(jsonPath("$.description").value("On-premise to AWS"))
                .andExpect(jsonPath("$.ownerId").value(1L))
                .andExpect(jsonPath("$.ownerUsername").value("vatika"));
    }
    @Test
    void getProjectById_ShouldReturn404_WhenProjectNotFound() throws Exception {
        when(projectService.getProjectById(any(), any()))
                .thenThrow(new ProjectNotFoundException("User not found" + user.getId()));

        mockMvc.perform(
                get("/api/projects/1")
                        .with(mockUser())
                        .with(csrf())
        )
                .andExpect(status().isNotFound());

    }
    @Test
    void getProjectById_ShouldReturn403_WhenUnauthorizedUser() throws Exception {
        when(projectService.getProjectById(any(), any()))
                .thenThrow(new UnauthorizedAccessException(user.getId()));

        mockMvc.perform(
                        get("/api/projects/1")
                                .with(mockUser())
                                .with(csrf())
                )
                .andExpect(status().isForbidden());
    }
    @Test
    void deleteProject_ShouldReturn204_WhenValidRequest() throws Exception {
        doNothing().when(projectService)
                .deleteProject(any(), any());
        mockMvc.perform(
                delete("/api/projects/1")
                        .with(mockUser())
                        .with(csrf())
        )
                .andExpect(status().isNoContent());
    }
    @Test
    void deleteProject_ShouldReturn404_WhenProjectNotFound() throws Exception {
        doThrow(new ProjectNotFoundException("Project not found"))
                .when(projectService)
                .deleteProject(any(), any());

        mockMvc.perform(
                        delete("/api/projects/1")
                                .with(mockUser())
                                .with(csrf())
                )
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteProject_ShouldReturn403_WhenUnauthorizedUser() throws Exception {
        doThrow(new UnauthorizedAccessException(user.getId()))
                .when(projectService)
                .deleteProject(any(), any());

        mockMvc.perform(
                        delete("/api/projects/1")
                                .with(mockUser())
                                .with(csrf())
                )
                .andExpect(status().isForbidden());
    }
    @Test
    void getTasksByProject_ShouldReturn200_WithTasks() throws Exception {
        Page<TaskResponse> responsePage = new PageImpl<>(List.of(taskResponse));
        when(taskService.getAllTasks(any(), any()))
                .thenReturn(responsePage);

        mockMvc.perform(
                        get("/api/projects/1/tasks")
                                .with(mockUser())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Task 1"))
                .andExpect(jsonPath("$.totalElements").value(1));

    }

}
