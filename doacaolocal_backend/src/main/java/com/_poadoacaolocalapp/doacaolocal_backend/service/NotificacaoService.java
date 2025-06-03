package com._poadoacaolocalapp.doacaolocal_backend.service;

import java.util.UUID;

public interface NotificacaoService {
    /**
     * Notifica os dois usuários que houve um novo match entre as publicações fornecidas.
     */
    void notifyNewMatch(UUID matchId, UUID usuario1, UUID usuario2);
}
