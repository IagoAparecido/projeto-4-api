package com.projeto.interdisciplinar.dtos.posts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.models.ImagesModel;

public record PostsDTO(
        UUID id,
        String name,
        String age,
        String description,
        String UF,
        String city,
        String sex,
        String type,
        String race,
        LocalDateTime createdAt,
        UsersModel user,
        List<ImagesModel> images) {
}
