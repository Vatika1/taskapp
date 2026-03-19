package com.taskapp.task_service.mapper;

import com.taskapp.task_service.dto.request.CreateCommentRequest;
import com.taskapp.task_service.dto.response.CommentResponse;
import com.taskapp.task_service.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public CommentResponse toResponseDto(Comment comment){
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .taskId(comment.getTask().getId())
                .authorId(comment.getAuthor().getId())
                .authorUsername(comment.getAuthor().getUsername())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public Comment toEntity(CreateCommentRequest request){

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        return comment;
    }

}
