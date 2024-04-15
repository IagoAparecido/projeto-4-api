package com.projeto.interdisciplinar.dtos.user;

import java.time.LocalDateTime;
import java.util.UUID;

import com.projeto.interdisciplinar.enums.Roles;

public interface GetUsersDTO {
    UUID getId();

    String getName();

    String getStatus();

    Boolean getIs_authenticated();

    String getImage_url();

    String getEmail();

    Roles getRole();

    String getCreated_at();
}
