package com.emma.emmamusic.domain.listareproduccion.service.impl;

import com.emma.emmamusic.domain.auth.model.Usuario;
import com.emma.emmamusic.domain.auth.repository.UsuarioRepository;
import com.emma.emmamusic.domain.buscar.dto.ResultadoBusquedaDTO;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import com.emma.emmamusic.domain.buscar.repository.MetadatoYoutubeRepository;
import com.emma.emmamusic.domain.buscar.service.BuscadorService;
import com.emma.emmamusic.domain.listareproduccion.dto.CancionPlaylistDTO;
import com.emma.emmamusic.domain.listareproduccion.dto.ListaReproduccionCrearDTO;
import com.emma.emmamusic.domain.listareproduccion.dto.ListaReproduccionDTO;
import com.emma.emmamusic.domain.listareproduccion.model.ListaReproduccion;
import com.emma.emmamusic.domain.listareproduccion.model.ListaReproduccionMetadato;
import com.emma.emmamusic.domain.listareproduccion.repository.ListaReproduccionMetadatoRepository;
import com.emma.emmamusic.domain.listareproduccion.repository.ListaReproduccionRepository;
import com.emma.emmamusic.domain.listareproduccion.service.PlaylistService;
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
public class PlaylistServiceImpl implements PlaylistService {

    private final ListaReproduccionRepository listaRepository;
    private final ListaReproduccionMetadatoRepository listaMetadatoRepository;
    private final MetadatoYoutubeRepository metadatoRepository;
    private final UsuarioRepository usuarioRepository;
    private final BuscadorService buscadorService;

    @Override
    @Transactional
    public ListaReproduccionDTO crearPlaylist(Long usuarioId, ListaReproduccionCrearDTO dto) {
        log.info("Creando playlist para usuario: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        ListaReproduccion lista = ListaReproduccion.builder()
                .usuario(usuario)
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .urlImagenPortada(dto.getUrlImagenPortada())
                .esPublica(dto.getEsPublica() != null ? dto.getEsPublica() : false)
                .esColaborativa(dto.getEsColaborativa() != null ? dto.getEsColaborativa() : false)
                .build();

        listaRepository.save(lista);

        log.info("Playlist creada con ID: {}", lista.getListaId());
        return convertirADTO(lista, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public ListaReproduccionDTO obtenerPlaylist(Long listaId) {
        log.info("Obteniendo playlist: {}", listaId);

        ListaReproduccion lista = listaRepository.findById(listaId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist", "id", listaId));

        List<ListaReproduccionMetadato> metadatos = listaMetadatoRepository
                .findByListaIdOrderByPosicion(listaId);

        List<CancionPlaylistDTO> canciones = metadatos.stream()
                .map(this::convertirCancionADTO)
                .collect(Collectors.toList());

        return convertirADTO(lista, canciones);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListaReproduccionDTO> obtenerPlaylistsPorUsuario(Long usuarioId) {
        log.info("Obteniendo playlists para usuario: {}", usuarioId);

        List<ListaReproduccion> listas = listaRepository.findByUsuarioUsuarioId(usuarioId);

        return listas.stream()
                .map(lista -> {
                    long totalCanciones = listaMetadatoRepository.countByListaId(lista.getListaId());
                    ListaReproduccionDTO dto = convertirADTO(lista, null);
                    dto.setTotalCanciones((int) totalCanciones);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListaReproduccionDTO> obtenerPlaylistsPublicas() {
        log.info("Obteniendo playlists públicas");

        List<ListaReproduccion> listas = listaRepository.findByEsPublicaTrue();

        return listas.stream()
                .map(lista -> {
                    long totalCanciones = listaMetadatoRepository.countByListaId(lista.getListaId());
                    ListaReproduccionDTO dto = convertirADTO(lista, null);
                    dto.setTotalCanciones((int) totalCanciones);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ListaReproduccionDTO actualizarPlaylist(Long listaId, Long usuarioId, ListaReproduccionCrearDTO dto) {
        log.info("Actualizando playlist: {}", listaId);

        ListaReproduccion lista = listaRepository.findById(listaId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist", "id", listaId));

        if (!lista.getUsuario().getUsuarioId().equals(usuarioId)) {
            throw new ResourceConflictException("No tienes permiso para actualizar esta playlist");
        }

        lista.setTitulo(dto.getTitulo());
        lista.setDescripcion(dto.getDescripcion());
        lista.setUrlImagenPortada(dto.getUrlImagenPortada());
        lista.setEsPublica(dto.getEsPublica());
        lista.setEsColaborativa(dto.getEsColaborativa());

        listaRepository.save(lista);

        List<ListaReproduccionMetadato> metadatos = listaMetadatoRepository
                .findByListaIdOrderByPosicion(listaId);

        List<CancionPlaylistDTO> canciones = metadatos.stream()
                .map(this::convertirCancionADTO)
                .collect(Collectors.toList());

        return convertirADTO(lista, canciones);
    }

    @Override
    @Transactional
    public void eliminarPlaylist(Long listaId, Long usuarioId) {
        log.info("Eliminando playlist: {}", listaId);

        ListaReproduccion lista = listaRepository.findById(listaId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist", "id", listaId));

        if (!lista.getUsuario().getUsuarioId().equals(usuarioId)) {
            throw new ResourceConflictException("No tienes permiso para eliminar esta playlist");
        }

        listaRepository.delete(lista);
        log.info("Playlist eliminada: {}", listaId);
    }

    @Override
    @Transactional
    public CancionPlaylistDTO agregarCancion(Long listaId, String videoId, Long usuarioId) {
        log.info("Agregando canción {} a playlist {}", videoId, listaId);

        ListaReproduccion lista = listaRepository.findById(listaId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist", "id", listaId));

        MetadatoYoutube metadato = metadatoRepository.findByIdVideoYoutube(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Metadato", "videoId", videoId));

        if (listaMetadatoRepository.existsByListaReproduccionListaIdAndMetadatoYoutubeMetadatoId(
                listaId, metadato.getMetadatoId())) {
            throw new ResourceConflictException("La canción ya está en la playlist");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        Integer maxPosicion = listaMetadatoRepository.findMaxPosicionByListaId(listaId).orElse(-1);
        Integer nuevaPosicion = maxPosicion + 1;

        ListaReproduccionMetadato.ListaReproduccionMetadatoId id =
                new ListaReproduccionMetadato.ListaReproduccionMetadatoId(listaId, metadato.getMetadatoId());

        ListaReproduccionMetadato listaMetadato = ListaReproduccionMetadato.builder()
                .id(id)
                .listaReproduccion(lista)
                .metadatoYoutube(metadato)
                .posicion(nuevaPosicion)
                .anadidoPorUsuario(usuario)
                .build();

        listaMetadatoRepository.save(listaMetadato);

        metadato.setContadorAgregadasPlaylists(metadato.getContadorAgregadasPlaylists() + 1);
        metadato.setUltimaActividad(Instant.now());
        metadatoRepository.save(metadato);

        log.info("Canción agregada a playlist");
        return convertirCancionADTO(listaMetadato);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CancionPlaylistDTO> obtenerCanciones(Long listaId) {
        log.info("Obteniendo canciones de playlist: {}", listaId);

        List<ListaReproduccionMetadato> metadatos = listaMetadatoRepository
                .findByListaIdOrderByPosicion(listaId);

        return metadatos.stream()
                .map(this::convertirCancionADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void eliminarCancion(Long listaId, String videoId, Long usuarioId) {
        log.info("Eliminando canción {} de playlist {}", videoId, listaId);

        MetadatoYoutube metadato = metadatoRepository.findByIdVideoYoutube(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Metadato", "videoId", videoId));

        listaMetadatoRepository.deleteByListaIdAndMetadatoId(listaId, metadato.getMetadatoId());

        if (metadato.getContadorAgregadasPlaylists() > 0) {
            metadato.setContadorAgregadasPlaylists(metadato.getContadorAgregadasPlaylists() - 1);
            metadato.setUltimaActividad(Instant.now());
            metadatoRepository.save(metadato);
        }

        log.info("Canción eliminada de playlist");
    }

    @Override
    @Transactional
    public void reordenarCancion(Long listaId, String videoId, Integer nuevaPosicion, Long usuarioId) {
        log.info("Reordenando canción {} en playlist {} a posición {}", videoId, listaId, nuevaPosicion);

        MetadatoYoutube metadato = metadatoRepository.findByIdVideoYoutube(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Metadato", "videoId", videoId));

        listaMetadatoRepository.actualizarPosicion(listaId, metadato.getMetadatoId(), nuevaPosicion);

        log.info("Canción reordenada");
    }

    @Override
    @Transactional(readOnly = true)
    public long contarCancionesEnPlaylist(Long listaId) {
        return listaMetadatoRepository.countByListaId(listaId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPlaylistsPorUsuario(Long usuarioId) {
        log.info("Contando playlists para usuario: {}", usuarioId);
        return listaRepository.countByUsuarioUsuarioId(usuarioId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CancionPlaylistDTO> buscarCancionesParaAgregar(Long listaId, String consulta) {
        log.info("Buscando canciones para agregar a playlist {}: '{}'", listaId, consulta);

        if (!listaRepository.existsById(listaId)) {
            throw new ResourceNotFoundException("Playlist", "id", listaId);
        }

        List<ResultadoBusquedaDTO> resultados = buscadorService.buscarCanciones(consulta);

        log.info("Se encontraron {} resultados de búsqueda", resultados.size());

        return resultados.stream()
                .map(resultado -> CancionPlaylistDTO.builder()
                        .idVideoYoutube(resultado.getIdVideo())
                        .titulo(resultado.getTitulo())
                        .canal(resultado.getCanal())
                        .duracionSegundos(resultado.getDuracionSegundos())
                        .duracionTexto(resultado.getDuracionTexto())
                        .miniaturaUrl(resultado.getMiniaturas() != null && !resultado.getMiniaturas().isEmpty()
                                ? resultado.getMiniaturas().get(0)
                                : null)
                        .esExplicito(resultado.getEsExplicito())
                        .build())
                .collect(Collectors.toList());
    }

    private ListaReproduccionDTO convertirADTO(ListaReproduccion lista, List<CancionPlaylistDTO> canciones) {
        return ListaReproduccionDTO.builder()
                .listaId(lista.getListaId())
                .usuarioId(lista.getUsuario().getUsuarioId())
                .titulo(lista.getTitulo())
                .descripcion(lista.getDescripcion())
                .urlImagenPortada(lista.getUrlImagenPortada())
                .esPublica(lista.getEsPublica())
                .esColaborativa(lista.getEsColaborativa())
                .fechaCreacion(lista.getFechaCreacion())
                .fechaActualizacion(lista.getFechaActualizacion())
                .totalCanciones(canciones != null ? canciones.size() : 0)
                .canciones(canciones)
                .build();
    }

    private CancionPlaylistDTO convertirCancionADTO(ListaReproduccionMetadato listaMetadato) {
        MetadatoYoutube metadato = listaMetadato.getMetadatoYoutube();

        return CancionPlaylistDTO.builder()
                .metadatoId(metadato.getMetadatoId())
                .idVideoYoutube(metadato.getIdVideoYoutube())
                .titulo(metadato.getTitulo())
                .canal(metadato.getCanal())
                .duracionSegundos(metadato.getDuracionSegundos())
                .duracionTexto(metadato.getDuracionTexto())
                .miniaturaUrl(metadato.getMiniaturaUrl())
                .esExplicito(metadato.getEsExplicito())
                .posicion(listaMetadato.getPosicion())
                .fechaAdicion(listaMetadato.getFechaAdicion())
                .anadidoPor(listaMetadato.getAnadidoPorUsuario() != null ?
                        listaMetadato.getAnadidoPorUsuario().getNombreUsuario() : null)
                .build();
    }
}

