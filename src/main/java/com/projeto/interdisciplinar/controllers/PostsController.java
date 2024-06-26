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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.interdisciplinar.dtos.posts.CreatePostDTO;
import com.projeto.interdisciplinar.dtos.posts.GetPostsAndCountDTO;
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
    public ResponseEntity<GetPostsAndCountDTO> getAllPosts(@RequestParam int page) {
        return ResponseEntity.ok().body(this.postService.getAllPosts(page));
    }

    @GetMapping("/{region}")
    public ResponseEntity<GetPostsAndCountDTO> getPostsByRegion(@PathVariable String region, @RequestParam int page) {
        return ResponseEntity.ok().body(this.postService.getAllPostsByRegion(region, page));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GetPostsAndCountDTO> getPostsByUser(@PathVariable UUID userId, @RequestParam int page) {
        return ResponseEntity.ok().body(this.postService.getPostsByUser(userId, page));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostsModel> getUniquePost(@PathVariable UUID postId) {
        return ResponseEntity.ok().body(this.postService.getUniquePost(postId));
    }

    @PostMapping("/post")
    public ResponseEntity<PostsModel> register(@ModelAttribute @Valid CreatePostDTO postDTO)
            throws BadRequestException {
        return ResponseEntity.ok().body(this.postService.create(postDTO));
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<PostsModel> remove(@PathVariable UUID postId) throws BadRequestException {
        return ResponseEntity.ok().body(this.postService.removePost(postId));
    }

}
