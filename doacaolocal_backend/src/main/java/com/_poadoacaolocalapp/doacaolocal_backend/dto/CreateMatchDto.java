package com._poadoacaolocalapp.doacaolocal_backend.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateMatchDto {

    @NotNull(message = "O ID da publicação é obrigatório")
    private UUID publicacaoId;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private Integer quantidade;

    public UUID getPublicacaoId() {
        return publicacaoId;
    }

    public void setPublicacaoId(UUID publicacaoId) {
        this.publicacaoId = publicacaoId;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
