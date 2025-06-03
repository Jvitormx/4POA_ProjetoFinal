package com._poadoacaolocalapp.doacaolocal_backend.dto;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUsuarioDto {
    @NotBlank
    private String nome;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String senha;

    private final Boolean ehInstituicao = false;

    // Getters e setters...

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome(this.nome);
        usuario.setEmail(this.email);
        usuario.setEhInstituicao(this.ehInstituicao);
        usuario.setHashSenha(new BCryptPasswordEncoder().encode(this.senha));
        return usuario;
    }
}