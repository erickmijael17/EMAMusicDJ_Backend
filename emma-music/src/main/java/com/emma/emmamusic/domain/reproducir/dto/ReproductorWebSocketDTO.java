package com.emma.emmamusic.domain.reproducir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReproductorWebSocketDTO {

    private String tipo;
    private EstadoReproductorDTO estado;
    private String mensaje;
    private Long timestamp;

    public static ReproductorWebSocketDTO cargando(EstadoReproductorDTO estado) {
        return ReproductorWebSocketDTO.builder()
                .tipo("CARGANDO")
                .estado(estado)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ReproductorWebSocketDTO listo(EstadoReproductorDTO estado) {
        return ReproductorWebSocketDTO.builder()
                .tipo("LISTO")
                .estado(estado)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ReproductorWebSocketDTO error(String mensajeError) {
        return ReproductorWebSocketDTO.builder()
                .tipo("ERROR")
                .mensaje(mensajeError)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static ReproductorWebSocketDTO actualizado(EstadoReproductorDTO estado) {
        return ReproductorWebSocketDTO.builder()
                .tipo("ACTUALIZADO")
                .estado(estado)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}

