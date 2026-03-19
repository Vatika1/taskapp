package com.taskapp.auth_service.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskapp.auth_service.dto.JwtResponse;
import com.taskapp.auth_service.dto.LoginRequest;
import com.taskapp.auth_service.dto.RegisterRequest;
import com.taskapp.auth_service.exception.EmailAlreadyExistsException;
import com.taskapp.auth_service.exception.InvalidCredentialsException;
import com.taskapp.auth_service.exception.UsernameAlreadyExistsException;
import com.taskapp.auth_service.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(controllers = AuthController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // to convert your request objects to JSON when sending fake HTTP requests.
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private JwtResponse jwtResponse;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // shared test data
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("vatika");
        registerRequest.setEmail("vatika@gmail.com");
        registerRequest.setPassword("Vatika123!");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("vatika@gmail.com");
        loginRequest.setPassword("Vatika123!");

        jwtResponse = new JwtResponse("mocked.jwt.token", "vatika", "vatika@gmail.com", "ROLE_USER");

    }

    @Test
    void register_shouldCreateUserAndReturnJwtToken() throws Exception  {
        //Step 1 — ARRANGE  → tell mock what to return
        when(authService.register(any()))
                .thenReturn(jwtResponse);

        // AuthControllerTest — fake HTTP request
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isCreated())        // ← check HTTP status 201
                .andExpect(jsonPath("$.token")          // ← check JSON response body
                        .value("mocked.jwt.token"))
                .andExpect(jsonPath("$.username").value("vatika"))
                .andExpect(jsonPath("$.email").value("vatika@gmail.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

    }

    @Test
    void register_EmailAlreadyExists() throws Exception{
        when(authService.register(any()))
                .thenThrow(new EmailAlreadyExistsException("Email already exists"));

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void register_UsernameAlreadyExists() throws Exception{
        when(authService.register(any()))
                .thenThrow(new UsernameAlreadyExistsException("Username already exists"));

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest))
                )
                .andExpect(status().isConflict());
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() throws Exception {
        when(authService.login(any()))
                .thenReturn(jwtResponse);

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isOk())        // ← check HTTP status 201
                .andExpect(jsonPath("$.token")          // ← check JSON response body
                        .value("mocked.jwt.token"))
                .andExpect(jsonPath("$.username").value("vatika"))
                .andExpect(jsonPath("$.email").value("vatika@gmail.com"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));

    }

    @Test
    void login_InvalidCredentials() throws Exception{
        when(authService.login(any()))
                .thenThrow(new InvalidCredentialsException());

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest))
                )
                .andExpect(status().isUnauthorized());
    }
    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() throws Exception {
        when(authService.validateToken(any()))
                .thenReturn(true);

        mockMvc.perform(
                        get("/auth/validate")
                                .param("token", "mocked.jwt.token")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void validateToken_InvalidToken() throws Exception{
        when(authService.validateToken(any()))
                .thenReturn(false);

        mockMvc.perform(
                        get("/auth/validate")
                                .param("token", "mocked.jwt.token")
                )
                .andExpect(status().isUnauthorized()); // ← body should be false;
    }


}
