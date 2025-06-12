package com._poadoacaolocalapp.doacaolocal_backend.service;

import java.util.UUID;

public interface NotificacaoService {
    /**
     * Notifica os dois usuários que houve um novo match entre as publicações fornecidas.
     */
    void notifyNewMatch(UUID matchId, UUID usuario1, UUID usuario2);

    /**
     * Sugere um match entre duas publicações para dois usuários.
     * 
     * @param pub1Id  ID da primeira publicação.
     * @param user1Id ID do usuário da primeira publicação.
     * @param pub2Id  ID da segunda publicação.
     * @param user2Id ID do usuário da segunda publicação.
     */
    default void notifyNewMatchSuggestion(UUID pub1Id, UUID user1Id, UUID pub2Id, UUID user2Id) {
        // Implementação básica: apenas loga no console
        System.out.printf("Sugestão de match: pub1=%s (user1=%s), pub2=%s (user2=%s)%n", pub1Id, user1Id, pub2Id, user2Id);
    }
}
