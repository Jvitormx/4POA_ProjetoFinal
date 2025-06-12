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

    private String fotoPerfilUrl; // Novo campo

    private final Boolean ehInstituicao = false;

    private String descricao;

    // Getters e setters...

    public Usuario toUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome(this.nome);
        usuario.setEmail(this.email);
        usuario.setEhInstituicao(this.ehInstituicao);
        usuario.setFotoPerfilUrl(this.fotoPerfilUrl); // Novo campo
        usuario.setHashSenha(new BCryptPasswordEncoder().encode(this.senha));
        usuario.setDescricao(this.descricao);
        return usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }

    public void setFotoPerfilUrl(String fotoPerfilUrl) {
        this.fotoPerfilUrl = fotoPerfilUrl;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}