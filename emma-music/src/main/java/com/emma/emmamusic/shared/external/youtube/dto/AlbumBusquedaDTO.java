package com.emma.emmamusic.shared.external.youtube.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlbumBusquedaDTO(

    @JsonProperty("nombre")
    String nombre,

    @JsonProperty("id_album")
    String idAlbum
) {
}