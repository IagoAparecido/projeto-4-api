package com.projeto.interdisciplinar.services;

import java.time.LocalDateTime;
import java.util.Random;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.interdisciplinar.dtos.AuthenticationDTO;
import com.projeto.interdisciplinar.dtos.TokenDTO;
import com.projeto.interdisciplinar.dtos.user.GetUsersDTO;
import com.projeto.interdisciplinar.dtos.user.UpdateIsAuthenticatedDTO;
import com.projeto.interdisciplinar.dtos.user.UserDTO;
import com.projeto.interdisciplinar.enums.Roles;
import com.projeto.interdisciplinar.enums.Status;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthenticationService {

    private AuthenticationManager authenticationManager;
    private TokenService tokenService;
    private UserRepository userRepository;
    private EmailService emailService;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            TokenService tokenService,
            EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    private String generateRandomCode() {
        Random random = new Random();
        int randomCode = random.nextInt(999999);

        return String.format("%06d", randomCode);
    }

    // login
    public ResponseEntity<TokenDTO> login(AuthenticationDTO authenticationDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(authenticationDTO.email(),
                authenticationDTO.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // verifica se o user esta autenticado
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        UsersModel user = (UsersModel) userDetails;
        if (!user.isAuthenticated()) {
            String rawCode = generateRandomCode();
            String rashCode = new BCryptPasswordEncoder().encode(rawCode);

            user.setCode(rashCode);

            // se o user não estiver autenticado, reenvia o email de confirmação
            this.emailService.sendEmail(user.getEmail(), "Confirmação do cadastro.",
                    "Ultilize o código: " + "<span style=\"font-size: 20px; font-weight: bold;\">" + rawCode + "</span>"
                            + " para confirmar seu cadastro.");

            this.userRepository.save(user);

            return ResponseEntity.ok().body(
                    new TokenDTO("Confirme seu e-mail para fazer login."));
        }

        var token = tokenService.genarateToken(user);
        return ResponseEntity.ok().body(new TokenDTO(token));
    }

    // create de usuários
    public UsersModel create(UserDTO userDTO, String role) {
        if (this.userRepository.findByEmail(userDTO.email()) != null)
            throw new RuntimeException("Email já existente.");

        String encryptedPassword = new BCryptPasswordEncoder().encode(userDTO.password());
        LocalDateTime createdAt = LocalDateTime.now();

        UsersModel user = new UsersModel();
        user.setEmail(userDTO.email());
        user.setName(userDTO.name());
        user.setStatus(Status.AUTHORIZED);
        user.setAuthenticated(role == "ADMIN" ? true : false);
        user.setPassword(encryptedPassword);
        user.setRole(Roles.valueOf(role));
        user.setCreatedAt(createdAt);

        String rawCode = generateRandomCode();
        String code = "ADMIN".equals(role) ? null : new BCryptPasswordEncoder().encode(rawCode);

        user.setCode(code);

        if (role == "USER") {
            this.emailService.sendEmail(user.getEmail(), "Confirmação do cadastro.",
                    "Ultilize o código: " + rawCode + " para confirmar seu cadastro.");
        }

        var response = this.userRepository.save(user);
        return response;
    }

    // confirmação do email
    public UsersModel confirmEmail(String email, UpdateIsAuthenticatedDTO isAuthenticatedDTO)
            throws BadRequestException {

        var user = (UsersModel) this.userRepository.findByEmail(email);

        try {
            System.out.println(isAuthenticatedDTO.code());
            System.out.println(new BCryptPasswordEncoder().matches(user.getCode(), isAuthenticatedDTO.code()));

            if (new BCryptPasswordEncoder().matches(isAuthenticatedDTO.code(), user.getCode())) {
                user.setAuthenticated(true);
                user.setCode(null);
            } else {
                throw new BadRequestException("Código inválido");
            }

            return this.userRepository.save(user);

        } catch (EntityNotFoundException e) {
            throw new BadRequestException("E-mail do usuario inválido");
        }
    }

    // verifica token
    public GetUsersDTO tokenData() throws BadRequestException {
        var security = SecurityContextHolder.getContext().getAuthentication();

        if (security.getPrincipal().getClass() != UsersModel.class) {
            throw new BadRequestException("Token Inválido");
        }

        UsersModel model = (UsersModel) security.getPrincipal();
        var user = this.userRepository.findByEmailAndReturnDto(model.getEmail());

        if (user.getIs_authenticated() == false) {
            throw new BadRequestException("Usuário não autenticado");
        }

        return user;
    }

}
