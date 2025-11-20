package com.emma.emmamusic.domain.reproducir.historial.service;

import com.emma.emmamusic.domain.reproducir.historial.dto.HistorialPaginadoDTO;
import com.emma.emmamusic.domain.reproducir.historial.dto.HistorialReproduccionDTO;
import com.emma.emmamusic.domain.reproducir.historial.dto.RegistrarReproduccionRequest;

import java.util.List;

public interface HistorialReproduccionService {

    HistorialReproduccionDTO registrarReproduccion(RegistrarReproduccionRequest request);

    HistorialPaginadoDTO obtenerHistorialPaginado(Long usuarioId, int pagina, int tamanio);

    List<HistorialReproduccionDTO> obtenerHistorialReciente(Long usuarioId, int limite);

    List<HistorialReproduccionDTO> obtenerHistorialPorVideoId(Long usuarioId, String videoId);

    Long contarReproduccionesUsuario(Long usuarioId);

    void limpiarHistorial(Long usuarioId);

    void eliminarReproduccion(Long reproduccionId);
}

