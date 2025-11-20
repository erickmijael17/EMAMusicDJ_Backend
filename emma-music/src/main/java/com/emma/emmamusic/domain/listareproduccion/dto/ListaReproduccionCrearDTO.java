package com.emma.emmamusic.domain.listareproduccion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListaReproduccionCrearDTO {

    private String titulo;
    private String descripcion;
    private String urlImagenPortada;
    private Boolean esPublica;
    private Boolean esColaborativa;
}

