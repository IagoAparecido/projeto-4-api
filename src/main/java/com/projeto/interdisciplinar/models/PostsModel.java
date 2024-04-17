package com.projeto.interdisciplinar.models;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    private String age;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String UF;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String sex;

    private LocalDateTime created_at;

    // fk
    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    // @JsonIgnore
    private UsersModel user;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<ImagesModel> images;
}
