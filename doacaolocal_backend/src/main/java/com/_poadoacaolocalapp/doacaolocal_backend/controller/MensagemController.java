package com._poadoacaolocalapp.doacaolocal_backend.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Mensagem;
import com._poadoacaolocalapp.doacaolocal_backend.repository.MatchRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.MensagemRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/mensagens")
public class MensagemController {
    private final MensagemRepository mensagemRepo;
    private final MatchRepository matchRepo;
    private final UsuarioRepository usuarioRepo;

    public MensagemController(MensagemRepository mensagemRepo, MatchRepository matchRepo, UsuarioRepository usuarioRepo) {
        this.mensagemRepo = mensagemRepo;
        this.matchRepo = matchRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @PostMapping
    public Mensagem criar(@RequestBody Map<String, Object> payload) {
        UUID matchId = UUID.fromString(payload.get("matchId").toString());
        UUID remetenteId = UUID.fromString(payload.get("remetenteId").toString());
        String conteudo = payload.get("conteudo").toString();

        Mensagem m = new Mensagem();
        m.setMatch(matchRepo.findById(matchId).orElseThrow());
        m.setRemetente(usuarioRepo.findById(remetenteId).orElseThrow());
        m.setConteudo(conteudo);
        m.setEnviadoEm(java.time.ZonedDateTime.now());
        return mensagemRepo.save(m);
    }

    @GetMapping
    public List<Mensagem> listarPorMatch(@RequestParam("matchId") UUID matchId) {
        return mensagemRepo.findByMatchIdOrderByEnviadoEmAsc(matchId);
    }
}
