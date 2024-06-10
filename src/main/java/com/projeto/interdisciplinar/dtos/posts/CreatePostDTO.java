package com.projeto.interdisciplinar.dtos.posts;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotNull;

public record CreatePostDTO(
        String name,
        String age,
        String race,
        @NotNull String sex,
        @NotNull String type,
        @NotNull String description,
        @NotNull String uf,

        List<MultipartFile> image,

        @NotNull String city) {

}
