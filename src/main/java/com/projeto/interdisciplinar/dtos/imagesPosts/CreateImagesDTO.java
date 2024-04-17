package com.projeto.interdisciplinar.dtos.imagesPosts;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public record CreateImagesDTO(
        String url,
        MultipartFile image,
        UUID post_id) {

}
