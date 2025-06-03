package com._poadoacaolocalapp.doacaolocal_backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com._poadoacaolocalapp.doacaolocal_backend.dto.LoginDto;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Usuario;
import com._poadoacaolocalapp.doacaolocal_backend.repository.UsuarioRepository;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario login(LoginDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        if (!encoder.matches(dto.getSenha(), usuario.getHashSenha())) {
            throw new IllegalArgumentException("Senha inválida");
        }
        return usuario;
    }
}