package com.emma.emmamusic.domain.reproducir.controller;

import com.emma.emmamusic.domain.reproducir.dto.ResultadoReproduccionDTO;
import com.emma.emmamusic.domain.reproducir.service.StreamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stream")
@RequiredArgsConstructor
@Slf4j
public class StreamController {

    private final StreamService streamService;

    @GetMapping("/{videoId}")
    public ResponseEntity<ResultadoReproduccionDTO> obtenerStream(
            @PathVariable String videoId,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId) {

        log.info("GET /api/v1/stream/{} (usuario: {})", videoId, usuarioId);

        try {
            ResultadoReproduccionDTO resultado = streamService.obtenerUrlReproduccion(videoId, usuarioId);

            if (resultado.getTipoReproduccion() == ResultadoReproduccionDTO.TipoReproduccion.ERROR) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultado);
            }

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            log.error("Error obteniendo stream: {}", e.getMessage(), e);

            ResultadoReproduccionDTO error = ResultadoReproduccionDTO.builder()
                .idVideo(videoId)
                .titulo("Error")
                .tipoReproduccion(ResultadoReproduccionDTO.TipoReproduccion.ERROR)
                .mensaje("Error interno del servidor: " + e.getMessage())
                .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    @GetMapping("/audio/{videoId}")
    public ResponseEntity<org.springframework.core.io.Resource> streamAudio(
            @PathVariable String videoId,
            @RequestParam(value = "usuarioId", required = false) Long usuarioId) {

        log.info("Streaming audio for video: {}", videoId);

        try {
            ResultadoReproduccionDTO resultado = streamService.obtenerUrlReproduccion(videoId, usuarioId);

            if (resultado.getTipoReproduccion() == ResultadoReproduccionDTO.TipoReproduccion.ERROR || resultado.getUrlReproduccion() == null) {
                return ResponseEntity.notFound().build();
            }

            java.net.URL url = new java.net.URL(resultado.getUrlReproduccion());
            java.io.InputStream inputStream = url.openStream();
            org.springframework.core.io.InputStreamResource resource = new org.springframework.core.io.InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            log.error("Error streaming audio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

