package com.emma.emmamusic.domain.biblioteca.controller;

import com.emma.emmamusic.domain.biblioteca.dto.BibliotecaDTO;
import com.emma.emmamusic.domain.biblioteca.dto.BibliotecaPaginadaDTO;
import com.emma.emmamusic.domain.biblioteca.dto.EstadisticasBibliotecaDTO;
import com.emma.emmamusic.domain.biblioteca.service.BibliotecaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/biblioteca")
@RequiredArgsConstructor
@Slf4j
public class BibliotecaController {

    private final BibliotecaService bibliotecaService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<BibliotecaDTO> obtenerBibliotecaCompleta(@PathVariable Long usuarioId) {
        log.info("GET /api/v1/biblioteca/usuario/{}", usuarioId);

        try {
            BibliotecaDTO biblioteca = bibliotecaService.obtenerBibliotecaCompleta(usuarioId);
            return ResponseEntity.ok(biblioteca);
        } catch (Exception e) {
            log.error("Error obteniendo biblioteca completa: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}/canciones")
    public ResponseEntity<BibliotecaPaginadaDTO> obtenerCancionesPaginadas(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "20") Integer tamanio) {

        log.info("GET /api/v1/biblioteca/usuario/{}/canciones?pagina={}&tamanio={}",
                usuarioId, pagina, tamanio);

        try {
            BibliotecaPaginadaDTO resultado = bibliotecaService
                    .obtenerCancionesPaginadas(usuarioId, pagina, tamanio);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error obteniendo canciones paginadas: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}/favoritos")
    public ResponseEntity<BibliotecaPaginadaDTO> obtenerFavoritosPaginados(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "20") Integer tamanio) {

        log.info("GET /api/v1/biblioteca/usuario/{}/favoritos?pagina={}&tamanio={}",
                usuarioId, pagina, tamanio);

        try {
            BibliotecaPaginadaDTO resultado = bibliotecaService
                    .obtenerFavoritosPaginados(usuarioId, pagina, tamanio);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error obteniendo favoritos paginados: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}/recientes")
    public ResponseEntity<BibliotecaPaginadaDTO> obtenerRecientesPaginadas(
            @PathVariable Long usuarioId,
            @RequestParam(defaultValue = "0") Integer pagina,
            @RequestParam(defaultValue = "20") Integer tamanio) {

        log.info("GET /api/v1/biblioteca/usuario/{}/recientes?pagina={}&tamanio={}",
                usuarioId, pagina, tamanio);

        try {
            BibliotecaPaginadaDTO resultado = bibliotecaService
                    .obtenerRecientesPaginadas(usuarioId, pagina, tamanio);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error obteniendo recientes paginadas: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}/estadisticas")
    public ResponseEntity<EstadisticasBibliotecaDTO> obtenerEstadisticas(
            @PathVariable Long usuarioId) {

        log.info("GET /api/v1/biblioteca/usuario/{}/estadisticas", usuarioId);

        try {
            EstadisticasBibliotecaDTO estadisticas = bibliotecaService.obtenerEstadisticas(usuarioId);
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            log.error("Error obteniendo estadísticas: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}

