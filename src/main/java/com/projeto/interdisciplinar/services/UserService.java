package com.projeto.interdisciplinar.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.projeto.interdisciplinar.dtos.user.GetUsersDTO;
import com.projeto.interdisciplinar.dtos.user.UpdateUserDTO;
import com.projeto.interdisciplinar.dtos.user.UserDTO;
import com.projeto.interdisciplinar.enums.Roles;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
    private UserRepository userRepository;
    private AuthenticationService authenticationService;

    public UserService(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    // get dos users default
    public List<GetUsersDTO> getAllUsersDefault() {
        return this.userRepository.findAllDefaultUsers();
    }

    // get dos users admin
    public List<GetUsersDTO> getAllUsersAdmin() {
        return this.userRepository.findAllAdminUsers();
    }

    // update dos admin
    public UsersModel updateAdmin(

            UUID userId,
            UpdateUserDTO updateUserDTO) throws BadRequestException {

        try {
            var user = this.userRepository.getReferenceById(userId);

            if (user.getRole() != Roles.ADMIN) {
                throw new BadRequestException("Apenas usuários com a role de ADMIN podem ser atualizados");
            }

            if (!updateUserDTO.name().isEmpty())
                user.setName(updateUserDTO.name());

            if (!updateUserDTO.password().isEmpty()) {
                String encryptedPassword = new BCryptPasswordEncoder().encode(updateUserDTO.password());
                user.setPassword(encryptedPassword);
            }

            return this.userRepository.save(user);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw new BadRequestException("ID de usuario invalido");
        }
    }

    // update do user
    public UsersModel updateUser(
            UpdateUserDTO updateUserDTO) throws BadRequestException {

        try {
            System.out.println("Acessou aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UUID authenticatedUserId = ((UsersModel) authentication.getPrincipal()).getId();

            var user = this.userRepository.getReferenceById(authenticatedUserId);

            if (!updateUserDTO.name().isEmpty())
                user.setName(updateUserDTO.name());

            if (!updateUserDTO.password().isEmpty()) {
                String encryptedPassword = new BCryptPasswordEncoder().encode(updateUserDTO.password());
                user.setPassword(encryptedPassword);
            }

            return this.userRepository.save(user);

        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw new BadRequestException("ID de usuario invalido");
        }
    }

    // update das imagens do user
    String[] array = {
            "image/png",
            "image/jpg",
            "image/jpeg",
    };
    List<String> mimetypePermissions = new ArrayList<>(Arrays.asList(array));

    @Value("${image.url}") // caminho onde as imagens serão armazenadas //no apllication
    private String root;

    // tipos de arquivos permitidos
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpeg", "jpg");

    public void updateUserImage(UUID userId, MultipartFile image) throws IOException {
        // verifica se o arquivo não esta vazio
        if (image.isEmpty()) {
            throw new BadRequestException("O arquivo de imagem está vazio.");
        }

        // verifique a extensão do arquivo
        String fileExtension = StringUtils.getFilenameExtension(image.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(fileExtension.toLowerCase())) {
            throw new BadRequestException("Apenas arquivos PNG e JPEG são permitidos.");
        }

        // pega o nome do arquivo
        String imageName = userId + "_" + StringUtils.cleanPath(image.getOriginalFilename());

        // cria o diretório se não existir
        Path uploadDir = Paths.get(root);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // apaga o anterior
        try (Stream<Path> paths = Files.walk(uploadDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().contains(userId.toString()))
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            throw new RuntimeException("Erro ao excluir arquivo existente: " + e.getMessage());
                        }
                    });
        }

        // salva o arquivo no diretório
        Path imageUrl = uploadDir.resolve(imageName);
        Files.copy(image.getInputStream(), imageUrl);

        // atualiza o caminho da imagem
        userRepository.updateImageUrl(userId, imageName.toString());
    }

}
