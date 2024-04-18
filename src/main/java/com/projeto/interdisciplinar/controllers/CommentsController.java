package com.projeto.interdisciplinar.controllers;

import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.interdisciplinar.dtos.comments.CreateCommentDTO;
import com.projeto.interdisciplinar.models.CommentsModel;
import com.projeto.interdisciplinar.models.SubCommentsModel;
import com.projeto.interdisciplinar.services.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("comments")
public class CommentsController {

    private CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/comment/{postId}")
    public ResponseEntity<CommentsModel> register(@PathVariable UUID postId,
            @RequestBody @Valid CreateCommentDTO createCommentDTO)
            throws BadRequestException {
        return ResponseEntity.ok().body(this.commentService.create(createCommentDTO, postId));
    }

    @PostMapping("/sub_comment/{commentId}")
    public ResponseEntity<SubCommentsModel> registerSubComment(@PathVariable UUID commentId,
            @RequestBody @Valid CreateCommentDTO createCommentDTO)
            throws BadRequestException {
        return ResponseEntity.ok().body(this.commentService.createSubComment(createCommentDTO, commentId));
    }

    @DeleteMapping("/sub_comment/{commentId}")
    public ResponseEntity<SubCommentsModel> deleteSubComment(@PathVariable UUID commentId)
            throws BadRequestException {
        return ResponseEntity.ok().body(this.commentService.removeSubComment(commentId));
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<CommentsModel> deleteComment(@PathVariable UUID commentId)
            throws BadRequestException {
        return ResponseEntity.ok().body(this.commentService.removeComment(commentId));
    }
}
