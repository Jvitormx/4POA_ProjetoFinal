package com._poadoacaolocalapp.doacaolocal_backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com._poadoacaolocalapp.doacaolocal_backend.dto.CreatePublicacaoDto;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Publicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Usuario;
import com._poadoacaolocalapp.doacaolocal_backend.entity.enums.StatusPublicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.enums.TipoPublicacao;
import com._poadoacaolocalapp.doacaolocal_backend.repository.PublicacaoRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PublicacaoService {
    private final PublicacaoRepository pubRepo;
    private final UsuarioRepository userRepo;
    private final GeocodingService geocode;
    private final MatchService matchService;

    public PublicacaoService(PublicacaoRepository pubRepo,
                             UsuarioRepository userRepo,
                             GeocodingService geocode,
                             MatchService matchService) {
        this.pubRepo = pubRepo;
        this.userRepo = userRepo;
        this.geocode = geocode;
        this.matchService = matchService;
    }

    /** Cria uma nova oferta ou pedido, geocodifica endereço e dispara matching automático */
    @Transactional
    public Publicacao criarPublicacao(UUID usuarioId, CreatePublicacaoDto dto) {
        Usuario u = userRepo.findById(usuarioId)
              .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // 1) Geocoding
        GeocodingService.LatLng coords = geocode.geocode(dto.getEndereco());
        // 2) Monta entidade
        Publicacao p = Publicacao.builder()
              .usuario(u)
              .tipo(dto.getTipo())
              .titulo(dto.getTitulo())
              .descricao(dto.getDescricao())
              .categoria(dto.getCategoria())
              .quantidade(dto.getQuantidade())
              .quantidadeOriginal(dto.getQuantidade())
              .latitude(coords.lat())
              .longitude(coords.lng())
              .status(StatusPublicacao.ABERTA)
              .permiteEntrega(dto.getPermiteEntrega())
              .urgente(dto.getUrgente())
              .build();
        Publicacao salva = pubRepo.save(p);

        // 3) Trigger de matching inteligente
        matchService.autoMatch(salva);

        return salva;
    }

    /** Lista feed de publicações “ABERTAS” do tipo oposto, por proximidade */
    @Transactional
    public List<Publicacao> listarFeed(UUID usuarioId, TipoPublicacao meuTipo) {
        Usuario u = userRepo.findById(usuarioId)
              .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        // tipo de feed é o oposto (quem é OFERTA vê PEDIDOS e vice-versa)
        TipoPublicacao alvo = (meuTipo == TipoPublicacao.OFERTA)
                              ? TipoPublicacao.PEDIDO
                              : TipoPublicacao.OFERTA;
        return pubRepo.findNearby(
            alvo.name(),
            u.getLatitudePadrao(),
            u.getLongitudePadrao(),
            u.getRaioBuscaKm()
        );
    }
}