package com.projeto.interdisciplinar.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
import com.projeto.interdisciplinar.dtos.user.UpdatePasswordDTO;
import com.projeto.interdisciplinar.dtos.user.UpdateStatusDTO;
import com.projeto.interdisciplinar.dtos.user.UpdateUserDTO;
import com.projeto.interdisciplinar.enums.Roles;
import com.projeto.interdisciplinar.enums.Status;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
    private EmailService emailService;
    private UserRepository userRepository;

    public UserService(UserRepository userRepository, EmailService emailService,
            AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
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
            throw new BadRequestException(e);
        }
    }

    // update do user
    public UsersModel updateUser(
            UpdateUserDTO updateUserDTO) throws BadRequestException {

        try {
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
            throw new BadRequestException("ID de usuario invalido: " + e);
        }
    }

    // update do status do usuário
    public UsersModel updateStatus(UUID userId) throws BadRequestException {

        try {
            var user = this.userRepository.getReferenceById(userId);

            if (user.getStatus().equals(Status.AUTHORIZED)) {
                user.setStatus(Status.UNAUTHORIZED);

            } else if (user.getStatus().equals(Status.UNAUTHORIZED)) {
                user.setStatus(Status.AUTHORIZED);

            }

            return this.userRepository.save(user);

        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw new BadRequestException("ID de usuario inválido:" + e);
        }
    }

    // enviar email esqueci a senha
    private String generateRandomCode() {
        Random random = new Random();
        int randomCode = random.nextInt(999999);
        return String.format("%06d", randomCode);
    }

    public UsersModel sendCode(String email)
            throws BadRequestException {

        var user = (UsersModel) this.userRepository.findByEmail(email.toLowerCase());

        if (user == null) {
            throw new BadRequestException("Usuário não encontrado");
        }

        String rawCode = generateRandomCode();
        String rashCode = new BCryptPasswordEncoder().encode(rawCode);

        try {
            this.emailService.sendEmail(user.getEmail(), "Alteração de senha.",
                    "Ultilize o código: " + rawCode + " para alterar sua senha.");

            user.setCode_password(rashCode);
            return this.userRepository.save(user);

        } catch (EntityNotFoundException e) {
            throw new BadRequestException("E-mail do usuario inválido: " + e);
        }
    }

    // confirmar email / esqueci a senha
    public UsersModel confirmEmail(String email, String code) throws BadRequestException {

        var user = (UsersModel) this.userRepository.findByEmail(email.toLowerCase());

        if (user == null) {
            throw new BadRequestException("Usuário não encontrado");
        }

        try {
            if (new BCryptPasswordEncoder().matches(code, user.getCode_password())) {
                user.setCode_password(null);
            } else {
                throw new BadRequestException("Código inválido");
            }

            return this.userRepository.save(user);

        } catch (EntityNotFoundException e) {
            throw new BadRequestException("E-mail do usuario inválido: " + e);
        }
    }

    // Update da senha / esqueci a senha
    public UsersModel changePassword(String email, UpdatePasswordDTO updatePasswordDTO) throws BadRequestException {

        try {
            var user = (UsersModel) this.userRepository.findByEmail(email.toLowerCase());

            if (user == null) {
                throw new BadRequestException("Usuário não encontrado");
            }

            String rashPassword = new BCryptPasswordEncoder().encode(updatePasswordDTO.password());

            user.setPassword(rashPassword);

            return this.userRepository.save(user);

        } catch (EntityNotFoundException e) {
            throw new BadRequestException("E-mail do usuario inválido: " + e);
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

    public void updateUserImage(MultipartFile image) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID authenticatedUserId = ((UsersModel) authentication.getPrincipal()).getId();

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
        String imageName = authenticatedUserId + "_" + StringUtils.cleanPath(image.getOriginalFilename());

        // cria o diretório se não existir
        Path uploadDir = Paths.get(root);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // apaga o anterior
        try (Stream<Path> paths = Files.walk(uploadDir)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().contains(authenticatedUserId.toString()))
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
        userRepository.updateImageUrl(authenticatedUserId, imageName.toString());
    }

}
