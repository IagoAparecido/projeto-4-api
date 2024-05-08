package com.projeto.interdisciplinar.services;

import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.projeto.interdisciplinar.dtos.imagesPosts.CreateImagesDTO;
import com.projeto.interdisciplinar.dtos.posts.CreatePostDTO;
import com.projeto.interdisciplinar.models.ImagesModel;
import com.projeto.interdisciplinar.models.PostsModel;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.ImageRepository;
import com.projeto.interdisciplinar.repositories.PostRepository;
import com.projeto.interdisciplinar.repositories.UserRepository;

@Service
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;
    private ImageRepository imageRepository;
    private ImageService imageService;

    public PostService(PostRepository postRepository, UserRepository userRepository, ImageService imageService,
            ImageRepository imageRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.imageRepository = imageRepository;
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

    public List<PostsModel> getAllPosts(int page) {
        Pageable pageable = PageRequest.of(page, 20);
        return this.postRepository.findAll(pageable).getContent();
    }

    public List<PostsModel> getPostsByUser(UUID userId, int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.postRepository.findByUser(userId, pageable).getContent();
    }

    public PostsModel getUniquePost(UUID postId) {
        PostsModel response = this.postRepository.findById(postId).orElse(null);
        return response;
    }

    public PostsModel removePost(UUID postId) throws BadRequestException {
        try {
            PostsModel post = this.postRepository.findById(postId).orElse(null);

            if (post != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UsersModel authenticatedUser = (UsersModel) authentication.getPrincipal();

                // List<UUID> imageIds = new ArrayList<>();
                List<String> imageUrl = new ArrayList<>();
                for (ImagesModel image : post.getImages()) {
                    // imageIds.add(image.getId());
                    imageUrl.add(image.getUrl());
                }

                for (String imagePath : imageUrl) {
                    try {
                        Path path = Paths.get("src/main/resources/static/uploads/posts/" + imagePath);
                        Files.deleteIfExists(path);
                    } catch (BadRequestException e) {
                        throw new BadRequestException("Erro ao excluir o arquivo de imagem: " + imagePath);
                    }
                }

                if (authenticatedUser.getRole().toString().equals("ADMIN")
                        || authenticatedUser.getId().equals(post.getUser().getId())) {

                    // this.imageRepository.deleteAllById(imageIds);
                    this.postRepository.deleteById(postId);

                } else {
                    throw new BadRequestException("Você não tem permissão para excluir este post.");
                }
            } else {
                throw new BadRequestException("Postagem não encontrada.");
            }

        } catch (Exception e) {
            throw new BadRequestException(e);
        }
        return null;
    }

}
