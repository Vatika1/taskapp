package com.taskapp.task_service.mapper;

import com.taskapp.task_service.dto.response.UserResponse;
import com.taskapp.task_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponseDto(User user){
        return UserResponse.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .email(user.getEmail())
                .role(user.getRole())
                .username(user.getDisplayUsername())
                .build();
    }
}
