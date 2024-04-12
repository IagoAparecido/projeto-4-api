package com.projeto.interdisciplinar.dtos.user;

import java.util.UUID;

import com.projeto.interdisciplinar.enums.Roles;

public interface GetUsersDTO {
    UUID getId();

    String getName();

    String getStatus();

    boolean getIsAuthenticated();

    String getImage_url();

    String getEmail();

    Roles getRole();
}
