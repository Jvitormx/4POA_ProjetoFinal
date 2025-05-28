package com._poadoacaolocalapp.doacaolocal_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Mensagem;

public interface MensagemRepository extends JpaRepository<Mensagem, UUID> {
    List<Mensagem> findByMatchIdOrderByIdAsc(UUID matchId);
}