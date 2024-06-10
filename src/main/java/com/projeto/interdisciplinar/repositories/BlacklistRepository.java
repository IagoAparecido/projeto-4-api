package com.projeto.interdisciplinar.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.projeto.interdisciplinar.models.BlacklistModel;

public interface BlacklistRepository extends JpaRepository<BlacklistModel, UUID> {

    @Query(value = "SELECT * FROM blacklist WHERE user_id = ?1 AND user_blocked_id = ?2", nativeQuery = true)
    BlacklistModel alreadyExist(UUID id, UUID id2);

    @Query(nativeQuery = true, value = "SELECT * FROM blacklist WHERE user_id = ?1 ORDER BY created_at DESC")
    List<BlacklistModel> findByUser(UUID userId);

}
