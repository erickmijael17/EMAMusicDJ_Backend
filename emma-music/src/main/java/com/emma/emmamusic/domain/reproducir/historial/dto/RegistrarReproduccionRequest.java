package com.emma.emmamusic.domain.reproducir.historial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarReproduccionRequest {

    private Long usuarioId;
    private String videoId;
    private Integer duracionReproducidaSegundos;
    private BigDecimal porcentajeCompletado;
    private String tipoDispositivo;
}

