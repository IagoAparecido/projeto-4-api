package com.projeto.interdisciplinar.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;

import com.projeto.interdisciplinar.dtos.user.GetUsersDTO;
import com.projeto.interdisciplinar.models.UsersModel;

public interface UserRepository extends JpaRepository<UsersModel, UUID> {
    UserDetails findByEmail(String email);

    @Query(nativeQuery = true, value = "SELECT id, name, email, role FROM users WHERE email = ?1")
    GetUsersDTO findByEmailAndReturnDto(String email);
}
