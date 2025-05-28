package com._poadoacaolocalapp.doacaolocal_backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
}