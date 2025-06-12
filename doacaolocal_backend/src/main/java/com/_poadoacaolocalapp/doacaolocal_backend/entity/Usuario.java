package com._poadoacaolocalapp.doacaolocal_backend.entity;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "hash_senha", nullable = false, length = 255)
    private String hashSenha;

    @Column(length = 20)
    private String telefone;

    @Builder.Default
    @Column(name = "eh_instituicao", nullable = false)
    private boolean ehInstituicao = false;

    @Column(name = "latitude_padrao")
    private Double latitudePadrao;

    @Column(name = "longitude_padrao")
    private Double longitudePadrao;

    @Builder.Default
    @Column(name = "raio_busca_km", nullable = false)
    private int raioBuscaKm = 5;

    @Builder.Default
    @Column(name = "criado_em", nullable = false)
    private ZonedDateTime criadoEm = ZonedDateTime.now();

    @Builder.Default
    @Column(name = "atualizado_em", nullable = false)
    private ZonedDateTime atualizadoEm = ZonedDateTime.now();

    @Column(name = "foto_perfil_url", length = 255)
    private String fotoPerfilUrl;

    @Column(columnDefinition = "TEXT")
    private String descricao;
}
