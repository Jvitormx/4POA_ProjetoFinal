package com._poadoacaolocalapp.doacaolocal_backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com._poadoacaolocalapp.doacaolocal_backend.entity.Match;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Publicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.enums.StatusMatch;
import com._poadoacaolocalapp.doacaolocal_backend.entity.enums.TipoPublicacao;
import com._poadoacaolocalapp.doacaolocal_backend.repository.MatchRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.PublicacaoRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class MatchService {
    private final MatchRepository matchRepo;
    private final PublicacaoRepository pubRepo;
    private final NotificacaoService notifier;

    public MatchService(MatchRepository matchRepo,
                        PublicacaoRepository pubRepo,
                        NotificacaoService notifier) {
        this.matchRepo = matchRepo;
        this.pubRepo = pubRepo;
        this.notifier = notifier;
    }

    @Transactional
    public Match criarMatchManual(UUID publicacaoId, UUID solicitanteId, int quantidade) {
        Publicacao alvo = pubRepo.findById(publicacaoId)
            .orElseThrow(() -> new EntityNotFoundException("Publicação não existe"));

        Match m = new Match();
        if (alvo.getTipo() == TipoPublicacao.OFERTA) {
            m.setOferta(alvo);
            // pedido ficará null; quem solicitou vira “pedido implícito”
        } else {
            m.setPedido(alvo);
        }
        m.setQuantidade(quantidade);
        m.setStatus(StatusMatch.PENDENTE);
        Match salvo = matchRepo.save(m);

        // Notifica o dono da publicação alvo
        UUID dono = (alvo.getTipo() == TipoPublicacao.OFERTA
                    ? alvo.getUsuario().getId()
                    : alvo.getUsuario().getId());
        notifier.notifyNewMatch(salvo.getId(), solicitanteId, dono);
        return salvo;
    }

    @Transactional
    public Match atualizarStatus(UUID matchId, StatusMatch novoStatus) {
        Match m = matchRepo.findById(matchId)
               .orElseThrow(() -> new EntityNotFoundException("Match não encontrado"));
        m.setStatus(novoStatus);
        Match salvo = matchRepo.save(m);
        return salvo;
    }

    @Transactional
    public void autoMatch(Publicacao p) {
        TipoPublicacao meuTipo = p.getTipo();
        TipoPublicacao alvo = (meuTipo == TipoPublicacao.OFERTA)
                              ? TipoPublicacao.PEDIDO
                              : TipoPublicacao.OFERTA;

        List<Publicacao> contrapartes = pubRepo.findNearby(
            alvo.name(),
            p.getLatitude(),
            p.getLongitude(),
            p.getUsuario().getRaioBuscaKm()
        ).stream()
         .filter(o -> o.getCategoria().equalsIgnoreCase(p.getCategoria()))
         .toList();

        int restante = p.getQuantidade();
        for (Publicacao contra : contrapartes) {
            if (restante <= 0) break;
            int dispo = contra.getQuantidade();
            int qtd = Math.min(restante, dispo);

            Match m = new Match();
            if (meuTipo == TipoPublicacao.OFERTA) {
                m.setOferta(p);
                m.setPedido(contra);
            } else {
                m.setPedido(p);
                m.setOferta(contra);
            }
            m.setQuantidade(qtd);
            m.setStatus(StatusMatch.PENDENTE);  // agora aguardará confirmação
            Match salvo = matchRepo.save(m);

            // Notifica os dois usuários do match inteligente
            notifier.notifyNewMatch(salvo.getId(),
                                    p.getUsuario().getId(),
                                    contra.getUsuario().getId());

            restante -= qtd;
        }
    }

    public List<Match> listAll() {
        return matchRepo.findAll();
    }
}