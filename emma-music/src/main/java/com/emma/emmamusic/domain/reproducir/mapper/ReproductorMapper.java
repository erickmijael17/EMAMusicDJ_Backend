package com.emma.emmamusic.domain.reproducir.mapper;

import com.emma.emmamusic.domain.reproducir.dto.CancionColaDTO;
import com.emma.emmamusic.domain.reproducir.dto.ColaReproduccionDTO;
import com.emma.emmamusic.domain.reproducir.dto.EstadoReproductorDTO;
import com.emma.emmamusic.domain.reproducir.model.ColaReproduccion;
import com.emma.emmamusic.domain.reproducir.model.EstadoReproductor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReproductorMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ColaReproduccionDTO toColaDTO(ColaReproduccion cola) {
        if (cola == null) {
            return null;
        }

        List<CancionColaDTO> cancionesList = parsearCanciones(cola.getCanciones());

        return ColaReproduccionDTO.builder()
                .colaId(cola.getColaId())
                .usuarioId(cola.getUsuario() != null ? cola.getUsuario().getUsuarioId() : null)
                .canciones(cancionesList)
                .indiceActual(cola.getIndiceActual())
                .modoReproduccion(mapModoReproduccion(cola.getModoReproduccion()))
                .fechaCreacion(cola.getFechaCreacion())
                .fechaActualizacion(cola.getFechaActualizacion())
                .build();
    }

    public EstadoReproductorDTO toEstadoDTO(EstadoReproductor estado) {
        if (estado == null) {
            return null;
        }

        return EstadoReproductorDTO.builder()
                .estadoId(estado.getEstadoId())
                .usuarioId(estado.getUsuario() != null ? estado.getUsuario().getUsuarioId() : null)
                .videoIdActual(estado.getVideoIdActual())
                .tituloActual(estado.getTituloActual())
                .canalActual(estado.getCanalActual())
                .miniaturaUrl(estado.getMiniaturaUrl())
                .duracionSegundos(estado.getDuracionSegundos())
                .estaReproduciendo(estado.getEstaReproduciendo())
                .posicionSegundos(estado.getPosicionSegundos())
                .volumen(estado.getVolumen())
                .esFavorita(estado.getEsFavorita())
                .fechaActualizacion(estado.getFechaActualizacion())
                .build();
    }

    public String serializarCanciones(List<CancionColaDTO> canciones) {
        try {
            if (canciones == null || canciones.isEmpty()) {
                return "[]";
            }
            return objectMapper.writeValueAsString(canciones);
        } catch (Exception e) {
            log.error("Error serializando canciones: {}", e.getMessage());
            return "[]";
        }
    }

    public List<CancionColaDTO> parsearCanciones(String cancionesJson) {
        try {
            if (cancionesJson == null || cancionesJson.trim().isEmpty() || "[]".equals(cancionesJson.trim())) {
                return new ArrayList<>();
            }

            List<Map<String, Object>> rawList = objectMapper.readValue(
                cancionesJson,
                new TypeReference<List<Map<String, Object>>>() {}
            );

            return rawList.stream()
                    .map(this::mapToCancionColaDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error parseando canciones JSON: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private CancionColaDTO mapToCancionColaDTO(Map<String, Object> map) {
        return CancionColaDTO.builder()
                .videoId((String) map.get("videoId"))
                .titulo((String) map.get("titulo"))
                .canal((String) map.get("canal"))
                .duracionSegundos((Integer) map.get("duracionSegundos"))
                .duracionTexto((String) map.get("duracionTexto"))
                .miniaturaUrl((String) map.get("miniaturaUrl"))
                .esExplicita((Boolean) map.get("esExplicita"))
                .tipoCancion((String) map.get("tipoCancion"))
                .build();
    }

    private ColaReproduccionDTO.ModoReproduccion mapModoReproduccion(ColaReproduccion.ModoReproduccion modo) {
        if (modo == null) {
            return ColaReproduccionDTO.ModoReproduccion.NORMAL;
        }
        return ColaReproduccionDTO.ModoReproduccion.valueOf(modo.name());
    }
}

