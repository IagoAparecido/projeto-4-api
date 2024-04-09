package com.projeto.interdisciplinar.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthenticationDTO(
                @Email @NotNull String email,
                @NotNull String password) {
}