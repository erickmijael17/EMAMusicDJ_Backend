package com.emma.emmamusic.domain.buscar.controller;

import com.emma.emmamusic.domain.buscar.dto.ResultadoBusquedaDTO;
import com.emma.emmamusic.domain.buscar.service.BuscadorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hibrido")
@RequiredArgsConstructor
@Slf4j
public class BuscadorController {

    private final BuscadorService buscadorService;

    @GetMapping("/buscar")
    public ResponseEntity<List<ResultadoBusquedaDTO>> buscarCanciones(
            @RequestParam("q") String consulta) {

        log.info("GET /api/v1/hibrido/buscar?q={}", consulta);

        if (consulta == null || consulta.trim().isEmpty()) {
            log.warn("Consulta vacía recibida");
            return ResponseEntity.badRequest().build();
        }

        try {
            List<ResultadoBusquedaDTO> resultados = buscadorService.buscarCanciones(consulta);
            log.info("Búsqueda completada: {} resultados para '{}'", resultados.size(), consulta);
            return ResponseEntity.ok(resultados);

        } catch (Exception e) {
            log.error("Error en búsqueda para '{}': {}", consulta, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
