package com.projeto.interdisciplinar.controllers;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.interdisciplinar.dtos.AuthenticationDTO;
import com.projeto.interdisciplinar.dtos.user.GetUsersDTO;
import com.projeto.interdisciplinar.dtos.user.UserDTO;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.UserRepository;
import com.projeto.interdisciplinar.services.AuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
        @Autowired
        private AuthenticationService authenticationService;

        @Autowired
        public UserRepository repository;

        @PostMapping("/login")
        public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
                return ResponseEntity.ok().body(this.authenticationService.login(data));
        }

        @PostMapping("/register")
        public ResponseEntity<UsersModel> register(
                        @RequestBody @Valid UserDTO userDTO) throws BadRequestException {
                return ResponseEntity.ok().body(this.authenticationService.create(userDTO));
        }

        @GetMapping("/token")
        public ResponseEntity<GetUsersDTO> tokenData() throws BadRequestException {
                return ResponseEntity.ok().body(this.authenticationService.tokenData());
        }

}
