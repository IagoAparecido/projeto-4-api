package com.projeto.interdisciplinar.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projeto.interdisciplinar.dtos.posts.PostsDTO;
import com.projeto.interdisciplinar.models.PostsModel;

public interface PostRepository extends JpaRepository<PostsModel, UUID> {

    @Query(nativeQuery = true, value = "Select * FROM posts WHERE posts.uf = ?1")
    Page<PostsModel> findAllByRegion(String region, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE posts.user_id = :userId ORDER BY posts.created_at DESC")
    Page<PostsModel> findByUser(@Param("userId") UUID userId, Pageable pageable);
}
