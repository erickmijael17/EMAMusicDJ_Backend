package com.emma.emmamusic.domain.listareproduccion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancionPlaylistDTO {

    private Long metadatoId;
    private String idVideoYoutube;
    private String titulo;
    private String canal;
    private Integer duracionSegundos;
    private String duracionTexto;
    private String miniaturaUrl;
    private Boolean esExplicito;
    private Integer posicion;
    private Instant fechaAdicion;
    private String anadidoPor;
}

