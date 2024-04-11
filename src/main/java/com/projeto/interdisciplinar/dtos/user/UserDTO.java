package com.projeto.interdisciplinar.dtos.user;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserDTO(@NotNull @Email(message = "Providencie um email válido!") String email,

                @NotNull String password,

                MultipartFile image,

                @NotNull String name

) {

}