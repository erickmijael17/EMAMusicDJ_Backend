package com.emma.emmamusic.domain.biblioteca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancionBibliotecaDTO {

    private Long metadatoId;
    private String videoId;
    private String titulo;
    private String canal;
    private Integer duracionSegundos;
    private String duracionTexto;
    private String miniaturaUrl;
    private Boolean esExplicita;
    private Integer contadorReproducciones;
    private String fechaAdicion;
    private Boolean esFavorita;
    private Integer vecesReproducida;
}

