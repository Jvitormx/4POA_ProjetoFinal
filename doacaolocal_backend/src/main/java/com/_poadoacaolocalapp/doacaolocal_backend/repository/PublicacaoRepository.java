package com._poadoacaolocalapp.doacaolocal_backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Publicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.StatusPublicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.TipoPublicacao;

public interface PublicacaoRepository extends JpaRepository<Publicacao, UUID> {
    List<Publicacao> findByTipo(TipoPublicacao tipo);
    List<Publicacao> findByUsuarioId(UUID usuarioId);
    List<Publicacao> findByStatus(StatusPublicacao status);

    // Listar todas as publicações de um tipo e status
    List<Publicacao> findByTipoAndStatus(TipoPublicacao tipo, StatusPublicacao status);

    // Matching geoespacial: retorna apenas publicações ABERTAS dentro do raio
    @Query(value = """
        SELECT p.* FROM publicacoes p
        WHERE p.status_id = (SELECT id FROM status_publicacao WHERE nome = 'ABERTA')
          AND p.tipo_id = :tipo
          AND ll_to_earth(p.latitude, p.longitude)
              <-> ll_to_earth(:lat, :lng) <= :radius
        ORDER BY ll_to_earth(p.latitude, p.longitude)
              <-> ll_to_earth(:lat, :lng)
        """, nativeQuery = true)
    List<Publicacao> findNearby(
        @Param("tipo") Integer tipo,
        @Param("lat") double latitude,
        @Param("lng") double longitude,
        @Param("radius") double radiusMeters // <-- veja observação abaixo
    );

    @Query(value = """
        SELECT p.id, p.titulo, p.descricao, p.categoria, p.quantidade,
               (ll_to_earth(p.latitude, p.longitude) <-> ll_to_earth(:lat, :lng)) / 1000.0 AS distancia,
               u.id as usuario_id, u.nome as usuario_nome, u.foto_perfil_url as usuario_foto
        FROM publicacoes p
        JOIN usuarios u ON u.id = p.usuario_id
        WHERE p.status_id = (SELECT id FROM status_publicacao WHERE nome = 'ABERTA')
          AND p.tipo_id = :tipo
          AND ll_to_earth(p.latitude, p.longitude) <-> ll_to_earth(:lat, :lng) <= :radius
        ORDER BY distancia
        """, nativeQuery = true)
    List<Object[]> findNearbyWithDistancia(
        @Param("tipo") Integer tipo,
        @Param("lat") double latitude,
        @Param("lng") double longitude,
        @Param("radius") double radiusMeters
    );
}