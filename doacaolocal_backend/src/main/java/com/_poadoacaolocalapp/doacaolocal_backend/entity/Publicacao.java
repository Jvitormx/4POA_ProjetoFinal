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
@Table(name = "publicacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publicacao {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tipo_id", nullable = false)
    private TipoPublicacao tipo;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private StatusPublicacao status;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false)
    private int quantidade;

    @Column(name = "quantidade_original", nullable = false)
    private int quantidadeOriginal;

    @Column(name = "inicio_coleta")
    private ZonedDateTime inicioColeta;

    @Column(name = "fim_coleta")
    private ZonedDateTime fimColeta;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Builder.Default
    @Column(name = "permite_entrega", nullable = false)
    private boolean permiteEntrega = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean urgente = false;

    @Builder.Default
    @Column(name = "criado_em", nullable = false)
    private ZonedDateTime criadoEm = ZonedDateTime.now();

    @Builder.Default
    @Column(name = "atualizado_em", nullable = false)
    private ZonedDateTime atualizadoEm = ZonedDateTime.now();
}