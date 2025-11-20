package com.emma.emmamusic.shared.external.youtube.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO genérico que mapea la estructura de respuesta estándar de los scripts de Python.
 * Utiliza un tipo genérico <T> para el campo 'datos', permitiendo reutilizarlo
 * para diferentes tipos de respuestas (búsqueda, stream, etc.).
 *
 * @param <T> El tipo de objeto contenido en el campo 'datos'.
 */
public record RespuestaPythonDTO<T>(

    @JsonProperty("exito")
    boolean exito,

    @JsonProperty("tipo_respuesta")
    String tipoRespuesta,

    @JsonProperty("version_api")
    String versionApi,

    @JsonProperty("timestamp")
    String timestamp,

    @JsonProperty("datos")
    T datos,

    @JsonProperty("error")
    String error,

    @JsonProperty("detalle")
    String detalle
) {
}