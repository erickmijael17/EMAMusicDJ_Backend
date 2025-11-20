package com.emma.emmamusic.domain.listareproduccion.controller;

import com.emma.emmamusic.domain.listareproduccion.dto.CancionPlaylistDTO;
import com.emma.emmamusic.domain.listareproduccion.dto.ListaReproduccionCrearDTO;
import com.emma.emmamusic.domain.listareproduccion.dto.ListaReproduccionDTO;
import com.emma.emmamusic.domain.listareproduccion.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/playlists")
@RequiredArgsConstructor
@Slf4j
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<ListaReproduccionDTO> crearPlaylist(
            @RequestParam Long usuarioId,
            @RequestBody ListaReproduccionCrearDTO dto) {

        log.info("POST /api/v1/playlists - Usuario: {}", usuarioId);

        ListaReproduccionDTO playlist = playlistService.crearPlaylist(usuarioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }

    @GetMapping("/{listaId}")
    public ResponseEntity<ListaReproduccionDTO> obtenerPlaylist(@PathVariable Long listaId) {
        log.info("GET /api/v1/playlists/{}", listaId);

        ListaReproduccionDTO playlist = playlistService.obtenerPlaylist(listaId);
        return ResponseEntity.ok(playlist);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ListaReproduccionDTO>> obtenerPlaylistsPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/v1/playlists/usuario/{}", usuarioId);

        List<ListaReproduccionDTO> playlists = playlistService.obtenerPlaylistsPorUsuario(usuarioId);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/usuario/{usuarioId}/count")
    public ResponseEntity<Map<String, Object>> contarPlaylistsPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /api/v1/playlists/usuario/{}/count", usuarioId);

        long total = playlistService.contarPlaylistsPorUsuario(usuarioId);
        return ResponseEntity.ok(Map.of("count", total, "usuarioId", usuarioId));
    }

    @GetMapping("/publicas")
    public ResponseEntity<List<ListaReproduccionDTO>> obtenerPlaylistsPublicas() {
        log.info("GET /api/v1/playlists/publicas");

        List<ListaReproduccionDTO> playlists = playlistService.obtenerPlaylistsPublicas();
        return ResponseEntity.ok(playlists);
    }

    @PutMapping("/{listaId}")
    public ResponseEntity<ListaReproduccionDTO> actualizarPlaylist(
            @PathVariable Long listaId,
            @RequestParam Long usuarioId,
            @RequestBody ListaReproduccionCrearDTO dto) {

        log.info("PUT /api/v1/playlists/{}", listaId);

        ListaReproduccionDTO playlist = playlistService.actualizarPlaylist(listaId, usuarioId, dto);
        return ResponseEntity.ok(playlist);
    }

    @DeleteMapping("/{listaId}")
    public ResponseEntity<Map<String, String>> eliminarPlaylist(
            @PathVariable Long listaId,
            @RequestParam Long usuarioId) {

        log.info("DELETE /api/v1/playlists/{}", listaId);

        playlistService.eliminarPlaylist(listaId, usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Playlist eliminada"));
    }

    @PostMapping("/{listaId}/canciones")
    public ResponseEntity<CancionPlaylistDTO> agregarCancion(
            @PathVariable Long listaId,
            @RequestParam String videoId,
            @RequestParam Long usuarioId) {

        log.info("POST /api/v1/playlists/{}/canciones - VideoId: {}", listaId, videoId);

        CancionPlaylistDTO cancion = playlistService.agregarCancion(listaId, videoId, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(cancion);
    }

    @GetMapping("/{listaId}/canciones")
    public ResponseEntity<List<CancionPlaylistDTO>> obtenerCanciones(@PathVariable Long listaId) {
        log.info("GET /api/v1/playlists/{}/canciones", listaId);

        List<CancionPlaylistDTO> canciones = playlistService.obtenerCanciones(listaId);
        return ResponseEntity.ok(canciones);
    }

    @GetMapping("/{listaId}/canciones/buscar")
    public ResponseEntity<List<CancionPlaylistDTO>> buscarCancionesParaAgregar(
            @PathVariable Long listaId,
            @RequestParam String consulta) {

        log.info("GET /api/v1/playlists/{}/canciones/buscar?consulta={}", listaId, consulta);

        List<CancionPlaylistDTO> canciones = playlistService.buscarCancionesParaAgregar(listaId, consulta);
        return ResponseEntity.ok(canciones);
    }

    @DeleteMapping("/{listaId}/canciones/{videoId}")
    public ResponseEntity<Map<String, String>> eliminarCancion(
            @PathVariable Long listaId,
            @PathVariable String videoId,
            @RequestParam Long usuarioId) {

        log.info("DELETE /api/v1/playlists/{}/canciones/{}", listaId, videoId);

        playlistService.eliminarCancion(listaId, videoId, usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Canción eliminada de playlist"));
    }

    @PutMapping("/{listaId}/canciones/{videoId}/reordenar")
    public ResponseEntity<Map<String, String>> reordenarCancion(
            @PathVariable Long listaId,
            @PathVariable String videoId,
            @RequestParam Integer nuevaPosicion,
            @RequestParam Long usuarioId) {

        log.info("PUT /api/v1/playlists/{}/canciones/{}/reordenar - Nueva posición: {}",
                listaId, videoId, nuevaPosicion);

        playlistService.reordenarCancion(listaId, videoId, nuevaPosicion, usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Canción reordenada"));
    }

    @GetMapping("/{listaId}/total")
    public ResponseEntity<Map<String, Object>> contarCanciones(@PathVariable Long listaId) {
        log.info("GET /api/v1/playlists/{}/total", listaId);

        long total = playlistService.contarCancionesEnPlaylist(listaId);
        return ResponseEntity.ok(Map.of("total", total));
    }
}

