package com.projeto.interdisciplinar.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.interdisciplinar.models.CommentsModel;

public interface CommentRepository extends JpaRepository<CommentsModel, UUID> {

}
