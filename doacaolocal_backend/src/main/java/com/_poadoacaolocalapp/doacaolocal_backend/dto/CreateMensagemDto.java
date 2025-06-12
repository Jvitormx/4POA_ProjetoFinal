package com._poadoacaolocalapp.doacaolocal_backend.dto;

import java.util.UUID;

public class CreateMensagemDto {
    private UUID matchId;
    private UUID remetenteId;
    private String conteudo;

    public UUID getMatchId() {
        return matchId;
    }

    public void setMatchId(UUID matchId) {
        this.matchId = matchId;
    }

    public UUID getRemetenteId() {
        return remetenteId;
    }

    public void setRemetenteId(UUID remetenteId) {
        this.remetenteId = remetenteId;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}
