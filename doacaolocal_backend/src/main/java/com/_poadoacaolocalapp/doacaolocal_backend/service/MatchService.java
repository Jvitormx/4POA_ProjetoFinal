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

    public MatchService(MatchRepository matchRepo,
                        PublicacaoRepository pubRepo) {
        this.matchRepo = matchRepo;
        this.pubRepo = pubRepo;
    }

    /** Matching manual: usuário seleciona uma publicação e inicia um match */
    @Transactional
    public Match criarMatchManual(UUID publicacaoId, UUID solicitanteId, int quantidade) {
        Publicacao alvo = pubRepo.findById(publicacaoId)
            .orElseThrow(() -> new EntityNotFoundException("Publicação não existe"));
        // monta o Match de acordo com o tipo da publicação
        Match m = new Match();
        if (alvo.getTipo() == TipoPublicacao.OFERTA) {
            m.setOferta(alvo);
        } else {
            m.setPedido(alvo);
        }
        m.setQuantidade(quantidade);
        m.setStatus(StatusMatch.PENDENTE);
        return matchRepo.save(m);
    }

    /** Confirma ou recusa um match (atualiza status e deixa os triggers do BD atualizarem quantidades) */
    @Transactional
    public Match atualizarStatus(UUID matchId, StatusMatch novoStatus) {
        Match m = matchRepo.findById(matchId)
               .orElseThrow(() -> new EntityNotFoundException("Match não encontrado"));
        m.setStatus(novoStatus);
        return matchRepo.save(m);
    }

    /** Algoritmo de auto-match simplificado:
     *  - Para uma nova oferta: busca pedidos abertos na mesma categoria
     *  - Para um novo pedido: busca ofertas abertas...
     *  - Cria matches confirmados até consumir totalmente uma das partes
     */
    @Transactional
    public void autoMatch(Publicacao p) {
        TipoPublicacao meuTipo = p.getTipo();
        TipoPublicacao alvo = (meuTipo == TipoPublicacao.OFERTA)
                              ? TipoPublicacao.PEDIDO
                              : TipoPublicacao.OFERTA;

        // 1) busca candidatas
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
            int disponivel = contra.getQuantidade();
            int qtd = Math.min(restante, disponivel);
            // 2) cria match já confirmado
            Match m = new Match();
            if (meuTipo == TipoPublicacao.OFERTA) {
                m.setOferta(p);
                m.setPedido(contra);
            } else {
                m.setPedido(p);
                m.setOferta(contra);
            }
            m.setQuantidade(qtd);
            m.setStatus(StatusMatch.CONFIRMADO);
            matchRepo.save(m);
            restante -= qtd;
        }
    }
}