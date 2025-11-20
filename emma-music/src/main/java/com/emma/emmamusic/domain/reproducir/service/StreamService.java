package com.emma.emmamusic.domain.reproducir.service;

import com.emma.emmamusic.domain.reproducir.dto.ResultadoReproduccionDTO;

public interface StreamService {

    ResultadoReproduccionDTO obtenerUrlReproduccion(String videoId, Long usuarioId);
}

