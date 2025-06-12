package com._poadoacaolocalapp.doacaolocal_backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {
    @Override
    public void notifyNewMatch(UUID matchId, UUID usuario1, UUID usuario2) {
        // Implementação simples (pode ser só um log por enquanto)
        System.out.println("Novo match: " + matchId + " entre " + usuario1 + " e " + usuario2);
    }
}