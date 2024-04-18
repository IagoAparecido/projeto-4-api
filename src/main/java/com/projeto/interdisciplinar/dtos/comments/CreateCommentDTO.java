package com.projeto.interdisciplinar.dtos.comments;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateCommentDTO(
        @NotNull String description) {

}