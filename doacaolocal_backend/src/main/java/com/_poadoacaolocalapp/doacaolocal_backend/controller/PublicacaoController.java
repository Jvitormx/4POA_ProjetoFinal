package com._poadoacaolocalapp.doacaolocal_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._poadoacaolocalapp.doacaolocal_backend.dto.CreatePublicacaoDto;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Publicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.enums.TipoPublicacao;
import com._poadoacaolocalapp.doacaolocal_backend.service.PublicacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/publicacoes")
public class PublicacaoController {

    private final PublicacaoService publicacaoService;

    // Por enquanto, usuário hardcoded:
    private static final UUID HARD_CODED_USER_ID =
        UUID.fromString("11111111-1111-1111-1111-111111111111");

    public PublicacaoController(PublicacaoService publicacaoService) {
        this.publicacaoService = publicacaoService;
    }

    /** Cria uma nova oferta ou pedido */
    @PostMapping
    public ResponseEntity<Publicacao> criar(
        @Valid @RequestBody CreatePublicacaoDto dto
    ) {
        Publicacao criada = publicacaoService.criarPublicacao(
            HARD_CODED_USER_ID,
            dto
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(criada);
    }

    /** Retorna o feed de publicações próximas do tipo oposto ao informado */
    @GetMapping("/feed")
    public ResponseEntity<List<Publicacao>> feed(
        @RequestParam("tipo") TipoPublicacao meuTipo
    ) {
        List<Publicacao> feed = publicacaoService.listarFeed(
            HARD_CODED_USER_ID,
            meuTipo
        );
        return ResponseEntity.ok(feed);
    }
}
