package com.projeto.interdisciplinar.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.interdisciplinar.dtos.user.GetUsersDTO;
import com.projeto.interdisciplinar.dtos.user.UpdateUserDTO;
import com.projeto.interdisciplinar.dtos.user.UserDTO;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("users")
public class UsersController {

    private UserService userService;

    public UsersController(
            UserService userService) {
        this.userService = userService;
    }

    // Get de todos os usuários com a role USER
    @GetMapping()
    public ResponseEntity<List<GetUsersDTO>> getAllDefaultUsers() {
        return ResponseEntity.ok().body(this.userService.getAllUsersDefault());
    }

    // Get de todos os usuários com a role ADMIN
    @GetMapping("/admin")
    public ResponseEntity<List<GetUsersDTO>> getAllAdminUsers() {
        return ResponseEntity.ok().body(this.userService.getAllUsersAdmin());
    }

    // Update dos admin
    @PatchMapping("/admin/{userId}")
    public ResponseEntity<UsersModel> updateAdmin(@PathVariable UUID userId,
            @Valid @RequestBody UpdateUserDTO updateUserDTO)
            throws BadRequestException {
        return ResponseEntity.ok().body(this.userService.updateAdmin(userId, updateUserDTO));
    }

    // Update do user
    @PatchMapping("/user")
    public ResponseEntity<UsersModel> updateUser(@RequestBody @Valid UpdateUserDTO updateUserDTO)
            throws BadRequestException {
        return ResponseEntity.ok().body(this.userService.updateUser(updateUserDTO));
    }

    // Update da imagem do usuário
    @PatchMapping("/{userId}/image")
    public ResponseEntity<String> updateUserImage(@PathVariable UUID userId,
            @RequestParam("image") MultipartFile image) {
        try {
            userService.updateUserImage(userId, image);
            return ResponseEntity.ok("Imagem do usuário atualizada com sucesso.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao fazer upload da imagem: " + e.getMessage());
        }
    }

}
