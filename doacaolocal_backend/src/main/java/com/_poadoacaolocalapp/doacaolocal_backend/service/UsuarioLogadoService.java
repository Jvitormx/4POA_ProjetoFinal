package com._poadoacaolocalapp.doacaolocal_backend.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Usuario;

import jakarta.annotation.PostConstruct;

@Service
public class UsuarioLogadoService {

    private Usuario usuarioMock;

    @PostConstruct
    public void init() {
        usuarioMock = new Usuario();
        usuarioMock.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        usuarioMock.setNome("Usuário Fictício");
        usuarioMock.setEmail("mock@usuario.com");
        usuarioMock.setEhInstituicao(false);
        usuarioMock.setLatitudePadrao(-22.877451);
        usuarioMock.setLongitudePadrao(-43.294469);
    }

    public Usuario getUsuarioLogado() {
        return usuarioMock;
    }
}