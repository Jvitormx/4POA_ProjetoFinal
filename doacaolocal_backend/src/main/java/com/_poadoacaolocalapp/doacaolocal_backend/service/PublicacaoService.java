package com._poadoacaolocalapp.doacaolocal_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com._poadoacaolocalapp.doacaolocal_backend.dto.CreatePublicacaoDto;
import com._poadoacaolocalapp.doacaolocal_backend.dto.PublicacaoFeedDto;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Publicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.StatusPublicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.TipoPublicacao;
import com._poadoacaolocalapp.doacaolocal_backend.entity.Usuario;
import com._poadoacaolocalapp.doacaolocal_backend.repository.PublicacaoRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.StatusPublicacaoRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.TipoPublicacaoRepository;
import com._poadoacaolocalapp.doacaolocal_backend.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PublicacaoService {
    private final PublicacaoRepository pubRepo;
    private final UsuarioRepository userRepo;
    private final GeocodingService geocode;
    private final MatchService matchService;
    private final TipoPublicacaoRepository tipoRepo;
    private final StatusPublicacaoRepository statusRepo;

    public PublicacaoService(PublicacaoRepository pubRepo,
                             UsuarioRepository userRepo,
                             GeocodingService geocode,
                             MatchService matchService,
                             TipoPublicacaoRepository tipoRepo,
                             StatusPublicacaoRepository statusRepo) {
        this.pubRepo = pubRepo;
        this.userRepo = userRepo;
        this.geocode = geocode;
        this.matchService = matchService;
        this.tipoRepo = tipoRepo;
        this.statusRepo = statusRepo;
    }

    /** Cria uma nova oferta ou pedido, geocodifica endereço e dispara matching automático */
    @Transactional
    public Publicacao criarPublicacao(UUID usuarioId, CreatePublicacaoDto dto) {
        Usuario u = userRepo.findById(usuarioId)
              .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        TipoPublicacao tipo = tipoRepo.findByNome(dto.getTipo())
              .orElseThrow(() -> new IllegalArgumentException("Tipo inválido"));

        StatusPublicacao status = statusRepo.findByNome("ABERTA")
              .orElseThrow(() -> new IllegalArgumentException("Status não encontrado"));

        double lat, lng;
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            lat = dto.getLatitude();
            lng = dto.getLongitude();
        } else {
            GeocodingService.LatLng coords = geocode.geocode(dto.getEndereco());
            lat = coords.lat();
            lng = coords.lng();
        }

        Publicacao p = Publicacao.builder()
              .usuario(u)
              .tipo(tipo)
              .status(status)
              .titulo(dto.getTitulo())
              .descricao(dto.getDescricao())
              .categoria(dto.getCategoria())
              .quantidade(dto.getQuantidade())
              .quantidadeOriginal(dto.getQuantidade())
              .latitude(lat)
              .longitude(lng)
              .permiteEntrega(dto.getPermiteEntrega())
              .urgente(dto.getUrgente())
              .inicioColeta(dto.getInicioColeta() != null ? dto.getInicioColeta().atZone(java.time.ZoneId.systemDefault()) : null)
              .fimColeta(dto.getFimColeta() != null ? dto.getFimColeta().atZone(java.time.ZoneId.systemDefault()) : null)
              .build();

        Publicacao salva = pubRepo.save(p);
        matchService.autoMatch(salva);
        return salva;
    }

    /** Lista feed de publicações “ABERTAS” do tipo oposto, por proximidade */
    @Transactional
    public List<PublicacaoFeedDto> listarFeed(UUID usuarioId, String meuTipoNome) {
        Usuario u = userRepo.findById(usuarioId)
              .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Busca o tipo oposto
        String alvoNome;
        if ("OFERTA".equalsIgnoreCase(meuTipoNome)) {
            alvoNome = "PEDIDO";
        } else if ("PEDIDO".equalsIgnoreCase(meuTipoNome)) {
            alvoNome = "OFERTA";
        } else {
            throw new IllegalArgumentException("Tipo inválido: " + meuTipoNome);
        }

        TipoPublicacao alvo = tipoRepo.findByNome(alvoNome)
              .orElseThrow(() -> new IllegalArgumentException("Tipo de publicação não encontrado: " + alvoNome));

        List<Object[]> rows = pubRepo.findNearbyWithDistancia(
            alvo.getId(),
            u.getLatitudePadrao(),
            u.getLongitudePadrao(),
            u.getRaioBuscaKm() * 1000.0
        );
        List<PublicacaoFeedDto> dtos = new ArrayList<>();
        for (Object[] row : rows) {
            PublicacaoFeedDto dto = new PublicacaoFeedDto();
            dto.setId((UUID) row[0]);
            dto.setTitulo((String) row[1]);
            dto.setDescricao((String) row[2]);
            dto.setCategoria((String) row[3]);
            dto.setQuantidade((Integer) row[4]);
            dto.setDistancia(((Number) row[5]).doubleValue());
            PublicacaoFeedDto.UsuarioResumoDto usuario = new PublicacaoFeedDto.UsuarioResumoDto();
            usuario.setId((UUID) row[6]);
            usuario.setNome((String) row[7]);
            usuario.setFotoPerfilUrl((String) row[8]);
            dto.setUsuario(usuario);
            dtos.add(dto);
        }
        return dtos;
    }

    public Publicacao buscarPorId(UUID id) {
        return pubRepo.findById(id).orElse(null);
    }
}