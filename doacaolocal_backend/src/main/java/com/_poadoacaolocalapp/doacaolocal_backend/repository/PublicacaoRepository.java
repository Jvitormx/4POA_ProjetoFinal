package com._poadoacaolocalapp.doacaolocal_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Publicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.enums.StatusPublicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.enums.TipoPublicacao;

public interface PublicacaoRepository extends JpaRepository<Publicacao, UUID> {
    List<Publicacao> findByTipo(TipoPublicacao tipo);
    List<Publicacao> findByUsuarioId(UUID usuarioId);
    List<Publicacao> findByStatus(StatusPublicacao status);

    // Listar todas as publicações de um tipo e status
    List<Publicacao> findByTipoAndStatus(TipoPublicacao tipo, StatusPublicacao status);

    // Matching geoespacial: retorna apenas publicações ABERTAS dentro do raio
    @Query(value = """
        SELECT p.* FROM publicacoes p
        WHERE p.status = 'ABERTA'
          AND p.tipo = :tipo
          AND ll_to_earth(p.latitude, p.longitude)
              <@> ll_to_earth(:lat, :lng) <= :radius
        ORDER BY ll_to_earth(p.latitude, p.longitude)
              <@> ll_to_earth(:lat, :lng)
        """, nativeQuery = true)
    List<Publicacao> findNearby(
        @Param("tipo") String tipo,
        @Param("lat") double latitude,
        @Param("lng") double longitude,
        @Param("radius") double radiusKm
    );
}