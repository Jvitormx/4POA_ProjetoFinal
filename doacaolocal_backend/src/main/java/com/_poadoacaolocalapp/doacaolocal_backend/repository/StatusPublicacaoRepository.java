package com._poadoacaolocalapp.doacaolocal_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com._poadoacaolocalapp.doacaolocal_backend.entity.StatusPublicacao;

public interface StatusPublicacaoRepository extends JpaRepository<StatusPublicacao, Integer> {
    Optional<StatusPublicacao> findByNome(String nome);
}
