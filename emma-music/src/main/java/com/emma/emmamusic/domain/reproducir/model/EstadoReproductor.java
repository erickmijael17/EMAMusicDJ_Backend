package com.emma.emmamusic.domain.reproducir.model;

import com.emma.emmamusic.domain.auth.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estado_reproductor")
public class EstadoReproductor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estado_id")
    private Long estadoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario usuario;

    @Column(name = "video_id_actual", length = 50)
    private String videoIdActual;

    @Column(name = "titulo_actual", length = 500)
    private String tituloActual;

    @Column(name = "canal_actual", length = 255)
    private String canalActual;

    @Column(name = "miniatura_url", columnDefinition = "TEXT")
    private String miniaturaUrl;

    @Column(name = "duracion_segundos")
    private Integer duracionSegundos;

    @Column(name = "esta_reproduciendo")
    private Boolean estaReproduciendo = false;

    @Column(name = "posicion_segundos")
    private Integer posicionSegundos = 0;

    @Column(name = "volumen")
    private Integer volumen = 80;

    @Column(name = "es_favorita")
    private Boolean esFavorita = false;

    @Column(name = "fecha_actualizacion")
    private Instant fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        if (fechaActualizacion == null) {
            fechaActualizacion = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = Instant.now();
    }

    public void play() {
        this.estaReproduciendo = true;
    }

    public void pause() {
        this.estaReproduciendo = false;
    }

    public void actualizarCancion(String videoId, String titulo, String canal, String miniatura, Integer duracion) {
        this.videoIdActual = videoId;
        this.tituloActual = titulo;
        this.canalActual = canal;
        this.miniaturaUrl = miniatura;
        this.duracionSegundos = duracion;
        this.posicionSegundos = 0;
    }
}

