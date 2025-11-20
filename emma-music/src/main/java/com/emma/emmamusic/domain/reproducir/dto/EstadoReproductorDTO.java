package com.emma.emmamusic.domain.reproducir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoReproductorDTO {

    private Long estadoId;
    private Long usuarioId;
    private String videoIdActual;
    private String tituloActual;
    private String canalActual;
    private String miniaturaUrl;
    private Integer duracionSegundos;
    private Boolean estaReproduciendo;
    private Integer posicionSegundos;
    private Integer volumen;
    private Boolean esFavorita;
    private Instant fechaActualizacion;

    private String urlReproduccion;
    private String tipoReproduccion;

    private Integer indiceEnCola;
    private Integer totalEnCola;
    private Boolean tieneSiguiente;
    private Boolean tieneAnterior;
    private ColaReproduccionDTO.ModoReproduccion modoReproduccion;
}

