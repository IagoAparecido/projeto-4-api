package com.projeto.interdisciplinar.services;

import java.time.LocalDateTime;

import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.projeto.interdisciplinar.dtos.AuthenticationDTO;
import com.projeto.interdisciplinar.dtos.TokenDTO;
import com.projeto.interdisciplinar.dtos.user.GetUsersDTO;
import com.projeto.interdisciplinar.dtos.user.UserDTO;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.UserRepository;

@Service
public class AuthenticationService {

    private AuthenticationManager authenticationManager;
    private TokenService tokenService;
    private UserRepository userRepository;

    public AuthenticationService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public TokenDTO login(AuthenticationDTO authenticationDTO) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(authenticationDTO.email(),
                authenticationDTO.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.genarateToken((UsersModel) auth.getPrincipal());
        return new TokenDTO(token);
    }

    public UsersModel create(UserDTO userDTO) throws BadRequestException {
        if (this.userRepository.findByEmail(userDTO.email()) != null)

            throw new BadRequestException("Email já existente.");

        String encryptedPassword = new BCryptPasswordEncoder().encode(userDTO.password());
        LocalDateTime createdAt = LocalDateTime.now();

        UsersModel user = new UsersModel();
        user.setEmail(userDTO.email());
        user.setName(userDTO.name());
        user.setPassword(encryptedPassword);
        user.setRole(userDTO.role());
        user.setCreatedAt(createdAt);

        var response = this.userRepository.save(user);
        return response;
    }

    public GetUsersDTO tokenData() throws BadRequestException {
        var security = SecurityContextHolder.getContext().getAuthentication();

        if (security.getPrincipal().getClass() != UsersModel.class) {
            throw new BadRequestException("Token Inválido");
        }

        UsersModel model = (UsersModel) security.getPrincipal();
        var user = this.userRepository.findByEmailAndReturnDto(model.getEmail());

        return user;
    }

}
