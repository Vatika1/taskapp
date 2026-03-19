package com.taskapp.auth_service.repository;

import com.taskapp.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Needed for login — find user by their email
    Optional<User> findByEmail(String email);

    // Needed for register — check if email already taken
    Boolean existsByEmail(String email);

    // Needed for register — check if username already taken
    Boolean existsByUsername(String username);

    // Needed by JWT filter — Spring Security loads user by username
    Optional<User> findByUsername(String username);

}
