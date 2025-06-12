package com._poadoacaolocalapp.doacaolocal_backend.dto;

import java.util.UUID;

public class PublicacaoFeedDto {
    private UUID id;
    private String titulo;
    private String descricao;
    private String categoria;
    private int quantidade;
    private double distancia;
    private UsuarioResumoDto usuario;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public UsuarioResumoDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioResumoDto usuario) {
        this.usuario = usuario;
    }

    public static class UsuarioResumoDto {
        private UUID id;
        private String nome;
        private String fotoPerfilUrl;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getFotoPerfilUrl() {
            return fotoPerfilUrl;
        }

        public void setFotoPerfilUrl(String fotoPerfilUrl) {
            this.fotoPerfilUrl = fotoPerfilUrl;
        }
    }
}