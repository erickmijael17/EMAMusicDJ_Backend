package com.emma.emmamusic.domain.buscar.service;

import com.emma.emmamusic.domain.buscar.dto.ResultadoBusquedaDTO;

import java.util.List;

public interface BuscadorService {

    List<ResultadoBusquedaDTO> buscarCanciones(String consulta);
}


