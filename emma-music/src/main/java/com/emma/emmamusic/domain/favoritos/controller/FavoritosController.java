package com.emma.emmamusic.domain.favoritos.controller;

import com.emma.emmamusic.domain.favoritos.dto.AgregarFavoritoRequest;
import com.emma.emmamusic.domain.favoritos.dto.FavoritoDTO;
import com.emma.emmamusic.domain.favoritos.service.FavoritoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/favoritos")
@RequiredArgsConstructor
@Slf4j
public class FavoritosController {

    private final FavoritoService favoritoService;

    @PostMapping
    public ResponseEntity<?> agregarFavorito(@RequestBody AgregarFavoritoRequest request) {
        log.info("POST /api/v1/favoritos - Usuario: {}, VideoId: {}",
                request.getUsuarioId(), request.getIdVideoYoutube());

        FavoritoDTO nuevoFavorito = favoritoService.agregarFavorito(
                request.getUsuarioId(),
                request.getIdVideoYoutube()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Favorito agregado",
                "favorito", nuevoFavorito
        ));
    }

    @DeleteMapping("/{usuarioId}/{videoId}")
    public ResponseEntity<Map<String, String>> eliminarFavorito(
            @PathVariable Long usuarioId,
            @PathVariable String videoId) {

        log.info("DELETE /api/v1/favoritos/{}/{}", usuarioId, videoId);

        favoritoService.eliminarFavorito(usuarioId, videoId);

        return ResponseEntity.ok(Map.of("mensaje", "Favorito eliminado"));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Map<String, Object>> obtenerFavoritos(@PathVariable Long usuarioId) {
        log.info("GET /api/v1/favoritos/usuario/{}", usuarioId);

        List<FavoritoDTO> favoritos = favoritoService.obtenerFavoritosPorUsuario(usuarioId);

        return ResponseEntity.ok(Map.of(
                "favoritos", favoritos,
                "total", favoritos.size(),
                "usuarioId", usuarioId
        ));
    }

    @GetMapping("/hibrido/usuario/{usuarioId}")
    public ResponseEntity<Map<String, Object>> obtenerFavoritosHibrido(@PathVariable Long usuarioId) {
        log.info("GET /api/v1/favoritos/hibrido/usuario/{}", usuarioId);

        List<FavoritoDTO> favoritos = favoritoService.obtenerFavoritosPorUsuario(usuarioId);

        return ResponseEntity.ok(Map.of(
                "favoritos", favoritos,
                "total", favoritos.size(),
                "usuarioId", usuarioId
        ));
    }

    @GetMapping("/verificar/{usuarioId}/{videoId}")
    public ResponseEntity<Map<String, Object>> verificarFavorito(
            @PathVariable Long usuarioId,
            @PathVariable String videoId) {

        log.info("GET /api/v1/favoritos/verificar/{}/{}", usuarioId, videoId);

        boolean esFavorito = favoritoService.esFavorito(usuarioId, videoId);

        return ResponseEntity.ok(Map.of(
                "esFavorito", esFavorito,
                "usuarioId", usuarioId,
                "videoId", videoId
        ));
    }

    @GetMapping("/contar/{usuarioId}")
    public ResponseEntity<Map<String, Object>> contarFavoritos(@PathVariable Long usuarioId) {
        log.info("GET /api/v1/favoritos/contar/{}", usuarioId);

        long totalFavoritos = favoritoService.contarFavoritosPorUsuario(usuarioId);

        return ResponseEntity.ok(Map.of(
                "total", totalFavoritos,
                "usuarioId", usuarioId
        ));
    }
}

