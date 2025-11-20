package com.emma.emmamusic.shared.infrastructure.python;

import com.emma.emmamusic.shared.external.youtube.dto.RespuestaPythonDTO;
import com.fasterxml.jackson.core.type.TypeReference;

public interface ClientePython {
    // El método ahora es genérico y recibe un TypeReference para saber cómo deserializar la respuesta.
    <T> RespuestaPythonDTO<T> ejecutar(PeticionPython peticion, TypeReference<RespuestaPythonDTO<T>> tipoRespuesta);
}