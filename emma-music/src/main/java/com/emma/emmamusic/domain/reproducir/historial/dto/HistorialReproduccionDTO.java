package com.emma.emmamusic.domain.reproducir.historial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialReproduccionDTO {

    private Long reproduccionId;
    private Long usuarioId;
    private String videoId;
    private String titulo;
    private String canal;
    private String miniaturaUrl;
    private Integer duracionSegundos;
    private String duracionTexto;
    private Instant fechaReproduccion;
    private Integer duracionReproducidaSegundos;
    private BigDecimal porcentajeCompletado;
    private String tipoDispositivo;
    private String pais;
    private String ciudad;
}

