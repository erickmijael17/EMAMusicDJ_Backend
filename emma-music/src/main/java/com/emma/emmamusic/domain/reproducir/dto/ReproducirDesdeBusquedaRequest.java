package com.emma.emmamusic.domain.reproducir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReproducirDesdeBusquedaRequest {
    private Long usuarioId;
    private String videoId;
    private String terminoBusqueda;
    private Integer indiceEnBusqueda;
}

