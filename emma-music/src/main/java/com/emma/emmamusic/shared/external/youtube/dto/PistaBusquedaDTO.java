package com.emma.emmamusic.shared.external.youtube.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PistaBusquedaDTO(

    @JsonProperty("id_video")
    String idVideo,

    @JsonProperty("titulo")
    String titulo,

    @JsonProperty("categoria")
    String categoria,

    @JsonProperty("tipo_resultado")
    String tipoResultado,

    @JsonProperty("artistas")
    List<ArtistaBusquedaDTO> artistas,

    @JsonProperty("album")
    AlbumBusquedaDTO album,

    @JsonProperty("duracion")
    String duracionTexto,

    @JsonProperty("duracion_segundos")
    Integer duracionSegundos,

    @JsonProperty("vistas")
    String vistas, // Se mapea como String porque puede venir como "1.2M views"

    @JsonProperty("es_explicito")
    Boolean esExplicito,

    @JsonProperty("miniaturas")
    List<String> miniaturas,

    @JsonProperty("tokens_feedback")
    Object tokensFeedback
) {
}