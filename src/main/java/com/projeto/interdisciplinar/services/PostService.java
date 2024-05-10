package com.projeto.interdisciplinar.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.interdisciplinar.dtos.imagesPosts.CreateImagesDTO;
import com.projeto.interdisciplinar.dtos.posts.CreatePostDTO;
import com.projeto.interdisciplinar.dtos.posts.GetPostsAndCountDTO;
import com.projeto.interdisciplinar.dtos.posts.PostsDTO;
import com.projeto.interdisciplinar.models.ImagesModel;
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

    // carregar palavras impróprias do json
    private List<String> loadWords(String caminhoDoArquivo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, List<String>> jsonMap = objectMapper.readValue(new File(caminhoDoArquivo),
                    new TypeReference<Map<String, List<String>>>() {
                    });
            // extrair a lista de palavras impróprias
            return jsonMap.get("palavras_improprias");
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // verificar e substituir palavras impróprias em um campo
    private String verifyWords(String text, List<String> words) {
        for (String word : words) {
            text = text.replaceAll("(?i)\\b" + word + "\\b", "*****");
        }
        return text;
    }

    public PostsModel create(CreatePostDTO createPostDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = ((UsersModel) authentication.getPrincipal()).getId();

        var user = this.userRepository.getReferenceById(authenticatedUserId);

        LocalDateTime createdAt = LocalDateTime.now();
        PostsModel post = new PostsModel();
        BeanUtils.copyProperties(createPostDTO, post);

        List<String> words = loadWords("src/main/java/com/projeto/interdisciplinar/configs/words.json");

        // verificar e substituir palavras impróprias
        String name = verifyWords(createPostDTO.name(), words);
        String race = verifyWords(createPostDTO.race(), words);
        String type = verifyWords(createPostDTO.type(), words);
        String city = verifyWords(createPostDTO.city(), words);
        String description = verifyWords(createPostDTO.description(), words);

        post.setName(name);
        post.setRace(race);
        post.setType(type);
        post.setCity(city);
        post.setDescription(description);
        post.setCreatedAt(createdAt);
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

    public GetPostsAndCountDTO getAllPosts(int page) {

        var posts = this.postRepository.findAll(PageRequest.of(page, 20, Sort.Direction.DESC, "createdAt"))
                .map(post -> new PostsDTO(post.getId(), post.getName(), post.getAge(), post.getDescription(),
                        post.getUF(), post.getCity(), post.getSex(), post.getType(), post.getRace(),
                        post.getCreatedAt(), post.getUser(),
                        post.getImages()));

        var response = new GetPostsAndCountDTO(posts.getContent(), posts.getTotalPages());
        return response;

    }

    public GetPostsAndCountDTO getAllPostsByRegion(String region, int page) {
        Pageable pageable = PageRequest.of(page, 20);

        var postsPage = this.postRepository.findAllByRegion(region.toUpperCase(), pageable);
        List<PostsDTO> postsDTOs = postsPage.getContent().stream()
                .map(post -> new PostsDTO(post.getId(), post.getName(), post.getAge(), post.getDescription(),
                        post.getUF(), post.getCity(), post.getSex(), post.getType(), post.getRace(),
                        post.getCreatedAt(), post.getUser(),
                        post.getImages()))
                .collect(Collectors.toList());

        return new GetPostsAndCountDTO(postsDTOs, postsPage.getTotalPages());

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
