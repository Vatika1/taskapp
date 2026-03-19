package com.taskapp.task_service.repository;

import com.taskapp.task_service.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {


    List<Project> findByOwnerId(Long ownerId);

    Page<Project> findByOwnerId(Long ownerId, Pageable pageable);

}
