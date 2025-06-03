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
import com._poadoacaolocalapp.doacaolocal_backend.entity.enums.StatusMatch;
import com._poadoacaolocalapp.doacaolocal_backend.service.MatchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    // Mesmo usuário hardcoded para solicitante
    private static final UUID HARD_CODED_USER_ID =
        UUID.fromString("11111111-1111-1111-1111-111111111111");

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
            HARD_CODED_USER_ID,
            dto.getQuantidade()
        );
        return ResponseEntity.ok(m);
    }

    /** Atualiza o status de um match (confirmar, recusar, cancelar) */
    @PostMapping("/{id}/status")
    public ResponseEntity<Match> atualizaStatus(
        @PathVariable("id") UUID matchId,
        @RequestParam("status") StatusMatch novoStatus
    ) {
        Match atualizado = matchService.atualizarStatus(matchId, novoStatus);
        return ResponseEntity.ok(atualizado);
    }

    /** Lista todos os matches (útil para testes) */
    @GetMapping
    public ResponseEntity<List<Match>> listarTodos() {
        List<Match> lista = matchService.listAll(); // implemente este método no service
        return ResponseEntity.ok(lista);
    }
}
