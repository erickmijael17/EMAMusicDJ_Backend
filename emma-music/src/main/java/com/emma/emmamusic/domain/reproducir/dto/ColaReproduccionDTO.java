package com.emma.emmamusic.domain.reproducir.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColaReproduccionDTO {

    private Long colaId;
    private Long usuarioId;
    private List<CancionColaDTO> canciones;
    private Integer indiceActual;
    private ModoReproduccion modoReproduccion;
    private ContextoOrigen contextoOrigen;
    private String terminoBusqueda;
    private Long playlistId;
    private Long albumId;
    private Instant fechaCreacion;
    private Instant fechaActualizacion;

    public enum ModoReproduccion {
        NORMAL,
        ALEATORIO,
        REPETIR_UNA,
        REPETIR_TODAS
    }

    public enum ContextoOrigen {
        BUSQUEDA,
        PLAYLIST,
        ALBUM,
        ARTISTA,
        FAVORITOS,
        HISTORIAL,
        MANUAL
    }

    public CancionColaDTO getCancionActual() {
        if (canciones == null || canciones.isEmpty() || indiceActual == null) {
            return null;
        }
        if (indiceActual >= 0 && indiceActual < canciones.size()) {
            return canciones.get(indiceActual);
        }
        return null;
    }

    public boolean tieneSiguiente() {
        if (canciones == null || canciones.isEmpty()) {
            return false;
        }
        return indiceActual < canciones.size() - 1 ||
               modoReproduccion == ModoReproduccion.REPETIR_TODAS;
    }

    public boolean tieneAnterior() {
        if (canciones == null || canciones.isEmpty()) {
            return false;
        }
        return indiceActual > 0 ||
               modoReproduccion == ModoReproduccion.REPETIR_TODAS;
    }
}

