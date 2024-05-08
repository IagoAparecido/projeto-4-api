package com.projeto.interdisciplinar.repositories;

import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.projeto.interdisciplinar.models.PostsModel;

public interface PostRepository extends JpaRepository<PostsModel, UUID> {

    Page<PostsModel> findAll(Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM posts WHERE posts.user_id = :userId ORDER BY posts.created_at DESC")
    List<PostsModel> findByUser(@Param("userId") UUID userId);
}
