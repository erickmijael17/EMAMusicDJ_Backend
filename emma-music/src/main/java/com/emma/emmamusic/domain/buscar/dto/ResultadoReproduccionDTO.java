package com.emma.emmamusic.domain.buscar.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResultadoReproduccionDTO {
    private String idVideo;
    private String titulo;
    private String urlReproduccion;
    private TipoReproduccion tipoReproduccion;
    private String calidad;
    private Integer duracionSegundos;
    private String mensaje;

    public enum TipoReproduccion {
        ARCHIVO_LOCAL,
        STREAM_ONLINE,
        ERROR
    }
}
