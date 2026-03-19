package com.taskapp.task_service.service;

import com.taskapp.task_service.dto.response.UserResponse;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.exception.UserNotFoundException;
import com.taskapp.task_service.mapper.UserMapper;
import com.taskapp.task_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toResponseDto(user);
    }

}
