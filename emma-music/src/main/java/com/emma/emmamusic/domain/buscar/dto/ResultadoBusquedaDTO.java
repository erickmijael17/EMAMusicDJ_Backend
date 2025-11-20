package com.emma.emmamusic.domain.buscar.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ResultadoBusquedaDTO {
    private String idVideo;
    private String titulo;
    private String canal;
    private Integer duracionSegundos;
    private String duracionTexto;
    private List<String> miniaturas;
    private Boolean esExplicito;
    private Integer contadorReproducciones;
    private Integer contadorBusquedas;
    private Instant ultimaActividad;
    private OrigenDatos origen;

    public enum OrigenDatos {
        BASE_DATOS_LOCAL,
        API_YOUTUBE
    }
}
