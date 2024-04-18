package com.projeto.interdisciplinar.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.interdisciplinar.models.SubCommentsModel;

public interface SubCommentRepository extends JpaRepository<SubCommentsModel, UUID> {

}
