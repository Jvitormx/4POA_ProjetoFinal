package com._poadoacaolocalapp.doacaolocal_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._poadoacaolocalapp.doacaolocal_backend.dto.CreatePublicacaoDto;
import com._poadoacaolocalapp.doacaolocal_backend.dto.PublicacaoFeedDto;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Publicacao;
import com._poadoacaolocalapp.doacaolocal_backend.service.PublicacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/publicacoes")
public class PublicacaoController {

    private final PublicacaoService publicacaoService;

    public PublicacaoController(PublicacaoService publicacaoService) {
        this.publicacaoService = publicacaoService;
    }

    /** Cria uma nova oferta ou pedido */
    @PostMapping
    public ResponseEntity<Publicacao> criar(
        @RequestParam("usuarioId") UUID usuarioId,
        @Valid @RequestBody CreatePublicacaoDto dto
    ) {
        Publicacao criada = publicacaoService.criarPublicacao(
            usuarioId,
            dto
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(criada);
    }

    /** Retorna o feed de publicações próximas do tipo oposto ao informado */
    @GetMapping("/feed")
    public ResponseEntity<List<PublicacaoFeedDto>> feed(
        @RequestParam("usuarioId") UUID usuarioId,
        @RequestParam("tipo") String meuTipo
    ) {
        List<PublicacaoFeedDto> feed = publicacaoService.listarFeed(usuarioId, meuTipo);
        return ResponseEntity.ok(feed);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Publicacao> buscarPorId(@PathVariable UUID id) {
        Publicacao pub = publicacaoService.buscarPorId(id);
        if (pub == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pub);
    }
}