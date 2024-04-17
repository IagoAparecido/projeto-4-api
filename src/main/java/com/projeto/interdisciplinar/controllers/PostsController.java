package com.projeto.interdisciplinar.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping("/post")
    public ResponseEntity<PostsModel> register(@ModelAttribute @Valid CreatePostDTO postDTO) {
        return ResponseEntity.ok().body(this.postService.create(postDTO));
    }

}
