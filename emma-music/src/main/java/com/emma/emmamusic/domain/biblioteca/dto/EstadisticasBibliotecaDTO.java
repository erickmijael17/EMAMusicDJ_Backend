package com.emma.emmamusic.domain.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasBibliotecaDTO {

    private Integer totalFavoritos;
    private Integer totalReproducciones;
    private Integer totalPlaylists;
    private Integer totalCancionesUnicas;
    private String cancionMasReproducida;
    private String artistaFavorito;
    private Integer minutosEscuchados;
}

