package com.emma.emmamusic.domain.reproducir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancionColaDTO {

    private String videoId;
    private String titulo;
    private String canal;
    private Integer duracionSegundos;
    private String duracionTexto;
    private String miniaturaUrl;
    private Boolean esExplicita;
    private String tipoCancion;
}


