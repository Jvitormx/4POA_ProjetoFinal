package com._poadoacaolocalapp.doacaolocal_backend.dto;

import java.time.Instant;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePublicacaoDto {

    @NotNull
    private String tipo; // Agora recebe o nome do tipo ("OFERTA" ou "PEDIDO")

    @NotBlank
    @Size(max = 150)
    private String titulo;

    private String descricao;

    @NotBlank
    @Size(max = 50)
    private String categoria;

    @NotNull
    @Min(1)
    private Integer quantidade;

    // Se for OFERTA
    private Instant inicioColeta;
    private Instant fimColeta;

    // Endereço textual (usado pelo GeocodingService)
    private String endereco;

    // Ou lat/lng direto (caso você escolha receber coords do front)
    private Double latitude;
    private Double longitude;

    private Boolean permiteEntrega = false;
    private Boolean urgente = false;

    // Getters e setters

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Boolean getPermiteEntrega() {
        return permiteEntrega;
    }

    public void setPermiteEntrega(Boolean permiteEntrega) {
        this.permiteEntrega = permiteEntrega;
    }

    public Boolean getUrgente() {
        return urgente;
    }

    public void setUrgente(Boolean urgente) {
        this.urgente = urgente;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Instant getInicioColeta() {
        return inicioColeta;
    }

    public void setInicioColeta(Instant inicioColeta) {
        this.inicioColeta = inicioColeta;
    }

    public Instant getFimColeta() {
        return fimColeta;
    }

    public void setFimColeta(Instant fimColeta) {
        this.fimColeta = fimColeta;
    }
}