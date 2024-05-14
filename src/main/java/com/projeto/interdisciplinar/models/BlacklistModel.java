package com.projeto.interdisciplinar.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "blacklist")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private LocalDateTime createdAt;

    // fk
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UsersModel user;

    // fk
    @ManyToOne
    @JoinColumn(name = "user_blocked_id", nullable = false)
    private UsersModel userBlocked;
}
