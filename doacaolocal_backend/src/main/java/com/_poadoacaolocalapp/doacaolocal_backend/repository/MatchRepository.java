package com._poadoacaolocalapp.doacaolocal_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Match;
import com._poadoacaolocalapp.doacaolocal_backend.entity.enums.StatusMatch;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findByOfertaId(UUID ofertaId);
    List<Match> findByPedidoId(UUID pedidoId);
    List<Match> findByStatus(StatusMatch status);
}