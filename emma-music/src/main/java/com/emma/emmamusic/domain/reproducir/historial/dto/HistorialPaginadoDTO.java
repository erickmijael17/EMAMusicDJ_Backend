package com.emma.emmamusic.domain.reproducir.historial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPaginadoDTO {

    private List<HistorialReproduccionDTO> contenido;
    private int paginaActual;
    private int tamanioPagina;
    private long totalElementos;
    private int totalPaginas;
    private boolean esUltimaPagina;
    private boolean esPrimeraPagina;
}

