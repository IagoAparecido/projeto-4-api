package com.projeto.interdisciplinar.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projeto.interdisciplinar.models.ImagesModel;

public interface ImageRepository extends JpaRepository<ImagesModel, UUID> {
}
