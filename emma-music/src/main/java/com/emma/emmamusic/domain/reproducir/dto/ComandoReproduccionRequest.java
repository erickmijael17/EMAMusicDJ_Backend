package com.emma.emmamusic.domain.reproducir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComandoReproduccionRequest {

    private Long usuarioId;
    private TipoComando comando;
    private String videoId;
    private Integer posicionSegundos;
    private Integer volumen;

    public enum TipoComando {
        PLAY,
        PAUSE,
        SIGUIENTE,
        ANTERIOR,
        REPRODUCIR,
        SEEK,
        VOLUMEN
    }
}

