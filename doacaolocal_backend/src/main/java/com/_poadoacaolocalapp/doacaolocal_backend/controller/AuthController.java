package com._poadoacaolocalapp.doacaolocal_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com._poadoacaolocalapp.doacaolocal_backend.dto.LoginDto;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Usuario;
import com._poadoacaolocalapp.doacaolocal_backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginDto dto) {
        Usuario usuario = authService.login(dto);
        return ResponseEntity.ok(usuario);
    }
}