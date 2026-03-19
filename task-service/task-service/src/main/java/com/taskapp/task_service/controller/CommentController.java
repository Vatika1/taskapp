package com.taskapp.task_service.controller;

import com.taskapp.task_service.dto.request.CreateCommentRequest;
import com.taskapp.task_service.dto.request.UpdateCommentRequest;
import com.taskapp.task_service.dto.response.CommentResponse;
import com.taskapp.task_service.entity.User;
import com.taskapp.task_service.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tasks")
public class CommentController {

    private final CommentService commentService;


    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<CommentResponse>> getCommentsByTaskId(@PathVariable Long id,
                                                                     @PageableDefault(size = 10) Pageable pageable){
        Page<CommentResponse> response = commentService.getAllComments(id, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CreateCommentRequest request,
                                                      @AuthenticationPrincipal User user){
        CommentResponse response = commentService.createComment(request, user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @Valid @RequestBody UpdateCommentRequest request,
                                                         @AuthenticationPrincipal User user){
        CommentResponse response = commentService.updateComment(id, request, user.getEmail());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id,
                                              @AuthenticationPrincipal User user){
        commentService.deleteComment(id, user.getEmail());
        return ResponseEntity.noContent().build();
    }




}
