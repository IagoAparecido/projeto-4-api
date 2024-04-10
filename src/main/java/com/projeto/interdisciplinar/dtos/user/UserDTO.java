package com.projeto.interdisciplinar.dtos.user;

import org.springframework.web.multipart.MultipartFile;

import com.projeto.interdisciplinar.enums.Roles;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserDTO(
        @NotNull @Email(message = "Providencie um email valido!") String email,

        @NotNull String password,

        MultipartFile image,

        @NotNull String name,

        @NotNull Roles role) {

}