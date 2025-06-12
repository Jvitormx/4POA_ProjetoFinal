package com._poadoacaolocalapp.doacaolocal_backend.service;

import org.springframework.stereotype.Service;

import com._poadoacaolocalapp.doacaolocal_backend.dto.LoginDto;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Usuario;
import com._poadoacaolocalapp.doacaolocal_backend.repository.UsuarioRepository;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario login(LoginDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        // Comparação simples, sem BCrypt
        if (!dto.getSenha().equals(usuario.getHashSenha())) {
            throw new IllegalArgumentException("Senha inválida");
        }
        return usuario;
    }
}