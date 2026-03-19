package com.taskapp.auth_service.service;

import com.taskapp.auth_service.Mapper.UserMapper;
import com.taskapp.auth_service.dto.JwtResponse;
import com.taskapp.auth_service.dto.LoginRequest;
import com.taskapp.auth_service.dto.RegisterRequest;
import com.taskapp.auth_service.entity.User;
import com.taskapp.auth_service.exception.EmailAlreadyExistsException;
import com.taskapp.auth_service.exception.InvalidCredentialsException;
import com.taskapp.auth_service.exception.UsernameAlreadyExistsException;
import com.taskapp.auth_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;  // ← fake, no real DB calls
    @Mock
    private JwtService jwtService;          // ← fake, no real JWT generation
    @Mock
    private PasswordEncoder passwordEncoder; // ← fake, no real bcrypt
    @InjectMocks
    private AuthService authService;        // ← REAL class, mocks injected into it
    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    public AuthServiceTest() {
    }

    @BeforeEach
    void setUp(){

        // shared test data
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("vatika");
        registerRequest.setEmail("vatika@gmail.com");
        registerRequest.setPassword("Vatika123!");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("vatika@gmail.com");
        loginRequest.setPassword("Vatika123!");

        user = new User();
        user.setEmail("vatika@gmail.com");
        user.setPassword("hashedPassword");
        user.setUsername("vatika");
        user.setRole("ROLE_USER");
    }

    @Test
    void register_ShouldReturnToken_WhenValidRequest() {

        //set up fakes
        when(userRepository.existsByEmail("vatika@gmail.com")).thenReturn(false);
        when(passwordEncoder.encode("Vatika123!")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken("vatika@gmail.com")).thenReturn("mocked.jwt.token");
        when(userMapper.toJwtResponse(any(User.class), eq("mocked.jwt.token")))
                .thenReturn(new JwtResponse("mocked.jwt.token", "vatika", "vatika@gmail.com", "ROLE_USER"));

        // ── ACT ── call the real method you're testing ──
        var response = authService.register(registerRequest);

        // ── ASSERT ── verify the result is what you expect ──
        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("vatika", response.getUsername());
        assertEquals("vatika@gmail.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());

        // ── VERIFY ── check that the right methods were called ──
        verify(userRepository, times(1)).existsByEmail("vatika@gmail.com");
        verify(passwordEncoder, times(1)).encode("Vatika123!");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken("vatika@gmail.com");
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists(){
        when(userRepository.existsByEmail("vatika@gmail.com")).thenReturn(true);

        // Assert exception
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(registerRequest);
        });
        verify(userRepository, never()).save(any());
        verify(userRepository, times(1)).existsByEmail("vatika@gmail.com");
    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists(){
        when(userRepository.existsByEmail("vatika@gmail.com")).thenReturn(false);
        when(userRepository.existsByUsername("vatika")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> {
            authService.register(registerRequest);
        });
        verify(userRepository, never()).save(any());
        verify(userRepository, times(1)).existsByUsername("vatika");
        verify(userRepository, times(1)).existsByEmail("vatika@gmail.com");
    }

    @Test
    void login_ShouldReturnToken_WhenValidCredentials() {
        when(userRepository.findByEmail("vatika@gmail.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("vatika@gmail.com")).thenReturn("mocked.jwt.token");
        when(userMapper.toJwtResponse(any(User.class), eq("mocked.jwt.token")))
                .thenReturn(new JwtResponse("mocked.jwt.token", "vatika", "vatika@gmail.com", "ROLE_USER"));
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(null); // authenticate() succeeds without exception

        var response = authService.login(loginRequest);

        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("vatika", response.getUsername());
        assertEquals("vatika@gmail.com", response.getEmail());
        assertEquals("ROLE_USER", response.getRole());

        verify(userRepository, times(1)).findByEmail("vatika@gmail.com");
        verify(jwtService, times(1)).generateToken("vatika@gmail.com");
        verify(authenticationManager).authenticate(any(Authentication.class));
    }

    @Test
    void login_ShouldThrowException_InvalidCredentials(){
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        when(jwtService.validateToken("some.token")).thenReturn(true);
        var validateToken = authService.validateToken("some.token");

        assertTrue(validateToken);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        when(jwtService.validateToken("some.token")).thenReturn(false);
        var validateToken = authService.validateToken("some.token");
        assertFalse(validateToken);
    }
}
