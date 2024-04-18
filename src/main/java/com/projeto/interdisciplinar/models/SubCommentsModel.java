package com.projeto.interdisciplinar.models;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
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

@Entity
@Table(name = "sub_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubCommentsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String description;

    private LocalDateTime created_at;

    // fk
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private UsersModel user;

    @ManyToOne()
    @JoinColumn(name = "comment_id", nullable = false)
    @JsonIgnore
    private CommentsModel comment;
}
