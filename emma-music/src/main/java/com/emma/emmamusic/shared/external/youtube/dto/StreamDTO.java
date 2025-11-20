package com.emma.emmamusic.shared.external.youtube.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StreamDTO(

    @JsonProperty("id_video")
    String idVideo,

    @JsonProperty("url_stream")
    String urlStream,

    @JsonProperty("titulo")
    String titulo,

    @JsonProperty("descripcion")
    String descripcion,

    @JsonProperty("duracion_segundos")
    Integer duracionSegundos,

    @JsonProperty("miniatura")
    String miniatura,

    @JsonProperty("canal")
    String canal,

    @JsonProperty("id_canal")
    String idCanal,

    @JsonProperty("uploader")
    String uploader,

    @JsonProperty("vistas")
    Long vistas,

    @JsonProperty("likes")
    Long likes,

    @JsonProperty("fecha_subida")
    String fechaSubida,

    @JsonProperty("extension")
    String extension,

    @JsonProperty("codec_audio")
    String codecAudio,

    @JsonProperty("frecuencia_muestreo_hz")
    Integer frecuenciaMuestreoHz,

    @JsonProperty("bitrate_kbps")
    Double bitrateKbps,

    @JsonProperty("calidad")
    String calidad
) {
}