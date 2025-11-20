package com.emma.emmamusic.domain.listareproduccion.service;

import com.emma.emmamusic.domain.listareproduccion.dto.ListaReproduccionDTO;
import com.emma.emmamusic.domain.listareproduccion.dto.ListaReproduccionCrearDTO;
import com.emma.emmamusic.domain.listareproduccion.dto.CancionPlaylistDTO;

import java.util.List;

public interface PlaylistService {

    ListaReproduccionDTO crearPlaylist(Long usuarioId, ListaReproduccionCrearDTO dto);

    ListaReproduccionDTO obtenerPlaylist(Long listaId);

    List<ListaReproduccionDTO> obtenerPlaylistsPorUsuario(Long usuarioId);

    List<ListaReproduccionDTO> obtenerPlaylistsPublicas();

    ListaReproduccionDTO actualizarPlaylist(Long listaId, Long usuarioId, ListaReproduccionCrearDTO dto);

    void eliminarPlaylist(Long listaId, Long usuarioId);

    CancionPlaylistDTO agregarCancion(Long listaId, String videoId, Long usuarioId);

    List<CancionPlaylistDTO> obtenerCanciones(Long listaId);

    void eliminarCancion(Long listaId, String videoId, Long usuarioId);

    void reordenarCancion(Long listaId, String videoId, Integer nuevaPosicion, Long usuarioId);

    long contarCancionesEnPlaylist(Long listaId);

    long contarPlaylistsPorUsuario(Long usuarioId);

    List<CancionPlaylistDTO> buscarCancionesParaAgregar(Long listaId, String consulta);
}

