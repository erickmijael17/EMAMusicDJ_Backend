package com.emma.emmamusic.domain.biblioteca.service.impl;

import com.emma.emmamusic.domain.biblioteca.dto.BibliotecaDTO;
import com.emma.emmamusic.domain.biblioteca.dto.BibliotecaPaginadaDTO;
import com.emma.emmamusic.domain.biblioteca.dto.CancionBibliotecaDTO;
import com.emma.emmamusic.domain.biblioteca.dto.EstadisticasBibliotecaDTO;
import com.emma.emmamusic.domain.biblioteca.mapper.BibliotecaMapper;
import com.emma.emmamusic.domain.biblioteca.service.BibliotecaService;
import com.emma.emmamusic.domain.favoritos.model.FavoritoMetadato;
import com.emma.emmamusic.domain.favoritos.repository.FavoritoMetadatoRepository;
import com.emma.emmamusic.domain.listareproduccion.repository.ListaReproduccionRepository;
import com.emma.emmamusic.domain.reproducir.historial.model.HistorialReproduccion;
import com.emma.emmamusic.domain.reproducir.historial.repository.HistorialReproduccionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BibliotecaServiceImpl implements BibliotecaService {

    private final FavoritoMetadatoRepository favoritoRepository;
    private final HistorialReproduccionRepository historialRepository;
    private final ListaReproduccionRepository listaReproduccionRepository;
    private final BibliotecaMapper bibliotecaMapper;

    @Override
    @Transactional(readOnly = true)
    public BibliotecaDTO obtenerBibliotecaCompleta(Long usuarioId) {
        log.info("Obteniendo biblioteca completa para usuario: {}", usuarioId);

        List<FavoritoMetadato> favoritos = favoritoRepository.findByUsuarioId(usuarioId);
        List<HistorialReproduccion> recientes = historialRepository
                .findTop50ByUsuarioUsuarioIdOrderByFechaReproduccionDesc(usuarioId);

        List<CancionBibliotecaDTO> favoritosDTO = favoritos.stream()
                .map(fav -> {
                    CancionBibliotecaDTO dto = bibliotecaMapper.toCancionDTO(
                            fav.getMetadatoYoutube(),
                            fav.getFechaAdicion()
                    );
                    dto.setEsFavorita(true);
                    return dto;
                })
                .collect(Collectors.toList());

        Map<String, Long> contadorReproducciones = recientes.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getMetadatoYoutube().getIdVideoYoutube(),
                        Collectors.counting()
                ));

        List<CancionBibliotecaDTO> recientesDTO = recientes.stream()
                .collect(Collectors.toMap(
                        h -> h.getMetadatoYoutube().getIdVideoYoutube(),
                        h -> {
                            CancionBibliotecaDTO dto = bibliotecaMapper.toCancionDTO(
                                    h.getMetadatoYoutube(),
                                    h.getFechaReproduccion()
                            );
                            dto.setVecesReproducida(
                                    contadorReproducciones.getOrDefault(h.getMetadatoYoutube().getIdVideoYoutube(), 0L).intValue()
                            );
                            return dto;
                        },
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .toList();

        EstadisticasBibliotecaDTO estadisticas = obtenerEstadisticas(usuarioId);

        Set<String> cancionesUnicas = new HashSet<>();
        favoritos.forEach(f -> cancionesUnicas.add(f.getMetadatoYoutube().getIdVideoYoutube()));
        recientes.forEach(r -> cancionesUnicas.add(r.getMetadatoYoutube().getIdVideoYoutube()));

        log.info("Biblioteca completa obtenida: {} favoritos, {} recientes, {} únicas",
                favoritosDTO.size(), recientesDTO.size(), cancionesUnicas.size());

        return BibliotecaDTO.builder()
                .usuarioId(usuarioId)
                .favoritos(favoritosDTO)
                .recientementeReproducidas(recientesDTO)
                .estadisticas(estadisticas)
                .totalCanciones(cancionesUnicas.size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BibliotecaPaginadaDTO obtenerCancionesPaginadas(Long usuarioId, Integer pagina, Integer tamanio) {
        log.info("Obteniendo canciones paginadas para usuario: {} (página {}, tamaño {})",
                usuarioId, pagina, tamanio);

        Pageable pageable = PageRequest.of(pagina, tamanio);
        var page = historialRepository.findCancionesUnicasPorUsuario(usuarioId, pageable);

        Set<String> videoIdsFavoritos = favoritoRepository.findByUsuarioId(usuarioId).stream()
                .map(f -> f.getMetadatoYoutube().getIdVideoYoutube())
                .collect(Collectors.toSet());

        BibliotecaPaginadaDTO resultado = bibliotecaMapper.toPaginadaDTO(
                page.map(HistorialReproduccion::getMetadatoYoutube)
        );

        resultado.getCanciones().forEach(cancion -> {
            cancion.setEsFavorita(videoIdsFavoritos.contains(cancion.getVideoId()));
        });

        log.info("Canciones paginadas obtenidas: {} elementos", resultado.getCanciones().size());
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public BibliotecaPaginadaDTO obtenerFavoritosPaginados(Long usuarioId, Integer pagina, Integer tamanio) {
        log.info("Obteniendo favoritos paginados para usuario: {} (página {}, tamaño {})",
                usuarioId, pagina, tamanio);

        List<FavoritoMetadato> todosFavoritos = favoritoRepository.findByUsuarioId(usuarioId);

        int inicio = pagina * tamanio;
        int fin = Math.min(inicio + tamanio, todosFavoritos.size());

        List<CancionBibliotecaDTO> favoritosPagina = todosFavoritos.subList(inicio, fin).stream()
                .map(fav -> {
                    CancionBibliotecaDTO dto = bibliotecaMapper.toCancionDTO(
                            fav.getMetadatoYoutube(),
                            fav.getFechaAdicion()
                    );
                    dto.setEsFavorita(true);
                    return dto;
                })
                .collect(Collectors.toList());

        int totalPaginas = (int) Math.ceil((double) todosFavoritos.size() / tamanio);

        log.info("Favoritos paginados obtenidos: {} elementos", favoritosPagina.size());

        return BibliotecaPaginadaDTO.builder()
                .canciones(favoritosPagina)
                .paginaActual(pagina)
                .tamanoPagina(tamanio)
                .totalPaginas(totalPaginas)
                .totalElementos((long) todosFavoritos.size())
                .esUltimaPagina(pagina >= totalPaginas - 1)
                .esPrimeraPagina(pagina == 0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public BibliotecaPaginadaDTO obtenerRecientesPaginadas(Long usuarioId, Integer pagina, Integer tamanio) {
        log.info("Obteniendo recientes paginadas para usuario: {} (página {}, tamaño {})",
                usuarioId, pagina, tamanio);

        Pageable pageable = PageRequest.of(pagina, tamanio);
        var page = historialRepository.findByUsuarioUsuarioIdOrderByFechaReproduccionDesc(usuarioId, pageable);

        Set<String> videoIdsFavoritos = favoritoRepository.findByUsuarioId(usuarioId).stream()
                .map(f -> f.getMetadatoYoutube().getIdVideoYoutube())
                .collect(Collectors.toSet());

        BibliotecaPaginadaDTO resultado = bibliotecaMapper.toPaginadaDTO(
                page.map(HistorialReproduccion::getMetadatoYoutube)
        );

        resultado.getCanciones().forEach(cancion -> {
            cancion.setEsFavorita(videoIdsFavoritos.contains(cancion.getVideoId()));
        });

        log.info("Recientes paginadas obtenidas: {} elementos", resultado.getCanciones().size());
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasBibliotecaDTO obtenerEstadisticas(Long usuarioId) {
        log.info("Obteniendo estadísticas para usuario: {}", usuarioId);

        long totalFavoritos = favoritoRepository.countByUsuarioId(usuarioId);
        Long totalReproducciones = historialRepository.contarPorUsuario(usuarioId);
        long totalPlaylists = listaReproduccionRepository.countByUsuarioUsuarioId(usuarioId);

        Instant hace30Dias = Instant.now().minus(30, ChronoUnit.DAYS);
        List<HistorialReproduccion> reproduccionesRecientes =
                historialRepository.findByUsuarioIdAndFechaDesde(usuarioId, hace30Dias);

        Set<String> cancionesUnicas = reproduccionesRecientes.stream()
                .map(h -> h.getMetadatoYoutube().getIdVideoYoutube())
                .collect(Collectors.toSet());

        String cancionMasReproducida = reproduccionesRecientes.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getMetadatoYoutube().getTitulo(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin datos");

        String artistaFavorito = reproduccionesRecientes.stream()
                .filter(h -> h.getMetadatoYoutube().getCanal() != null)
                .collect(Collectors.groupingBy(
                        h -> h.getMetadatoYoutube().getCanal(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin datos");

        int minutosEscuchados = reproduccionesRecientes.stream()
                .mapToInt(h -> {
                    Integer duracion = h.getDuracionReproducidaSegundos();
                    return duracion != null ? duracion / 60 : 0;
                })
                .sum();

        log.info("Estadísticas obtenidas para usuario {}: {} favoritos, {} reproducciones, {} playlists",
                usuarioId, totalFavoritos, totalReproducciones, totalPlaylists);

        return EstadisticasBibliotecaDTO.builder()
                .totalFavoritos((int) totalFavoritos)
                .totalReproducciones(totalReproducciones != null ? totalReproducciones.intValue() : 0)
                .totalPlaylists((int) totalPlaylists)
                .totalCancionesUnicas(cancionesUnicas.size())
                .cancionMasReproducida(cancionMasReproducida)
                .artistaFavorito(artistaFavorito)
                .minutosEscuchados(minutosEscuchados)
                .build();
    }
}

