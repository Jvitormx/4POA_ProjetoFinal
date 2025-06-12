package com._poadoacaolocalapp.doacaolocal_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "status_publicacao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StatusPublicacao {
    @Id
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String nome;
}
