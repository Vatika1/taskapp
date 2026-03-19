package com.taskapp.task_service.repository;

import com.taskapp.task_service.entity.Task;
import com.taskapp.task_service.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByAssigneeId(Long assigneeId);

    // Get tasks assigned to user that aren't done -TODO and IN PROGRESS
    List<Task> findByAssigneeIdAndStatusNot(Long assigneeId, TaskStatus status);

}
