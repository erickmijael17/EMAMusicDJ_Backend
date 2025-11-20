package com.emma.emmamusic.domain.favoritos.service;

import com.emma.emmamusic.domain.favoritos.dto.FavoritoDTO;

import java.util.List;

public interface FavoritoService {

    FavoritoDTO agregarFavorito(Long usuarioId, String videoId);

    void eliminarFavorito(Long usuarioId, String videoId);

    List<FavoritoDTO> obtenerFavoritosPorUsuario(Long usuarioId);

    boolean esFavorito(Long usuarioId, String videoId);

    long contarFavoritosPorUsuario(Long usuarioId);
}

