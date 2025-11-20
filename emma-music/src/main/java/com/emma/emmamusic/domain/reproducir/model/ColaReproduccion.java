package com.emma.emmamusic.domain.reproducir.model;

import com.emma.emmamusic.domain.auth.model.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cola_reproduccion")
@Slf4j
public class ColaReproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cola_id")
    private Long colaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario usuario;

    @Type(JsonType.class)
    @Column(name = "canciones", columnDefinition = "jsonb", nullable = false)
    private String canciones = "[]";

    @Column(name = "indice_actual")
    private Integer indiceActual = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "modo_reproduccion", length = 20)
    private ModoReproduccion modoReproduccion = ModoReproduccion.NORMAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "contexto_origen", length = 50)
    private ContextoOrigen contextoOrigen;

    @Column(name = "termino_busqueda", length = 500)
    private String terminoBusqueda;

    @Column(name = "playlist_id")
    private Long playlistId;

    @Column(name = "album_id")
    private Long albumId;

    @Column(name = "fecha_creacion", updatable = false)
    private Instant fechaCreacion;

    @Column(name = "fecha_actualizacion")
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

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (fechaCreacion == null) {
            fechaCreacion = now;
        }
        if (fechaActualizacion == null) {
            fechaActualizacion = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = Instant.now();
    }

    public List<Map<String, Object>> getCancionesAsList() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(canciones, List.class);
        } catch (JsonProcessingException e) {
            log.error("Error parseando canciones JSON: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    public void setCancionesFromList(List<Map<String, Object>> listaCanciones) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.canciones = mapper.writeValueAsString(listaCanciones);
        } catch (JsonProcessingException e) {
            log.error("Error serializando canciones a JSON: {}", e.getMessage());
            this.canciones = "[]";
        }
    }
}

