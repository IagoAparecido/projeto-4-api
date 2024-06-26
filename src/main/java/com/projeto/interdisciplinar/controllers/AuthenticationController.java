package com.projeto.interdisciplinar.controllers;

import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projeto.interdisciplinar.dtos.AuthenticationDTO;
import com.projeto.interdisciplinar.dtos.user.GetUsersDTO;
import com.projeto.interdisciplinar.dtos.user.UpdateIsAuthenticatedDTO;
import com.projeto.interdisciplinar.dtos.user.UserDTO;
import com.projeto.interdisciplinar.enums.Roles;
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

        @PostMapping("/login-dash")
        public ResponseEntity loginDash(@RequestBody @Valid AuthenticationDTO data) throws BadRequestException {
                return ResponseEntity.ok().body(this.authenticationService.loginDash(data));
        }

        @PostMapping("/confirm")
        public ResponseEntity confirmEmail(@RequestParam String email,
                        @RequestParam String code) throws BadRequestException {
                return ResponseEntity.ok().body(this.authenticationService.confirmEmail(email, code));
        }

        @GetMapping("/confirm/resend")
        public ResponseEntity resendCode(@RequestParam String email) throws BadRequestException {
                return ResponseEntity.ok().body(this.authenticationService.resendCode(email));
        }

        @PostMapping("/register")
        public ResponseEntity<UsersModel> register(Roles role,
                        @RequestBody @Valid UserDTO userDTO) throws BadRequestException {
                return ResponseEntity.ok().body(this.authenticationService.create(userDTO, "USER"));
        }

        @PostMapping("/register/admin")
        public ResponseEntity<UsersModel> registerAdmin(Roles role,
                        @RequestBody @Valid UserDTO userDTO) throws BadRequestException {
                return ResponseEntity.ok().body(this.authenticationService.create(userDTO, "ADMIN"));
        }

        @GetMapping("/token")
        public ResponseEntity<GetUsersDTO> tokenData() throws BadRequestException {
                return ResponseEntity.ok().body(this.authenticationService.tokenData());
        }

}
