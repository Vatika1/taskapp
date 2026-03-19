package com.taskapp.auth_service.Mapper;

import com.taskapp.auth_service.dto.JwtResponse;
import com.taskapp.auth_service.dto.RegisterRequest;
import com.taskapp.auth_service.entity.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {


    public User toEntity(RegisterRequest dto){

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());

        return user;
    }

    public JwtResponse toJwtResponse(User user, String token) {

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setEmail(user.getEmail());
        jwtResponse.setRole(user.getRole());
        jwtResponse.setUsername(user.getUsername());
        jwtResponse.setToken(token);

        return jwtResponse;
    }

}
