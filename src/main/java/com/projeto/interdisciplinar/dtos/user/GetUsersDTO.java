package com.projeto.interdisciplinar.dtos.user;

import java.util.UUID;

import com.projeto.interdisciplinar.enums.Roles;

public interface GetUsersDTO {
    UUID getId();

    String getName();

    String getEmail();

    Roles getRole();
}
