package com.emma.emmamusic.domain.favoritos.service.impl;

import com.emma.emmamusic.domain.auth.model.Usuario;
import com.emma.emmamusic.domain.auth.repository.UsuarioRepository;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import com.emma.emmamusic.domain.buscar.repository.MetadatoYoutubeRepository;
import com.emma.emmamusic.domain.favoritos.dto.FavoritoDTO;
import com.emma.emmamusic.domain.favoritos.model.FavoritoMetadato;
import com.emma.emmamusic.domain.favoritos.repository.FavoritoMetadatoRepository;
import com.emma.emmamusic.domain.favoritos.service.FavoritoService;
import com.emma.emmamusic.exception.ResourceConflictException;
import com.emma.emmamusic.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoritoServiceImpl implements FavoritoService {

    private final FavoritoMetadatoRepository favoritoMetadatoRepository;
    private final MetadatoYoutubeRepository metadatoYoutubeRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public FavoritoDTO agregarFavorito(Long usuarioId, String videoId) {
        log.info("Agregando favorito: usuario={}, videoId={}", usuarioId, videoId);

        if (favoritoMetadatoRepository.existsByUsuarioIdAndMetadatoIdVideoYoutube(usuarioId, videoId)) {
            throw new ResourceConflictException("La canción ya está en favoritos");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        MetadatoYoutube metadato = metadatoYoutubeRepository.findByIdVideoYoutube(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Metadato", "videoId", videoId));

        FavoritoMetadato.FavoritoMetadatoId id = new FavoritoMetadato.FavoritoMetadatoId(
                usuarioId,
                metadato.getMetadatoId()
        );

        FavoritoMetadato favorito = FavoritoMetadato.builder()
                .id(id)
                .usuario(usuario)
                .metadatoYoutube(metadato)
                .build();

        favoritoMetadatoRepository.save(favorito);

        metadato.setContadorAgregadasPlaylists(metadato.getContadorAgregadasPlaylists() + 1);
        metadato.setUltimaActividad(Instant.now());
        metadatoYoutubeRepository.save(metadato);

        log.info("Favorito agregado exitosamente");
        return convertirADTO(metadato, favorito.getFechaAdicion());
    }

    @Override
    @Transactional
    public void eliminarFavorito(Long usuarioId, String videoId) {
        log.info("Eliminando favorito: usuario={}, videoId={}", usuarioId, videoId);

        FavoritoMetadato favorito = favoritoMetadatoRepository
                .findByUsuarioIdAndVideoId(usuarioId, videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito no encontrado"));

        favoritoMetadatoRepository.delete(favorito);

        MetadatoYoutube metadato = favorito.getMetadatoYoutube();
        if (metadato.getContadorAgregadasPlaylists() > 0) {
            metadato.setContadorAgregadasPlaylists(metadato.getContadorAgregadasPlaylists() - 1);
            metadato.setUltimaActividad(Instant.now());
            metadatoYoutubeRepository.save(metadato);
        }

        log.info("Favorito eliminado exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoritoDTO> obtenerFavoritosPorUsuario(Long usuarioId) {
        log.info("Obteniendo favoritos para usuario: {}", usuarioId);

        List<FavoritoMetadato> favoritos = favoritoMetadatoRepository.findByUsuarioId(usuarioId);

        List<FavoritoDTO> resultado = favoritos.stream()
                .map(f -> convertirADTO(f.getMetadatoYoutube(), f.getFechaAdicion()))
                .collect(Collectors.toList());

        log.info("Total de favoritos: {}", resultado.size());
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean esFavorito(Long usuarioId, String videoId) {
        return favoritoMetadatoRepository.existsByUsuarioIdAndMetadatoIdVideoYoutube(usuarioId, videoId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarFavoritosPorUsuario(Long usuarioId) {
        return favoritoMetadatoRepository.countByUsuarioId(usuarioId);
    }

    private FavoritoDTO convertirADTO(MetadatoYoutube metadato, Instant fechaAdicion) {
        return FavoritoDTO.builder()
                .idVideoYoutube(metadato.getIdVideoYoutube())
                .titulo(metadato.getTitulo())
                .canal(metadato.getCanal())
                .duracionSegundos(metadato.getDuracionSegundos())
                .duracionTexto(metadato.getDuracionTexto())
                .miniaturaUrl(metadato.getMiniaturaUrl())
                .esExplicito(metadato.getEsExplicito())
                .fechaAdicion(fechaAdicion)
                .origen("METADATO")
                .build();
    }
}

