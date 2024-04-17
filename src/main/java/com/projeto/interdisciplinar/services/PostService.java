package com.projeto.interdisciplinar.services;

import java.nio.file.FileSystemException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.projeto.interdisciplinar.dtos.imagesPosts.CreateImagesDTO;
import com.projeto.interdisciplinar.dtos.posts.CreatePostDTO;
import com.projeto.interdisciplinar.models.PostsModel;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.PostRepository;
import com.projeto.interdisciplinar.repositories.UserRepository;

@Service
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;
    private ImageService imageService;

    public PostService(PostRepository postRepository, UserRepository userRepository, ImageService imageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    public PostsModel create(CreatePostDTO createPostDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = ((UsersModel) authentication.getPrincipal()).getId();

        var user = this.userRepository.getReferenceById(authenticatedUserId);

        LocalDateTime createdAt = LocalDateTime.now();
        PostsModel post = new PostsModel();
        BeanUtils.copyProperties(createPostDTO, post);

        post.setCreated_at(createdAt);
        post.setUser(user);

        var response = this.postRepository.save(post);

        for (int i = 0; i < createPostDTO.image().size(); i++) {
            try {
                this.imageService.create(new CreateImagesDTO(
                        createPostDTO.image().get(i).getOriginalFilename(),
                        createPostDTO.image().get(i),
                        response.getId()));
            } catch (BadRequestException | FileSystemException e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    public List<PostsModel> getAllPosts() {
        return this.postRepository.findAllPosts();
    }
}
