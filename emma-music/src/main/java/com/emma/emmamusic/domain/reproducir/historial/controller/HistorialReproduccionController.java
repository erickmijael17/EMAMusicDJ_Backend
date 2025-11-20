package com.emma.emmamusic.domain.reproducir.historial.controller;

import com.emma.emmamusic.domain.reproducir.historial.dto.HistorialPaginadoDTO;
import com.emma.emmamusic.domain.reproducir.historial.dto.HistorialReproduccionDTO;
import com.emma.emmamusic.domain.reproducir.historial.dto.RegistrarReproduccionRequest;
import com.emma.emmamusic.domain.reproducir.historial.service.HistorialReproduccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/historial")
@RequiredArgsConstructor
@Slf4j
public class HistorialReproduccionController {

    private final HistorialReproduccionService historialService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarReproduccion(@RequestBody RegistrarReproduccionRequest request) {
        log.info("POST /api/v1/historial/registrar - Usuario: {}, VideoId: {}",
                request.getUsuarioId(), request.getVideoId());

        try {
            HistorialReproduccionDTO resultado = historialService.registrarReproduccion(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensaje", "Reproducción registrada exitosamente",
                    "historial", resultado,
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error registrando reproducción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerHistorialPaginado(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio) {

        log.info("GET /api/v1/historial/usuario/{} - Página: {}, Tamaño: {}",
                usuarioId, pagina, tamanio);

        try {
            HistorialPaginadoDTO historial = historialService
                    .obtenerHistorialPaginado(usuarioId, pagina, tamanio);

            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            log.error("Error obteniendo historial paginado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @GetMapping("/usuario/{usuarioId}/reciente")
    public ResponseEntity<?> obtenerHistorialReciente(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "20") int limite) {

        log.info("GET /api/v1/historial/usuario/{}/reciente - Límite: {}", usuarioId, limite);

        try {
            List<HistorialReproduccionDTO> historial = historialService
                    .obtenerHistorialReciente(usuarioId, limite);

            return ResponseEntity.ok(Map.of(
                    "historial", historial,
                    "total", historial.size(),
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error obteniendo historial reciente: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @GetMapping("/usuario/{usuarioId}/video/{videoId}")
    public ResponseEntity<?> obtenerHistorialPorVideo(
            @PathVariable Long usuarioId,
            @PathVariable String videoId) {

        log.info("GET /api/v1/historial/usuario/{}/video/{}", usuarioId, videoId);

        try {
            List<HistorialReproduccionDTO> historial = historialService
                    .obtenerHistorialPorVideoId(usuarioId, videoId);

            return ResponseEntity.ok(Map.of(
                    "historial", historial,
                    "total", historial.size(),
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error obteniendo historial por video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @GetMapping("/usuario/{usuarioId}/contador")
    public ResponseEntity<?> contarReproducciones(@PathVariable Long usuarioId) {
        log.info("GET /api/v1/historial/usuario/{}/contador", usuarioId);

        try {
            Long total = historialService.contarReproduccionesUsuario(usuarioId);

            return ResponseEntity.ok(Map.of(
                    "usuarioId", usuarioId,
                    "totalReproducciones", total,
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error contando reproducciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @DeleteMapping("/usuario/{usuarioId}/limpiar")
    public ResponseEntity<?> limpiarHistorial(@PathVariable Long usuarioId) {
        log.info("DELETE /api/v1/historial/usuario/{}/limpiar", usuarioId);

        try {
            historialService.limpiarHistorial(usuarioId);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Historial limpiado exitosamente",
                    "usuarioId", usuarioId,
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error limpiando historial: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }

    @DeleteMapping("/{reproduccionId}")
    public ResponseEntity<?> eliminarReproduccion(@PathVariable Long reproduccionId) {
        log.info("DELETE /api/v1/historial/{}", reproduccionId);

        try {
            historialService.eliminarReproduccion(reproduccionId);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Reproducción eliminada exitosamente",
                    "reproduccionId", reproduccionId,
                    "estado", "SUCCESS"
            ));
        } catch (Exception e) {
            log.error("Error eliminando reproducción: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "mensaje", e.getMessage(),
                    "estado", "ERROR"
            ));
        }
    }
}

