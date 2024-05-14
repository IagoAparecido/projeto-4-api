package com.projeto.interdisciplinar.services;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.interdisciplinar.dtos.comments.CreateCommentDTO;
import com.projeto.interdisciplinar.enums.Status;
import com.projeto.interdisciplinar.models.CommentsModel;
import com.projeto.interdisciplinar.models.SubCommentsModel;
import com.projeto.interdisciplinar.models.UsersModel;
import com.projeto.interdisciplinar.repositories.CommentRepository;
import com.projeto.interdisciplinar.repositories.PostRepository;
import com.projeto.interdisciplinar.repositories.SubCommentRepository;
import com.projeto.interdisciplinar.repositories.UserRepository;

@Service
public class CommentService {
    private PostRepository postRepository;
    private UserRepository userRepository;
    private CommentRepository commentRepository;
    private SubCommentRepository subCommentRepository;

    private CommentService(PostRepository postRepository, UserRepository userRepository,
            SubCommentRepository subCommentRepository,
            CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.subCommentRepository = subCommentRepository;
    }

    // carregar palavras impróprias do json
    private List<String> loadWords(String caminhoDoArquivo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, List<String>> jsonMap = objectMapper.readValue(new File(caminhoDoArquivo),
                    new TypeReference<Map<String, List<String>>>() {
                    });
            // extrair a lista de palavras impróprias
            return jsonMap.get("palavras_improprias");
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // verificar e substituir palavras impróprias em um campo
    private String verifyWords(String text, List<String> words) {
        for (String word : words) {
            text = text.replaceAll("(?i)\\b" + word + "\\b", "*****");
        }
        return text;
    }

    // criar comntário
    public CommentsModel create(CreateCommentDTO createCommentDTO, UUID postId) throws BadRequestException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersModel authenticatedUser = (UsersModel) authentication.getPrincipal();

            if (createCommentDTO.description().isEmpty()) {
                throw new BadRequestException("Comentário vazio.");
            }

            if (authenticatedUser.getStatus().equals(Status.UNAUTHORIZED)) {
                throw new BadRequestException("Usuário não autorizado a realizar essa operação.");
            }

            // armazena no banco
            LocalDateTime createdAt = LocalDateTime.now();
            CommentsModel comments = new CommentsModel();

            List<String> words = loadWords("src/main/java/com/projeto/interdisciplinar/configs/words.json");

            // verificar e substituir palavras impróprias
            String description = verifyWords(createCommentDTO.description(), words);

            comments.setDescription(description);
            comments.setCreated_at(createdAt);

            var post = this.postRepository.getReferenceById(postId);
            var user = this.userRepository.getReferenceById(authenticatedUser.getId());

            comments.setPost(post);
            comments.setUser(user);

            return this.commentRepository.save(comments);

        } catch (Exception e) {
            throw new BadRequestException(e);
        }

    }

    // criar subcomentário
    public SubCommentsModel createSubComment(CreateCommentDTO createCommentDTO, UUID commentId)

            throws BadRequestException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersModel authenticatedUser = (UsersModel) authentication.getPrincipal();

            if (createCommentDTO.description().isEmpty()) {
                throw new BadRequestException("Comentário vazio.");
            }

            if (authenticatedUser.getStatus().equals(Status.UNAUTHORIZED)) {
                throw new BadRequestException("Usuário não autorizado a realizar essa operação.");
            }

            // armazena no banco
            LocalDateTime createdAt = LocalDateTime.now();
            SubCommentsModel subComments = new SubCommentsModel();

            List<String> words = loadWords("src/main/java/com/projeto/interdisciplinar/configs/words.json");

            // verificar e substituir palavras impróprias
            String description = verifyWords(createCommentDTO.description(), words);

            subComments.setCreated_at(createdAt);
            subComments.setDescription(description);

            var comment = this.commentRepository.getReferenceById(commentId);
            var user = this.userRepository.getReferenceById(authenticatedUser.getId());

            subComments.setComment(comment);
            subComments.setUser(user);

            return this.subCommentRepository.save(subComments);
        } catch (Exception e) {
            throw new BadRequestException(e);
        }

    }

    public SubCommentsModel removeSubComment(UUID commentId) throws BadRequestException {
        try {
            SubCommentsModel subComment = this.subCommentRepository.findById(commentId).orElse(null);

            if (subComment != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UsersModel authenticatedUser = (UsersModel) authentication.getPrincipal();

                if (authenticatedUser.getRole().toString().equals("ADMIN")
                        || authenticatedUser.getId().equals(subComment.getUser().getId())) {

                    this.subCommentRepository.deleteById(commentId);

                    return subComment;
                } else {
                    throw new BadRequestException("Você não tem permissão para excluir este comentário.");
                }
            } else {
                throw new BadRequestException("Comentário não encontrado.");
            }

        } catch (Exception e) {
            throw new BadRequestException(e);
        }
    }

    public CommentsModel removeComment(UUID commentId) throws BadRequestException {
        try {
            CommentsModel comment = this.commentRepository.findById(commentId).orElse(null);

            if (comment != null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UsersModel authenticatedUser = (UsersModel) authentication.getPrincipal();

                if (authenticatedUser.getRole().toString().equals("ADMIN")
                        || authenticatedUser.getId().equals(comment.getUser().getId())) {

                    this.commentRepository.deleteById(commentId);

                    return comment;

                } else {
                    throw new BadRequestException("Você não tem permissão para excluir este comentário.");
                }
            } else {
                throw new BadRequestException("Comentário não encontrada.");
            }

        } catch (Exception e) {
            throw new BadRequestException(e);
        }

    }
}
