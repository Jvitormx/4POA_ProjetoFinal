package com._poadoacaolocalapp.doacaolocal_backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._poadoacaolocalapp.doacaolocal_backend.dto.CreateMatchDto;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Match;
import com._poadoacaolocalapp.doacaolocal_backend.service.MatchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    /** Iniciação manual de match: usuário solicita uma publicação */
    @PostMapping("/manual")
    public ResponseEntity<Match> criarManual(
        @Valid @RequestBody CreateMatchDto dto
    ) {
        Match m = matchService.criarMatchManual(
            dto.getPublicacaoId(),
            dto.getSolicitanteId(), 
            dto.getQuantidade()
        );
        return ResponseEntity.ok(m);
    }

    /** Atualiza o status de um match (confirmar, recusar, cancelar) */
    @PostMapping("/{id}/status")
    public ResponseEntity<Match> atualizaStatus(
        @PathVariable("id") UUID matchId,
        @RequestParam("status") String novoStatus // Agora recebe o nome do status como String
    ) {
        Match atualizado = matchService.atualizarStatus(matchId, novoStatus);
        return ResponseEntity.ok(atualizado);
    }

    /** Lista todos os matches (útil para testes) */
    @GetMapping
    public ResponseEntity<List<Match>> listarTodos() {
        List<Match> lista = matchService.listAll();
        return ResponseEntity.ok(lista);
    }

    /** Lista os matches de um usuário específico */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Match>> listarPorUsuario(@PathVariable UUID usuarioId) {
        List<Match> matches = matchService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(matches);
    }
}
