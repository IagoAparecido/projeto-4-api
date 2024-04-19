package com.projeto.interdisciplinar.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;

import com.projeto.interdisciplinar.dtos.comments.CreateCommentDTO;
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

    public CommentsModel create(CreateCommentDTO createCommentDTO, UUID postId) throws BadRequestException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersModel authenticatedUser = (UsersModel) authentication.getPrincipal();

            if (createCommentDTO.description().isEmpty()) {
                throw new BadRequestException("Comentário vazio.");
            }

            // armazena no banco
            LocalDateTime createdAt = LocalDateTime.now();
            CommentsModel comments = new CommentsModel();
            comments.setCreated_at(createdAt);
            comments.setDescription(createCommentDTO.description());

            var post = this.postRepository.getReferenceById(postId);
            var user = this.userRepository.getReferenceById(authenticatedUser.getId());

            comments.setPost(post);
            comments.setUser(user);

            return this.commentRepository.save(comments);

        } catch (Exception e) {
            throw new BadRequestException(e);
        }

    }

    public SubCommentsModel createSubComment(CreateCommentDTO createCommentDTO, UUID commentId)

            throws BadRequestException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UsersModel authenticatedUser = (UsersModel) authentication.getPrincipal();

            if (createCommentDTO.description().isEmpty()) {
                throw new BadRequestException("Comentário vazio.");
            }

            // armazena no banco
            LocalDateTime createdAt = LocalDateTime.now();
            SubCommentsModel subComments = new SubCommentsModel();
            subComments.setCreated_at(createdAt);
            subComments.setDescription(createCommentDTO.description());

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
                throw new BadRequestException("Comentário não encontrada.");
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
