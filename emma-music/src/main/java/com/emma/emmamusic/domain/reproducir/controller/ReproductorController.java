package com.emma.emmamusic.domain.reproducir.controller;

import com.emma.emmamusic.domain.reproducir.dto.*;
import com.emma.emmamusic.domain.reproducir.service.ReproductorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reproductor")
@RequiredArgsConstructor
@Slf4j
public class ReproductorController {

    private final ReproductorService reproductorService;

    @GetMapping("/estado/{usuarioId}")
    public ResponseEntity<EstadoReproductorDTO> obtenerEstado(@PathVariable Long usuarioId) {
        log.info("Obteniendo estado del reproductor para usuario: {}", usuarioId);

        try {
            EstadoReproductorDTO estado = reproductorService.obtenerEstado(usuarioId);
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            log.error("Error obteniendo estado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/reproducir")
    public ResponseEntity<?> reproducir(@RequestBody ComandoReproduccionRequest request) {

        log.info("Reproduciendo {} para usuario {}", request.getVideoId(), request.getUsuarioId());

        try {
            EstadoReproductorDTO estado = reproductorService.reproducir(request.getUsuarioId(), request.getVideoId());
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            log.error("Error reproduciendo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/reproducir/desde-busqueda")
    public ResponseEntity<?> reproducirDesdeBusqueda(@RequestBody ReproducirDesdeBusquedaRequest request) {

        log.info("Reproduciendo desde búsqueda: '{}', videoId: {}, usuario: {}",
            request.getTerminoBusqueda(), request.getVideoId(), request.getUsuarioId());

        try {
            Integer indice = request.getIndiceEnBusqueda() != null ? request.getIndiceEnBusqueda() : 0;
            EstadoReproductorDTO estado = reproductorService.reproducirDesdeBusqueda(
                request.getUsuarioId(),
                request.getVideoId(),
                request.getTerminoBusqueda(),
                indice
            );
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            log.error("Error reproduciendo desde búsqueda: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/reproducir-busqueda")
    public ResponseEntity<?> reproducirBusqueda(@RequestBody ReproducirDesdeBusquedaRequest request) {

        log.info("Reproduciendo búsqueda (alias): '{}', videoId: {}, usuario: {}",
            request.getTerminoBusqueda(), request.getVideoId(), request.getUsuarioId());

        try {
            Integer indice = request.getIndiceEnBusqueda() != null ? request.getIndiceEnBusqueda() : 0;
            EstadoReproductorDTO estado = reproductorService.reproducirDesdeBusqueda(
                request.getUsuarioId(),
                request.getVideoId(),
                request.getTerminoBusqueda(),
                indice
            );
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            log.error("Error reproduciendo búsqueda (alias): {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/play")
    public ResponseEntity<?> play(@RequestBody ComandoReproduccionRequest request) {
        log.info("Play para usuario {}", request.getUsuarioId());

        try {
            EstadoReproductorDTO estado = reproductorService.play(request.getUsuarioId());
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            log.error("Error en play: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/pause")
    public ResponseEntity<?> pause(@RequestBody ComandoReproduccionRequest request) {
        log.info("Pause para usuario {}", request.getUsuarioId());

        try {
            EstadoReproductorDTO estado = reproductorService.pause(request.getUsuarioId());
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            log.error("Error en pause: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/siguiente")
    public ResponseEntity<?> siguiente(@RequestBody ComandoReproduccionRequest request) {
        log.info("Siguiente canción para usuario {}", request.getUsuarioId());

        try {
            EstadoReproductorDTO estado = reproductorService.siguiente(request.getUsuarioId());
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            log.error("Error obteniendo siguiente: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/anterior")
    public ResponseEntity<?> anterior(@RequestBody ComandoReproduccionRequest request) {
        log.info("Canción anterior para usuario {}", request.getUsuarioId());

        try {
            EstadoReproductorDTO estado = reproductorService.anterior(request.getUsuarioId());
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            log.error("Error obteniendo anterior: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/posicion")
    public ResponseEntity<?> actualizarPosicion(@RequestBody ComandoReproduccionRequest request) {

        log.debug("Actualizando posición a {} para usuario {}", request.getPosicionSegundos(), request.getUsuarioId());

        try {
            EstadoReproductorDTO estado = reproductorService.actualizarPosicion(request.getUsuarioId(), request.getPosicionSegundos());
            return ResponseEntity.ok(estado);
        } catch (Exception e) {
            log.error("Error actualizando posición: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/volumen")
    public ResponseEntity<?> actualizarVolumen(@RequestBody ComandoReproduccionRequest request) {

        log.debug("Actualizando volumen a {} para usuario {}", request.getVolumen(), request.getUsuarioId());

        try {
            EstadoReproductorDTO estado = reproductorService.actualizarVolumen(request.getUsuarioId(), request.getVolumen());
            return ResponseEntity.ok(estado);
        } catch (IllegalArgumentException e) {
            log.error("Volumen inválido: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        } catch (Exception e) {
            log.error("Error actualizando volumen: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/favorito/toggle")
    public ResponseEntity<?> toggleFavorito(@RequestBody ComandoReproduccionRequest request) {
        log.info("Toggle favorito para usuario {}", request.getUsuarioId());

        try {
            EstadoReproductorDTO estado = reproductorService.toggleFavorito(request.getUsuarioId());
            return ResponseEntity.ok(Map.of(
                    "mensaje", estado.getEsFavorita() ? "Agregado a favoritos" : "Eliminado de favoritos",
                    "esFavorita", estado.getEsFavorita(),
                    "estado", estado
            ));
        } catch (Exception e) {
            log.error("Error toggle favorito: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @GetMapping("/cola/{usuarioId}")
    public ResponseEntity<?> obtenerCola(@PathVariable Long usuarioId) {
        log.info("Obteniendo cola para usuario {}", usuarioId);

        try {
            ColaReproduccionDTO cola = reproductorService.obtenerCola(usuarioId);
            return ResponseEntity.ok(cola);
        } catch (Exception e) {
            log.error("Error obteniendo cola: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/cola/agregar")
    public ResponseEntity<?> agregarACola(@RequestBody AgregarAColaRequest request) {
        log.info("Agregando {} canciones a cola de usuario {}",
            request.getVideoIds().size(), request.getUsuarioId());

        try {
            ColaReproduccionDTO cola = reproductorService.agregarACola(
                request.getUsuarioId(),
                request.getVideoIds(),
                request.getReproducirAhora()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensaje", "Canciones agregadas exitosamente",
                    "cola", cola,
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error agregando a cola: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @DeleteMapping("/cola/eliminar")
    public ResponseEntity<?> eliminarDeCola(@RequestBody EliminarDeColaRequest request) {

        log.info("Eliminando canción en índice {} de cola de usuario {}", request.getIndice(), request.getUsuarioId());

        try {
            ColaReproduccionDTO cola = reproductorService.eliminarDeCola(request.getUsuarioId(), request.getIndice());
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Canción eliminada de la cola",
                    "cola", cola,
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error eliminando de cola: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @DeleteMapping("/cola/limpiar")
    public ResponseEntity<?> limpiarCola(@RequestBody LimpiarColaRequest request) {
        log.info("Limpiando cola de usuario {}", request.getUsuarioId());

        try {
            reproductorService.limpiarCola(request.getUsuarioId());
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Cola limpiada exitosamente",
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error limpiando cola: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/modo")
    public ResponseEntity<?> cambiarModoReproduccion(@RequestBody CambiarModoRequest request) {

        log.info("Cambiando modo de reproducción a {} para usuario {}", request.getModo(), request.getUsuarioId());

        try {
            ColaReproduccionDTO cola = reproductorService.cambiarModoReproduccion(request.getUsuarioId(), request.getModo());
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Modo de reproducción actualizado",
                    "cola", cola,
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error cambiando modo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/cola/reordenar")
    public ResponseEntity<?> reordenarCola(@RequestBody ReordenarColaRequest request) {

        log.info("Reordenando cola de usuario {}: {} -> {}", request.getUsuarioId(), request.getIndiceOrigen(), request.getIndiceDestino());

        try {
            ColaReproduccionDTO cola = reproductorService.reordenarCola(request.getUsuarioId(), request.getIndiceOrigen(), request.getIndiceDestino());
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Cola reordenada exitosamente",
                    "cola", cola,
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error reordenando cola: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @PostMapping("/cola/{usuarioId}/cargar-playlist/{playlistId}")
    public ResponseEntity<?> cargarPlaylist(
            @PathVariable Long usuarioId,
            @PathVariable Long playlistId) {

        log.info("Cargando playlist {} en cola de usuario {}", playlistId, usuarioId);

        try {
            ColaReproduccionDTO cola = reproductorService.cargarPlaylist(usuarioId, playlistId);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Playlist cargada exitosamente",
                    "cola", cola,
                    "estado", "SUCCESS"
            ));
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of(
                    "mensaje", "Funcionalidad pendiente de implementación",
                    "estado", "ERROR"
            ));
        } catch (Exception e) {
            log.error("Error cargando playlist: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }
}

