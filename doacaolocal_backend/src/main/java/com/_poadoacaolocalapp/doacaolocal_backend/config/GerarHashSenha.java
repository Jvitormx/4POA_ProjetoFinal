package com._poadoacaolocalapp.doacaolocal_backend.config;

// Adicione este método em qualquer classe Java do seu backend, execute e veja o hash no console
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GerarHashSenha {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("1234"); // Troque "1234" pela senha desejada
        System.out.println("aqui: " + hash);
    }
}