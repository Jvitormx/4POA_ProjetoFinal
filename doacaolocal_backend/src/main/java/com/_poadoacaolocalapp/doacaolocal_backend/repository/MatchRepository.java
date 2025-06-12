package com._poadoacaolocalapp.doacaolocal_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Match;
import com._poadoacaolocalapp.doacaolocal_backend.entity.StatusMatch;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findByStatus(StatusMatch status);

    @Query("""
        SELECT m FROM Match m
        WHERE m.usuario.id = :usuarioId OR m.publicacao.usuario.id = :usuarioId
    """)
    List<Match> findByUsuarioEnvolvido(@Param("usuarioId") UUID usuarioId);
}