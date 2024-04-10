package com.projeto.interdisciplinar.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import com.projeto.interdisciplinar.dtos.user.GetUsersDTO;
import com.projeto.interdisciplinar.models.UsersModel;

import jakarta.transaction.Transactional;

public interface UserRepository extends JpaRepository<UsersModel, UUID> {
    UserDetails findByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT id, name, email, role, image_url FROM users WHERE email = ?1")
    GetUsersDTO findByEmailAndReturnDto(String email);

    @Query(nativeQuery = true, value = "SELECT id, name, email, role, image_url FROM users u WHERE u.role = 'USER'")
    List<GetUsersDTO> findAllDefaultUsers();

    @Query(nativeQuery = true, value = "SELECT id, name, email, role, image_url FROM users u WHERE u.role = 'ADMIN'")
    List<GetUsersDTO> findAllAdminUsers();

    @Transactional
    @Modifying
    @Query("UPDATE users u SET u.imageUrl = ?2 WHERE u.id = ?1")
    void updateImageUrl(UUID userId, String imagePath);
}
