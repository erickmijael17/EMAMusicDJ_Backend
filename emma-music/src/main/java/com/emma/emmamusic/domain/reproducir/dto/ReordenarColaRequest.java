package com.emma.emmamusic.domain.reproducir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReordenarColaRequest {
    private Long usuarioId;
    private Integer indiceOrigen;
    private Integer indiceDestino;
}
