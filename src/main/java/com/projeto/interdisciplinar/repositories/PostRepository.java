package com.projeto.interdisciplinar.repositories;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.projeto.interdisciplinar.models.PostsModel;

public interface PostRepository extends JpaRepository<PostsModel, UUID> {

    @Query(nativeQuery = true, value = "SELECT * FROM posts ORDER BY posts.created_at DESC")
    List<PostsModel> findAllPosts();
}
