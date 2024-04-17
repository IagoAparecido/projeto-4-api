package com.projeto.interdisciplinar.services;

import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import org.apache.coyote.BadRequestException;

import com.projeto.interdisciplinar.dtos.imagesPosts.CreateImagesDTO;
import com.projeto.interdisciplinar.models.ImagesModel;
import com.projeto.interdisciplinar.repositories.ImageRepository;
import com.projeto.interdisciplinar.repositories.PostRepository;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ImageService {

    private final Path root = Paths.get("src/main/resources/static/uploads/posts");

    private ImageRepository imageRepository;
    private PostRepository postRepository;

    private ImageService(ImageRepository imageRepository, PostRepository postRepository) {
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
    }

    public ImagesModel create(CreateImagesDTO createImagesDTO) throws BadRequestException, FileSystemException {
        String[] array = {
                "image/png",
                "image/jpg",
                "image/jpeg"
        };

        System.out.println(root);

        List<String> mimetypePermissions = new ArrayList<>(Arrays.asList(array));

        if (createImagesDTO.image().isEmpty()) {
            throw new BadRequestException("Necessário ao menos um unico arquivo!");
        }

        if (!mimetypePermissions.contains(createImagesDTO.image().getContentType())) {
            throw new BadRequestException("Tipo de arquivo invalido.");
        }

        // gera o nome
        String imageName = UUID.randomUUID().toString()
                + StringUtils.cleanPath(createImagesDTO.image().getOriginalFilename());

        // salva nos arquivos
        try {
            if (!Files.exists(root))
                Files.createDirectory(root);

            Files.copy(createImagesDTO.image().getInputStream(), this.root.resolve(imageName));
        } catch (IOException e) {
            throw new FileSystemException("Falha ao salvar arquivo");
        }

        // armazena no banco
        LocalDateTime createdAt = LocalDateTime.now();
        ImagesModel image = new ImagesModel();
        image.setCreated_at(createdAt);
        image.setUrl(imageName);

        var post = this.postRepository.getReferenceById(createImagesDTO.post_id());
        image.setPost(post);

        try {
            return this.imageRepository.save(image);
        } catch (Exception e) {
            // remove arquivo
            try {
                Path delete = Paths.get("${image.post.url}/" + imageName);
                Files.deleteIfExists(delete);
            } catch (IOException io) {
                throw new FileSystemException("Falha ao deletar o arquivo do sistema!");
            }
            throw e;
        }

    }

}
