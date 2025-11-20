package com.emma.emmamusic.domain.biblioteca.service;

import com.emma.emmamusic.domain.biblioteca.dto.BibliotecaDTO;
import com.emma.emmamusic.domain.biblioteca.dto.BibliotecaPaginadaDTO;
import com.emma.emmamusic.domain.biblioteca.dto.EstadisticasBibliotecaDTO;

public interface BibliotecaService {

    BibliotecaDTO obtenerBibliotecaCompleta(Long usuarioId);

    BibliotecaPaginadaDTO obtenerCancionesPaginadas(Long usuarioId, Integer pagina, Integer tamanio);

    BibliotecaPaginadaDTO obtenerFavoritosPaginados(Long usuarioId, Integer pagina, Integer tamanio);

    BibliotecaPaginadaDTO obtenerRecientesPaginadas(Long usuarioId, Integer pagina, Integer tamanio);

    EstadisticasBibliotecaDTO obtenerEstadisticas(Long usuarioId);
}

