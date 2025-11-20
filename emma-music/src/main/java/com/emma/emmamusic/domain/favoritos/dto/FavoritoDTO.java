package com.emma.emmamusic.domain.favoritos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoDTO {

    private String idVideoYoutube;
    private String titulo;
    private String canal;
    private Integer duracionSegundos;
    private String duracionTexto;
    private String miniaturaUrl;
    private Boolean esExplicito;
    private Instant fechaAdicion;
    private String origen;
}

