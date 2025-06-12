package com._poadoacaolocalapp.doacaolocal_backend.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "matches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "publicacao_id", nullable = false)
    private Publicacao publicacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // solicitante

    @Column(nullable = false)
    private int quantidade;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusMatch status;

    @Builder.Default
    @Column(name = "criado_em", nullable = false)
    private ZonedDateTime criadoEm = ZonedDateTime.now();

    @Builder.Default
    @Column(name = "atualizado_em", nullable = false)
    private ZonedDateTime atualizadoEm = ZonedDateTime.now();
}