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
public class BibliotecaDTO {

    private Long usuarioId;
    private List<CancionBibliotecaDTO> favoritos;
    private List<CancionBibliotecaDTO> recientementeReproducidas;
    private EstadisticasBibliotecaDTO estadisticas;
    private Integer totalCanciones;
}

