package com.emma.emmamusic.domain.reproducir.historial.service.impl;

import com.emma.emmamusic.domain.auth.model.Usuario;
import com.emma.emmamusic.domain.auth.repository.UsuarioRepository;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import com.emma.emmamusic.domain.buscar.repository.MetadatoYoutubeRepository;
import com.emma.emmamusic.domain.reproducir.historial.dto.HistorialPaginadoDTO;
import com.emma.emmamusic.domain.reproducir.historial.dto.HistorialReproduccionDTO;
import com.emma.emmamusic.domain.reproducir.historial.dto.RegistrarReproduccionRequest;
import com.emma.emmamusic.domain.reproducir.historial.mapper.HistorialReproduccionMapper;
import com.emma.emmamusic.domain.reproducir.historial.model.HistorialReproduccion;
import com.emma.emmamusic.domain.reproducir.historial.repository.HistorialReproduccionRepository;
import com.emma.emmamusic.domain.reproducir.historial.service.HistorialReproduccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HistorialReproduccionServiceImpl implements HistorialReproduccionService {

    private final HistorialReproduccionRepository historialRepository;
    private final UsuarioRepository usuarioRepository;
    private final MetadatoYoutubeRepository metadatoYoutubeRepository;
    private final HistorialReproduccionMapper mapper;

    @Override
    public HistorialReproduccionDTO registrarReproduccion(RegistrarReproduccionRequest request) {
        log.info("Registrando reproducción para usuario: {}, videoId: {}",
                request.getUsuarioId(), request.getVideoId());

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + request.getUsuarioId()));

        MetadatoYoutube metadato = metadatoYoutubeRepository.findByIdVideoYoutube(request.getVideoId())
                .orElseThrow(() -> new RuntimeException("Metadato no encontrado para videoId: " + request.getVideoId()));

        metadato.setContadorReproducciones(
                (metadato.getContadorReproducciones() != null ? metadato.getContadorReproducciones() : 0) + 1
        );
        metadatoYoutubeRepository.save(metadato);

        HistorialReproduccion historial = HistorialReproduccion.builder()
                .usuario(usuario)
                .metadatoYoutube(metadato)
                .idVideoYoutube(request.getVideoId())
                .duracionReproducidaSegundos(request.getDuracionReproducidaSegundos())
                .porcentajeCompletado(request.getPorcentajeCompletado())
                .tipoDispositivo(request.getTipoDispositivo())
                .build();

        historial = historialRepository.save(historial);

        log.info("Reproducción registrada exitosamente: {}", historial.getReproduccionId());

        return mapper.toDTO(historial);
    }

    @Override
    @Transactional(readOnly = true)
    public HistorialPaginadoDTO obtenerHistorialPaginado(Long usuarioId, int pagina, int tamanio) {
        log.info("Obteniendo historial paginado para usuario: {} (página: {}, tamaño: {})",
                usuarioId, pagina, tamanio);

        Pageable pageable = PageRequest.of(pagina, tamanio);
        Page<HistorialReproduccion> page = historialRepository
                .findByUsuarioUsuarioIdOrderByFechaReproduccionDesc(usuarioId, pageable);

        log.info("Historial obtenido: {} elementos en página {} de {}",
                page.getNumberOfElements(), page.getNumber(), page.getTotalPages());

        return mapper.toPaginadoDTO(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialReproduccionDTO> obtenerHistorialReciente(Long usuarioId, int limite) {
        log.info("Obteniendo historial reciente para usuario: {} (límite: {})", usuarioId, limite);

        List<HistorialReproduccion> historiales = limite <= 50
                ? historialRepository.findTop50ByUsuarioUsuarioIdOrderByFechaReproduccionDesc(usuarioId)
                : historialRepository.findByUsuarioUsuarioIdOrderByFechaReproduccionDesc(
                        usuarioId, PageRequest.of(0, limite)).getContent();

        log.info("Historial reciente obtenido: {} registros", historiales.size());

        return mapper.toDTOList(historiales);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialReproduccionDTO> obtenerHistorialPorVideoId(Long usuarioId, String videoId) {
        log.info("Obteniendo historial para usuario: {} y videoId: {}", usuarioId, videoId);

        List<HistorialReproduccion> historiales = historialRepository
                .findByUsuarioIdAndVideoId(usuarioId, videoId);

        log.info("Historial obtenido: {} reproducciones de este video", historiales.size());

        return mapper.toDTOList(historiales);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarReproduccionesUsuario(Long usuarioId) {
        log.info("Contando reproducciones para usuario: {}", usuarioId);

        Long total = historialRepository.contarPorUsuario(usuarioId);

        log.info("Total de reproducciones: {}", total);

        return total;
    }

    @Override
    public void limpiarHistorial(Long usuarioId) {
        log.info("Limpiando historial completo para usuario: {}", usuarioId);

        historialRepository.deleteByUsuarioUsuarioId(usuarioId);

        log.info("Historial limpiado exitosamente");
    }

    @Override
    public void eliminarReproduccion(Long reproduccionId) {
        log.info("Eliminando reproducción: {}", reproduccionId);

        HistorialReproduccion historial = historialRepository.findById(reproduccionId)
                .orElseThrow(() -> new RuntimeException("Reproducción no encontrada: " + reproduccionId));

        historialRepository.delete(historial);

        log.info("Reproducción eliminada exitosamente");
    }
}

