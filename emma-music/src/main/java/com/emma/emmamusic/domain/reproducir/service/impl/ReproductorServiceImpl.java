package com.emma.emmamusic.domain.reproducir.service.impl;

import com.emma.emmamusic.domain.auth.model.Usuario;
import com.emma.emmamusic.domain.auth.repository.UsuarioRepository;
import com.emma.emmamusic.domain.buscar.repository.MetadatoYoutubeRepository;
import com.emma.emmamusic.domain.buscar.service.BuscadorService;
import com.emma.emmamusic.domain.favoritos.repository.FavoritoMetadatoRepository;
import com.emma.emmamusic.domain.favoritos.service.FavoritoService;
import com.emma.emmamusic.domain.reproducir.dto.*;
import com.emma.emmamusic.domain.reproducir.mapper.ReproductorMapper;
import com.emma.emmamusic.domain.reproducir.model.ColaReproduccion;
import com.emma.emmamusic.domain.reproducir.model.EstadoReproductor;
import com.emma.emmamusic.domain.reproducir.repository.ColaReproduccionRepository;
import com.emma.emmamusic.domain.reproducir.repository.EstadoReproductorRepository;
import com.emma.emmamusic.domain.reproducir.service.ReproductorService;
import com.emma.emmamusic.domain.reproducir.service.StreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReproductorServiceImpl implements ReproductorService {

    private final EstadoReproductorRepository estadoRepository;
    private final ColaReproduccionRepository colaRepository;
    private final UsuarioRepository usuarioRepository;
    private final StreamService streamService;
    private final BuscadorService buscadorService;
    private final MetadatoYoutubeRepository metadatoYoutubeRepository;
    private final FavoritoMetadatoRepository favoritoMetadatoRepository;
    private final FavoritoService favoritoService;
    private final ReproductorMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    private static final int MAX_CANCIONES_COLA = 100;

    @Override
    @Transactional(readOnly = true)
    public EstadoReproductorDTO obtenerEstado(Long usuarioId) {
        log.info("Obteniendo estado del reproductor para usuario: {}", usuarioId);

        EstadoReproductor estado = estadoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearEstadoInicial(usuarioId));

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElse(null);

        return construirEstadoCompleto(estado, cola);
    }

    @Override
    public EstadoReproductorDTO reproducir(Long usuarioId, String videoId) {
        return reproducirConInfoCancion(usuarioId, videoId, null);
    }

    private EstadoReproductorDTO reproducirConInfoCancion(Long usuarioId, String videoId, CancionColaDTO infoCancion) {
        log.info("Reproduciendo canción {} para usuario {}", videoId, usuarioId);
        long tiempoInicio = System.currentTimeMillis();

        EstadoReproductor estado = obtenerOCrearEstado(usuarioId);
        estado.actualizarCancion(videoId, "Cargando...", null, null, null);
        estado.play();
        estadoRepository.save(estado);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId).orElse(null);
        EstadoReproductorDTO estadoInicial = construirEstadoCompleto(estado, cola);

        notificarCargando(usuarioId, estadoInicial);

        CompletableFuture.supplyAsync(() -> {
            try {
                log.info("=== INICIANDO OBTENCIÓN DE STREAM para videoId: {} ===", videoId);
                ResultadoReproduccionDTO resultado = streamService.obtenerUrlReproduccion(videoId, usuarioId);
                log.info("=== STREAM OBTENIDO EXITOSAMENTE: tipo={} ===", resultado.getTipoReproduccion());
                return resultado;
            } catch (Exception e) {
                log.error("=== ERROR OBTENIENDO STREAM: {} ===", e.getMessage(), e);
                notificarError(usuarioId, "Error obteniendo stream: " + e.getMessage());
                throw e;
            }
        }).thenAccept(resultado -> {
            long tiempoStream = System.currentTimeMillis() - tiempoInicio;
            log.info("=== Stream obtenido en {} ms ===", tiempoStream);

            if (resultado.getTipoReproduccion() != ResultadoReproduccionDTO.TipoReproduccion.ERROR) {
                log.info("=== PROCESANDO RESULTADO DEL STREAM ===");

                EstadoReproductor estadoActualizado = estadoRepository.findByUsuarioId(usuarioId).orElse(estado);
                estadoActualizado.actualizarCancion(videoId, resultado.getTitulo(), null, null, null);

                boolean esFavorita = verificarEsFavorita(usuarioId, videoId);
                estadoActualizado.setEsFavorita(esFavorita);
                estadoRepository.save(estadoActualizado);
                log.info("=== Estado actualizado en BD ===");

                ColaReproduccion colaActualizada = colaRepository.findByUsuarioId(usuarioId).orElse(null);
                EstadoReproductorDTO estadoFinal = construirEstadoCompleto(estadoActualizado, colaActualizada);
                
                // Usar el endpoint de proxy para evitar problemas de CORS/IP
                String proxyUrl = "/api/v1/stream/audio/" + videoId + "?usuarioId=" + usuarioId;
                estadoFinal.setUrlReproduccion(proxyUrl);
                
                estadoFinal.setTipoReproduccion(resultado.getTipoReproduccion().name());

                log.info("=== ENVIANDO NOTIFICACIÓN LISTO POR WEBSOCKET a usuario {} ===", usuarioId);
                notificarListo(usuarioId, estadoFinal);
                log.info("=== NOTIFICACIÓN LISTO ENVIADA ===");

                procesarReproduccionAsync(videoId, usuarioId, infoCancion);
            } else {
                log.error("=== TIPO DE REPRODUCCIÓN ES ERROR, NO SE ENVÍA NOTIFICACIÓN LISTO ===");
            }
        }).exceptionally(throwable -> {
            log.error("=== EXCEPCIÓN EN COMPLETABLEFUTURE: {} ===", throwable.getMessage(), throwable);
            notificarError(usuarioId, "Error procesando stream: " + throwable.getMessage());
            return null;
        });

        return estadoInicial;
    }

    private void procesarReproduccionAsync(String videoId, Long usuarioId, CancionColaDTO infoCancion) {
        CompletableFuture.runAsync(() -> {
            try {
                if (infoCancion != null) {
                    agregarAColaInternoConInfo(usuarioId, infoCancion);
                } else {
                    agregarAColaInterno(usuarioId, videoId, true);
                }
            } catch (Exception e) {
                log.error("Error agregando a cola async: {}", e.getMessage());
            }
        });
    }

    @Override
    public EstadoReproductorDTO reproducirDesdeBusqueda(Long usuarioId, String videoId, String terminoBusqueda, Integer indiceEnBusqueda) {
        log.info("Reproduciendo desde búsqueda: '{}', videoId: {}, usuario: {}", terminoBusqueda, videoId, usuarioId);

        List<com.emma.emmamusic.domain.buscar.dto.ResultadoBusquedaDTO> resultadosBusqueda =
            buscadorService.buscarCanciones(terminoBusqueda);

        if (resultadosBusqueda == null || resultadosBusqueda.isEmpty()) {
            throw new RuntimeException("No se encontraron resultados para: " + terminoBusqueda);
        }

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearColaInicial(usuarioId));

        cola.setContextoOrigen(ColaReproduccion.ContextoOrigen.BUSQUEDA);
        cola.setTerminoBusqueda(terminoBusqueda);
        cola.setPlaylistId(null);
        cola.setAlbumId(null);

        List<CancionColaDTO> canciones = mapper.parsearCanciones(cola.getCanciones());
        canciones.clear();

        for (com.emma.emmamusic.domain.buscar.dto.ResultadoBusquedaDTO resultado : resultadosBusqueda) {
            String miniatura = null;
            if (resultado.getMiniaturas() != null && !resultado.getMiniaturas().isEmpty()) {
                miniatura = resultado.getMiniaturas().get(0);
            }

            CancionColaDTO cancion = CancionColaDTO.builder()
                    .videoId(resultado.getIdVideo())
                    .titulo(resultado.getTitulo())
                    .canal(resultado.getCanal() != null ? resultado.getCanal() : "Desconocido")
                    .duracionSegundos(resultado.getDuracionSegundos())
                    .duracionTexto(resultado.getDuracionTexto())
                    .miniaturaUrl(miniatura)
                    .esExplicita(resultado.getEsExplicito())
                    .tipoCancion("Canción")
                    .build();
            canciones.add(cancion);
        }

        int indiceCancion = indiceEnBusqueda != null && indiceEnBusqueda >= 0 && indiceEnBusqueda < canciones.size()
            ? indiceEnBusqueda
            : 0;

        boolean encontrada = false;
        for (int i = 0; i < canciones.size(); i++) {
            if (videoId != null && videoId.equals(canciones.get(i).getVideoId())) {
                indiceCancion = i;
                encontrada = true;
                break;
            }
        }

        if (!encontrada) {
            log.warn("VideoId {} no encontrado en resultados de búsqueda, usando índice {}", videoId, indiceCancion);
        }

        cola.setIndiceActual(indiceCancion);
        cola.setCanciones(mapper.serializarCanciones(canciones));
        colaRepository.save(cola);

        log.info("Cola actualizada con {} canciones de búsqueda '{}', índice actual: {}",
            canciones.size(), terminoBusqueda, indiceCancion);

        CancionColaDTO cancionSeleccionada = canciones.get(indiceCancion);
        return reproducirConInfoCancion(usuarioId, cancionSeleccionada.getVideoId(), cancionSeleccionada);
    }

    @Override
    public EstadoReproductorDTO play(Long usuarioId) {
        log.info("Play para usuario {}", usuarioId);

        EstadoReproductor estado = obtenerOCrearEstado(usuarioId);

        if (estado.getVideoIdActual() == null) {
            ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId).orElse(null);
            if (cola != null) {
                List<CancionColaDTO> canciones = mapper.parsearCanciones(cola.getCanciones());
                if (!canciones.isEmpty() && cola.getIndiceActual() < canciones.size()) {
                    CancionColaDTO cancion = canciones.get(cola.getIndiceActual());
                    return reproducir(usuarioId, cancion.getVideoId());
                }
            }
            throw new RuntimeException("No hay canción actual para reproducir");
        }

        estado.play();
        estadoRepository.save(estado);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId).orElse(null);
        return construirEstadoCompleto(estado, cola);
    }

    @Override
    public EstadoReproductorDTO pause(Long usuarioId) {
        log.info("Pause para usuario {}", usuarioId);

        EstadoReproductor estado = obtenerOCrearEstado(usuarioId);
        estado.pause();
        estadoRepository.save(estado);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId).orElse(null);
        return construirEstadoCompleto(estado, cola);
    }

    @Override
    public EstadoReproductorDTO siguiente(Long usuarioId) {
        log.info("Siguiente canción para usuario {}", usuarioId);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay cola de reproducción"));

        List<CancionColaDTO> canciones = mapper.parsearCanciones(cola.getCanciones());

        if (canciones.isEmpty()) {
            throw new RuntimeException("La cola está vacía");
        }

        int nuevoIndice = calcularSiguienteIndice(cola, canciones.size());
        cola.setIndiceActual(nuevoIndice);
        colaRepository.save(cola);

        CancionColaDTO siguienteCancion = canciones.get(nuevoIndice);
        return reproducir(usuarioId, siguienteCancion.getVideoId());
    }

    @Override
    public EstadoReproductorDTO anterior(Long usuarioId) {
        log.info("Canción anterior para usuario {}", usuarioId);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay cola de reproducción"));

        List<CancionColaDTO> canciones = mapper.parsearCanciones(cola.getCanciones());

        if (canciones.isEmpty()) {
            throw new RuntimeException("La cola está vacía");
        }

        int nuevoIndice = calcularAnteriorIndice(cola, canciones.size());
        cola.setIndiceActual(nuevoIndice);
        colaRepository.save(cola);

        CancionColaDTO anteriorCancion = canciones.get(nuevoIndice);
        return reproducir(usuarioId, anteriorCancion.getVideoId());
    }

    @Override
    public EstadoReproductorDTO actualizarPosicion(Long usuarioId, Integer posicionSegundos) {
        log.debug("Actualizando posición a {} segundos para usuario {}", posicionSegundos, usuarioId);

        EstadoReproductor estado = obtenerOCrearEstado(usuarioId);
        estado.setPosicionSegundos(posicionSegundos);
        estadoRepository.save(estado);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId).orElse(null);
        return construirEstadoCompleto(estado, cola);
    }

    @Override
    public EstadoReproductorDTO actualizarVolumen(Long usuarioId, Integer volumen) {
        log.debug("Actualizando volumen a {} para usuario {}", volumen, usuarioId);

        if (volumen < 0 || volumen > 100) {
            throw new IllegalArgumentException("El volumen debe estar entre 0 y 100");
        }

        EstadoReproductor estado = obtenerOCrearEstado(usuarioId);
        estado.setVolumen(volumen);
        estadoRepository.save(estado);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId).orElse(null);
        return construirEstadoCompleto(estado, cola);
    }

    @Override
    public EstadoReproductorDTO toggleFavorito(Long usuarioId) {
        log.info("Toggle favorito para usuario {}", usuarioId);

        EstadoReproductor estado = obtenerOCrearEstado(usuarioId);

        if (estado.getVideoIdActual() == null) {
            throw new RuntimeException("No hay canción actual para marcar como favorita");
        }

        String videoId = estado.getVideoIdActual();
        boolean esFavorita = verificarEsFavorita(usuarioId, videoId);

        if (esFavorita) {
            favoritoService.eliminarFavorito(usuarioId, videoId);
            estado.setEsFavorita(false);
        } else {
            favoritoService.agregarFavorito(usuarioId, videoId);
            estado.setEsFavorita(true);
        }

        estadoRepository.save(estado);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId).orElse(null);
        return construirEstadoCompleto(estado, cola);
    }

    @Override
    @Transactional(readOnly = true)
    public ColaReproduccionDTO obtenerCola(Long usuarioId) {
        log.info("Obteniendo cola de reproducción para usuario {}", usuarioId);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearColaInicial(usuarioId));

        return mapper.toColaDTO(cola);
    }

    @Override
    public ColaReproduccionDTO agregarACola(Long usuarioId, List<String> videoIds, Boolean reproducirAhora) {
        log.info("Agregando {} canciones a la cola del usuario {}", videoIds.size(), usuarioId);

        if (videoIds == null || videoIds.isEmpty()) {
            throw new IllegalArgumentException("La lista de videoIds no puede estar vacía");
        }

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearColaInicial(usuarioId));

        List<CancionColaDTO> cancionesActuales = mapper.parsearCanciones(cola.getCanciones());

        for (String videoId : videoIds) {
            if (cancionesActuales.size() >= MAX_CANCIONES_COLA) {
                log.warn("Cola llena, eliminando canciones antiguas");
                cancionesActuales.remove(0);
                if (cola.getIndiceActual() > 0) {
                    cola.setIndiceActual(cola.getIndiceActual() - 1);
                }
            }

            CancionColaDTO cancion = obtenerInfoCancion(videoId);
            if (cancion != null) {
                cancionesActuales.add(cancion);
            }
        }

        String cancionesJson = mapper.serializarCanciones(cancionesActuales);
        cola.setCanciones(cancionesJson);

        if (Boolean.TRUE.equals(reproducirAhora) && !cancionesActuales.isEmpty()) {
            cola.setIndiceActual(cancionesActuales.size() - videoIds.size());
        }

        colaRepository.save(cola);

        if (Boolean.TRUE.equals(reproducirAhora) && !videoIds.isEmpty()) {
            reproducir(usuarioId, videoIds.get(0));
        }

        return mapper.toColaDTO(cola);
    }

    @Override
    public ColaReproduccionDTO eliminarDeCola(Long usuarioId, Integer indice) {
        log.info("Eliminando canción en índice {} de la cola del usuario {}", indice, usuarioId);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay cola de reproducción"));

        List<CancionColaDTO> canciones = mapper.parsearCanciones(cola.getCanciones());

        if (indice < 0 || indice >= canciones.size()) {
            throw new IllegalArgumentException("Índice fuera de rango");
        }

        canciones.remove(indice.intValue());

        if (indice < cola.getIndiceActual()) {
            cola.setIndiceActual(cola.getIndiceActual() - 1);
        } else if (indice.equals(cola.getIndiceActual())) {
            if (canciones.isEmpty()) {
                cola.setIndiceActual(0);
            } else if (cola.getIndiceActual() >= canciones.size()) {
                cola.setIndiceActual(canciones.size() - 1);
            }
        }

        String cancionesJson = mapper.serializarCanciones(canciones);
        cola.setCanciones(cancionesJson);
        colaRepository.save(cola);

        return mapper.toColaDTO(cola);
    }

    @Override
    public void limpiarCola(Long usuarioId) {
        log.info("Limpiando cola de reproducción para usuario {}", usuarioId);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay cola de reproducción"));

        cola.setCanciones("[]");
        cola.setIndiceActual(0);
        colaRepository.save(cola);

        EstadoReproductor estado = estadoRepository.findByUsuarioId(usuarioId).orElse(null);
        if (estado != null) {
            estado.pause();
            estadoRepository.save(estado);
        }
    }

    @Override
    public ColaReproduccionDTO cargarPlaylist(Long usuarioId, Long playlistId) {
        log.info("Cargando playlist {} en cola del usuario {}", playlistId, usuarioId);

        throw new UnsupportedOperationException("Implementación pendiente: integración con módulo de playlists");
    }

    @Override
    public ColaReproduccionDTO cambiarModoReproduccion(Long usuarioId, ColaReproduccionDTO.ModoReproduccion modo) {
        log.info("Cambiando modo de reproducción a {} para usuario {}", modo, usuarioId);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay cola de reproducción"));

        cola.setModoReproduccion(ColaReproduccion.ModoReproduccion.valueOf(modo.name()));
        colaRepository.save(cola);

        return mapper.toColaDTO(cola);
    }

    @Override
    public ColaReproduccionDTO reordenarCola(Long usuarioId, Integer indiceOrigen, Integer indiceDestino) {
        log.info("Reordenando cola: moviendo canción de índice {} a {} para usuario {}",
            indiceOrigen, indiceDestino, usuarioId);

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("No hay cola de reproducción"));

        List<CancionColaDTO> canciones = mapper.parsearCanciones(cola.getCanciones());

        if (indiceOrigen < 0 || indiceOrigen >= canciones.size() ||
            indiceDestino < 0 || indiceDestino >= canciones.size()) {
            throw new IllegalArgumentException("Índices fuera de rango");
        }

        CancionColaDTO cancion = canciones.remove(indiceOrigen.intValue());
        canciones.add(indiceDestino, cancion);

        if (cola.getIndiceActual().equals(indiceOrigen)) {
            cola.setIndiceActual(indiceDestino);
        } else if (indiceOrigen < cola.getIndiceActual() && indiceDestino >= cola.getIndiceActual()) {
            cola.setIndiceActual(cola.getIndiceActual() - 1);
        } else if (indiceOrigen > cola.getIndiceActual() && indiceDestino <= cola.getIndiceActual()) {
            cola.setIndiceActual(cola.getIndiceActual() + 1);
        }

        String cancionesJson = mapper.serializarCanciones(canciones);
        cola.setCanciones(cancionesJson);
        colaRepository.save(cola);

        return mapper.toColaDTO(cola);
    }

    private EstadoReproductor obtenerOCrearEstado(Long usuarioId) {
        return estadoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearEstadoInicial(usuarioId));
    }

    private EstadoReproductor crearEstadoInicial(Long usuarioId) {
        log.info("Creando estado inicial para usuario {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        EstadoReproductor estado = EstadoReproductor.builder()
                .usuario(usuario)
                .estaReproduciendo(false)
                .posicionSegundos(0)
                .volumen(80)
                .esFavorita(false)
                .build();

        return estadoRepository.save(estado);
    }

    private ColaReproduccion crearColaInicial(Long usuarioId) {
        log.info("Creando cola inicial para usuario {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + usuarioId));

        ColaReproduccion cola = ColaReproduccion.builder()
                .usuario(usuario)
                .canciones("[]")
                .indiceActual(0)
                .modoReproduccion(ColaReproduccion.ModoReproduccion.NORMAL)
                .build();

        return colaRepository.save(cola);
    }

    private EstadoReproductorDTO construirEstadoCompleto(EstadoReproductor estado, ColaReproduccion cola) {
        EstadoReproductorDTO estadoDTO = mapper.toEstadoDTO(estado);

        if (cola != null) {
            List<CancionColaDTO> canciones = mapper.parsearCanciones(cola.getCanciones());
            estadoDTO.setIndiceEnCola(cola.getIndiceActual());
            estadoDTO.setTotalEnCola(canciones.size());
            estadoDTO.setTieneSiguiente(!canciones.isEmpty() &&
                (cola.getIndiceActual() < canciones.size() - 1 ||
                 cola.getModoReproduccion() == ColaReproduccion.ModoReproduccion.REPETIR_TODAS));
            estadoDTO.setTieneAnterior(!canciones.isEmpty() &&
                (cola.getIndiceActual() > 0 ||
                 cola.getModoReproduccion() == ColaReproduccion.ModoReproduccion.REPETIR_TODAS));
            estadoDTO.setModoReproduccion(ColaReproduccionDTO.ModoReproduccion.valueOf(
                cola.getModoReproduccion().name()));
        }

        return estadoDTO;
    }

    private int calcularSiguienteIndice(ColaReproduccion cola, int totalCanciones) {
        int indiceActual = cola.getIndiceActual();
        ColaReproduccion.ModoReproduccion modo = cola.getModoReproduccion();

        if (modo == ColaReproduccion.ModoReproduccion.REPETIR_UNA) {
            return indiceActual;
        }

        if (indiceActual < totalCanciones - 1) {
            return indiceActual + 1;
        }

        if (modo == ColaReproduccion.ModoReproduccion.REPETIR_TODAS) {
            return 0;
        }

        return indiceActual;
    }

    private int calcularAnteriorIndice(ColaReproduccion cola, int totalCanciones) {
        int indiceActual = cola.getIndiceActual();
        ColaReproduccion.ModoReproduccion modo = cola.getModoReproduccion();

        if (modo == ColaReproduccion.ModoReproduccion.REPETIR_UNA) {
            return indiceActual;
        }

        if (indiceActual > 0) {
            return indiceActual - 1;
        }

        if (modo == ColaReproduccion.ModoReproduccion.REPETIR_TODAS) {
            return totalCanciones - 1;
        }

        return indiceActual;
    }

    private CancionColaDTO obtenerInfoCancion(String videoId) {
        try {
            return metadatoYoutubeRepository.findByIdVideoYoutube(videoId)
                    .map(metadato -> {
                        String miniatura = metadato.getMiniaturaUrl();
                        if (miniatura == null && videoId != null) {
                            miniatura = "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg";
                        }

                        return CancionColaDTO.builder()
                                .videoId(metadato.getIdVideoYoutube())
                                .titulo(metadato.getTitulo())
                                .canal(metadato.getCanal() != null ? metadato.getCanal() : "Desconocido")
                                .duracionSegundos(metadato.getDuracionSegundos())
                                .duracionTexto(metadato.getDuracionTexto())
                                .miniaturaUrl(miniatura)
                                .esExplicita(metadato.getEsExplicito())
                                .tipoCancion("Canción")
                                .build();
                    })
                    .orElseGet(() -> {
                        log.warn("No se encontró metadato para videoId: {}", videoId);
                        return CancionColaDTO.builder()
                                .videoId(videoId)
                                .titulo("Canción desconocida")
                                .canal("Desconocido")
                                .build();
                    });
        } catch (Exception e) {
            log.error("Error obteniendo información de canción {}: {}", videoId, e.getMessage());
            return CancionColaDTO.builder()
                    .videoId(videoId)
                    .titulo("Error al cargar")
                    .canal("Desconocido")
                    .build();
        }
    }

    private void agregarAColaInterno(Long usuarioId, String videoId, boolean esActual) {
        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearColaInicial(usuarioId));

        List<CancionColaDTO> canciones = mapper.parsearCanciones(cola.getCanciones());

        int indiceExistente = -1;
        for (int i = 0; i < canciones.size(); i++) {
            if (videoId.equals(canciones.get(i).getVideoId())) {
                indiceExistente = i;
                break;
            }
        }

        if (indiceExistente >= 0) {
            cola.setIndiceActual(indiceExistente);
        } else {
            CancionColaDTO cancion = obtenerInfoCancion(videoId);
            if (cancion != null) {
                if (canciones.size() >= MAX_CANCIONES_COLA) {
                    canciones.remove(0);
                    if (cola.getIndiceActual() > 0) {
                        cola.setIndiceActual(cola.getIndiceActual() - 1);
                    }
                }
                canciones.add(cancion);
                cola.setIndiceActual(canciones.size() - 1);
                String cancionesJson = mapper.serializarCanciones(canciones);
                cola.setCanciones(cancionesJson);
            }
        }

        colaRepository.save(cola);
    }

    private void agregarAColaInternoConInfo(Long usuarioId, CancionColaDTO cancionInfo) {
        log.debug("Agregando canción a cola con información precargada: {}", cancionInfo.getVideoId());

        ColaReproduccion cola = colaRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> crearColaInicial(usuarioId));

        List<CancionColaDTO> canciones = mapper.parsearCanciones(cola.getCanciones());

        int indiceExistente = -1;
        for (int i = 0; i < canciones.size(); i++) {
            if (cancionInfo.getVideoId().equals(canciones.get(i).getVideoId())) {
                indiceExistente = i;
                break;
            }
        }

        if (indiceExistente >= 0) {
            cola.setIndiceActual(indiceExistente);
            log.debug("Canción ya existe en cola en índice {}", indiceExistente);
        } else {
            if (canciones.size() >= MAX_CANCIONES_COLA) {
                canciones.remove(0);
                if (cola.getIndiceActual() > 0) {
                    cola.setIndiceActual(cola.getIndiceActual() - 1);
                }
            }
            canciones.add(cancionInfo);
            cola.setIndiceActual(canciones.size() - 1);
            String cancionesJson = mapper.serializarCanciones(canciones);
            cola.setCanciones(cancionesJson);
            log.debug("Canción agregada a cola en índice {}", canciones.size() - 1);
        }

        colaRepository.save(cola);
    }

    private boolean verificarEsFavorita(Long usuarioId, String videoId) {
        boolean esFavoritaMetadato = favoritoMetadatoRepository.existsByUsuarioIdAndMetadatoIdVideoYoutube(usuarioId, videoId);
        return esFavoritaMetadato;
    }

    private void notificarWebSocket(Long usuarioId, String tipo, EstadoReproductorDTO estado, String mensaje) {
        ReproductorWebSocketDTO notificacion = ReproductorWebSocketDTO.builder()
                .tipo(tipo)
                .estado(estado)
                .mensaje(mensaje)
                .timestamp(System.currentTimeMillis())
                .build();

        String destino = "/topic/reproductor/" + usuarioId;

        log.info("╔══════════════════════════════════════════════════════════════╗");
        log.info("║ ENVIANDO NOTIFICACIÓN WEBSOCKET                              ║");
        log.info("╠══════════════════════════════════════════════════════════════╣");
        log.info("║ Tipo: {}", tipo);
        log.info("║ Usuario: {}", usuarioId);
        log.info("║ Destino: {}", destino);
        log.info("║ Tiene Estado: {}", estado != null);
        if (estado != null) {
            log.info("║ URL Reproducción: {}", estado.getUrlReproduccion());
            log.info("║ Título: {}", estado.getTituloActual());
        }
        log.info("║ Mensaje: {}", mensaje);
        log.info("╚══════════════════════════════════════════════════════════════╝");

        messagingTemplate.convertAndSend(destino, notificacion);

        log.info("✓ Notificación WebSocket ENVIADA exitosamente");
    }

    private void notificarCargando(Long usuarioId, EstadoReproductorDTO estado) {
        notificarWebSocket(usuarioId, "CARGANDO", estado, null);
    }

    private void notificarListo(Long usuarioId, EstadoReproductorDTO estado) {
        notificarWebSocket(usuarioId, "LISTO", estado, null);
    }

    private void notificarError(Long usuarioId, String mensajeError) {
        notificarWebSocket(usuarioId, "ERROR", null, mensajeError);
    }

    private void notificarActualizado(Long usuarioId, EstadoReproductorDTO estado) {
        notificarWebSocket(usuarioId, "ACTUALIZADO", estado, null);
    }
}
