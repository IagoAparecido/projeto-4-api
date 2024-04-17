package com.projeto.interdisciplinar.dtos.posts;

import java.time.LocalDateTime;
import java.util.UUID;

import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.models.ImagesModel;

public record PostsDTO(
                UUID id, String name,
                String age,
                String description,
                String UF,
                String city,
                String sex,
                LocalDateTime created_at,
                UsersModel user,
                ImagesModel images) {

}
