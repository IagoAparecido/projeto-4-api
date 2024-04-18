package com.projeto.interdisciplinar.controllers;

import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.interdisciplinar.dtos.posts.CreatePostDTO;
import com.projeto.interdisciplinar.models.PostsModel;
import com.projeto.interdisciplinar.services.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("posts")
public class PostsController {

    private PostService postService;

    public PostsController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    public ResponseEntity<List<PostsModel>> getAllPosts() {
        return ResponseEntity.ok().body(this.postService.getAllPosts());
    }

    @GetMapping("/post/{userId}")
    public ResponseEntity<List<PostsModel>> getPostsByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok().body(this.postService.getPostsByUser(userId));
    }

    @PostMapping("/post")
    public ResponseEntity<PostsModel> register(@ModelAttribute @Valid CreatePostDTO postDTO) {
        return ResponseEntity.ok().body(this.postService.create(postDTO));
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<PostsModel> register(@PathVariable UUID postId) throws BadRequestException {
        return ResponseEntity.ok().body(this.postService.removePost(postId));
    }

}
