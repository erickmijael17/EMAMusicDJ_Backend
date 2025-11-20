package com.emma.emmamusic.domain.listareproduccion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListaReproduccionDTO {

    private Long listaId;
    private Long usuarioId;
    private String nombreUsuario;
    private String titulo;
    private String descripcion;
    private String urlImagenPortada;
    private Boolean esPublica;
    private Boolean esColaborativa;
    private Instant fechaCreacion;
    private Instant fechaActualizacion;
    private Integer totalCanciones;
    private List<CancionPlaylistDTO> canciones;
}

