package com._poadoacaolocalapp.doacaolocal_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com._poadoacaolocalapp.doacaolocal_backend.entity.TipoPublicacao;

public interface TipoPublicacaoRepository extends JpaRepository<TipoPublicacao, Integer> {
    Optional<TipoPublicacao> findByNome(String nome);
}
