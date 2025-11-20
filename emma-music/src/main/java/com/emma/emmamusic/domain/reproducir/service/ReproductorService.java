package com.emma.emmamusic.domain.reproducir.service;

import com.emma.emmamusic.domain.reproducir.dto.*;

import java.util.List;

public interface ReproductorService {

    EstadoReproductorDTO obtenerEstado(Long usuarioId);

    EstadoReproductorDTO reproducir(Long usuarioId, String videoId);

    EstadoReproductorDTO reproducirDesdeBusqueda(Long usuarioId, String videoId, String terminoBusqueda, Integer indiceEnBusqueda);

    EstadoReproductorDTO play(Long usuarioId);

    EstadoReproductorDTO pause(Long usuarioId);

    EstadoReproductorDTO siguiente(Long usuarioId);

    EstadoReproductorDTO anterior(Long usuarioId);

    EstadoReproductorDTO actualizarPosicion(Long usuarioId, Integer posicionSegundos);

    EstadoReproductorDTO actualizarVolumen(Long usuarioId, Integer volumen);

    EstadoReproductorDTO toggleFavorito(Long usuarioId);

    ColaReproduccionDTO obtenerCola(Long usuarioId);

    ColaReproduccionDTO agregarACola(Long usuarioId, List<String> videoIds, Boolean reproducirAhora);

    ColaReproduccionDTO eliminarDeCola(Long usuarioId, Integer indice);

    void limpiarCola(Long usuarioId);

    ColaReproduccionDTO cargarPlaylist(Long usuarioId, Long playlistId);

    ColaReproduccionDTO cambiarModoReproduccion(Long usuarioId, ColaReproduccionDTO.ModoReproduccion modo);

    ColaReproduccionDTO reordenarCola(Long usuarioId, Integer indiceOrigen, Integer indiceDestino);
}

