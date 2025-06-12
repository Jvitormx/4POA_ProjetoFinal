package com._poadoacaolocalapp.doacaolocal_backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Match;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Publicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.StatusMatch;
import com._poadoacaolocalapp.doacaolocal_backend.entity.TipoPublicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Usuario;
import com._poadoacaolocalapp.doacaolocal_backend.repository.MatchRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.PublicacaoRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.StatusMatchRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.TipoPublicacaoRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class MatchService {
    private final MatchRepository matchRepo;
    private final PublicacaoRepository pubRepo;
    private final NotificacaoService notifier;
    private final StatusMatchRepository statusMatchRepo;
    private final TipoPublicacaoRepository tipoPublicacaoRepo;
    private final UsuarioRepository userRepo;

    public MatchService(
            MatchRepository matchRepo,
            PublicacaoRepository pubRepo,
            NotificacaoService notifier,
            StatusMatchRepository statusMatchRepo,
            TipoPublicacaoRepository tipoPublicacaoRepo,
            UsuarioRepository userRepo) {
        this.matchRepo = matchRepo;
        this.pubRepo = pubRepo;
        this.notifier = notifier;
        this.statusMatchRepo = statusMatchRepo;
        this.tipoPublicacaoRepo = tipoPublicacaoRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public Match criarMatchManual(UUID publicacaoId, UUID solicitanteId, int quantidade) {
        Publicacao pub = pubRepo.findById(publicacaoId)
                .orElseThrow(() -> new EntityNotFoundException("Publicação não existe"));
        Usuario solicitante = userRepo.findById(solicitanteId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não existe"));

        Match m = new Match();
        m.setPublicacao(pub);
        m.setUsuario(solicitante);
        m.setQuantidade(quantidade);

        StatusMatch pendente = statusMatchRepo.findByNome("PENDENTE")
                .orElseThrow(() -> new IllegalArgumentException("StatusMatch PENDENTE não encontrado"));
        m.setStatus(pendente);

        Match salvo = matchRepo.save(m);

        notifier.notifyNewMatch(salvo.getId(), solicitanteId, pub.getUsuario().getId());
        return salvo;
    }

    @Transactional
    public Match atualizarStatus(UUID matchId, String novoStatusNome) {
        Match m = matchRepo.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match não encontrado"));
        StatusMatch novoStatus = statusMatchRepo.findByNome(novoStatusNome)
                .orElseThrow(() -> new IllegalArgumentException("StatusMatch não encontrado: " + novoStatusNome));
        m.setStatus(novoStatus);
        return matchRepo.save(m);
    }

    @Transactional
    public void autoMatch(Publicacao p) {
        String meuTipoNome = p.getTipo().getNome();
        String alvoNome = "OFERTA".equalsIgnoreCase(meuTipoNome) ? "PEDIDO" : "OFERTA";
        TipoPublicacao alvoTipo = tipoPublicacaoRepo.findByNome(alvoNome)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de publicação não encontrado: " + alvoNome));

        List<Publicacao> contrapartes = pubRepo.findNearby(
                alvoTipo.getId(),
                p.getLatitude(),
                p.getLongitude(),
                p.getUsuario().getRaioBuscaKm()).stream()
                .filter(o -> o.getCategoria().equalsIgnoreCase(p.getCategoria()))
                .toList();

        for (Publicacao contra : contrapartes) {
            notifier.notifyNewMatchSuggestion(
                    p.getId(), p.getUsuario().getId(),
                    contra.getId(), contra.getUsuario().getId());
        }
    }

    public List<Match> listAll() {
        return matchRepo.findAll();
    }

    public List<Match> listarPorUsuario(UUID usuarioId) {
        return matchRepo.findByUsuarioEnvolvido(usuarioId);
    }
}