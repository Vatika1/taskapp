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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        if(userRepository.existsByUsername(request.getUsername())){
            throw new UsernameAlreadyExistsException(request.getUsername());
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail());

        return userMapper.toJwtResponse(user, token);
    }

    public JwtResponse login(LoginRequest request) {

        // Step 1 - Spring Security verifies credentials
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }
        // Step 2 - find user from DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);
        String token = jwtService.generateToken(user.getEmail());

        //return JwtResponse
        return userMapper.toJwtResponse(user, token);
    }

    public Boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }
}
