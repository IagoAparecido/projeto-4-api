package com.projeto.interdisciplinar.controllers;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.projeto.interdisciplinar.dtos.user.GetUsersDTO;
import com.projeto.interdisciplinar.services.UserService;

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

    // Update da imagem do usuário
    @PutMapping("/{userId}/image")
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
