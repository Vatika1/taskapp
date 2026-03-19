package com.taskapp.task_service.service;

import com.taskapp.task_service.dto.request.CreateCommentRequest;
import com.taskapp.task_service.dto.request.CreateTaskRequest;
import com.taskapp.task_service.dto.request.UpdateCommentRequest;
import com.taskapp.task_service.dto.request.UpdateTaskRequest;
import com.taskapp.task_service.dto.response.CommentResponse;
import com.taskapp.task_service.dto.response.TaskResponse;
import com.taskapp.task_service.entity.Comment;
import com.taskapp.task_service.entity.Project;
import com.taskapp.task_service.entity.Task;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.exception.*;
import com.taskapp.task_service.mapper.CommentMapper;
import com.taskapp.task_service.repository.CommentRepository;
import com.taskapp.task_service.repository.TaskRepository;
import com.taskapp.task_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;

    private User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public Page<CommentResponse> getAllComments(Long id, Pageable pageable){

        Page<Comment> comments = commentRepository.findByTaskId(id, pageable);
        return comments.map(commentMapper::toResponseDto);
    }

    @Transactional
    public CommentResponse createComment(CreateCommentRequest request, String email){
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new TaskNotFoundException(request.getTaskId()));

        User user = getCurrentUser(email);
        Comment comment = commentMapper.toEntity(request);
        comment.setTask(task);
        comment.setAuthor(user);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponseDto(savedComment);
    }

    @Transactional
    public void deleteComment(Long id, String email) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        User author = getCurrentUser(email);
        if(!comment.getAuthor().getId().equals(author.getId())){
            throw new UnauthorizedAccessException(id);
        }
        commentRepository.delete(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long id, UpdateCommentRequest request, String email){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        // Check ownership
        User user = getCurrentUser(email);
        if(!comment.getAuthor().getId().equals(user.getId())){
            throw new UnauthorizedAccessException(id);
        }
        comment.setContent(request.getContent());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toResponseDto(savedComment);
    }

}
