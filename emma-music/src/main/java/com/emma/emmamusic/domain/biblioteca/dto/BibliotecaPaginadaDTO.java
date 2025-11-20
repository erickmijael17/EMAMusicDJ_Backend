package com.emma.emmamusic.domain.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BibliotecaPaginadaDTO {

    private List<CancionBibliotecaDTO> canciones;
    private Integer paginaActual;
    private Integer tamanoPagina;
    private Integer totalPaginas;
    private Long totalElementos;
    private Boolean esUltimaPagina;
    private Boolean esPrimeraPagina;
}

