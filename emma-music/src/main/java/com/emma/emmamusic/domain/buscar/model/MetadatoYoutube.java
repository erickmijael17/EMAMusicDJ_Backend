package com.emma.emmamusic.domain.buscar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "metadatos_youtube")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadatoYoutube {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metadato_id")
    private Long metadatoId;

    @Column(name = "id_video_youtube", unique = true, nullable = false, length = 50)
    private String idVideoYoutube;

    @Column(name = "titulo", nullable = false, length = 500)
    private String titulo;

    @Column(name = "canal", length = 200)
    private String canal;

    @Column(name = "duracion_segundos")
    private Integer duracionSegundos;

    @Column(name = "duracion_texto", length = 20)
    private String duracionTexto;

    @Column(name = "miniatura_url", length = 500)
    private String miniaturaUrl;

    @Column(name = "es_explicito")
    private Boolean esExplicito;

    @Column(name = "contador_reproducciones")
    private Integer contadorReproducciones;

    @Column(name = "contador_busquedas")
    private Integer contadorBusquedas;

    @Column(name = "contador_agregadas_playlists")
    private Integer contadorAgregadasPlaylists;

    @Column(name = "ultima_actividad")
    private Instant ultimaActividad;

    @CreationTimestamp
    @Column(name = "fecha_creacion")
    private Instant fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_ultima_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        fechaCreacion = now;
        fechaActualizacion = now;
        ultimaActividad = Instant.now();
        if (contadorReproducciones == null) contadorReproducciones = 0;
        if (contadorBusquedas == null) contadorBusquedas = 0;
        if (contadorAgregadasPlaylists == null) contadorAgregadasPlaylists = 0;
        if (esExplicito == null) esExplicito = false;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = Instant.now();
        ultimaActividad = Instant.now();
    }
}
