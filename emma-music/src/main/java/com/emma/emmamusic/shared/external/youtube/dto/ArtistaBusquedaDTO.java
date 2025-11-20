package com.emma.emmamusic.shared.external.youtube.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArtistaBusquedaDTO(

    @JsonProperty("nombre")
    String nombre,

    @JsonProperty("id_artista")
    String idArtista
) {
}