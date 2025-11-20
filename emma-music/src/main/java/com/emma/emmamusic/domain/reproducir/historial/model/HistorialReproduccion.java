package com.emma.emmamusic.domain.reproducir.historial.model;

import com.emma.emmamusic.domain.auth.model.Usuario;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "historial_reproducciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialReproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reproduccion_id")
    private Long reproduccionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metadato_id", nullable = false)
    private MetadatoYoutube metadatoYoutube;

    @Column(name = "id_video_youtube", length = 50)
    private String idVideoYoutube;

    @CreationTimestamp
    @Column(name = "fecha_reproduccion", nullable = false, updatable = false)
    private Instant fechaReproduccion;

    @Column(name = "duracion_reproducida_segundos")
    private Integer duracionReproducidaSegundos;

    @Column(name = "porcentaje_completado", precision = 5, scale = 2)
    private BigDecimal porcentajeCompletado;

    @Column(name = "tipo_dispositivo", length = 50)
    private String tipoDispositivo;

    @Column(name = "direccion_ip", length = 50)
    private String direccionIp;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "ciudad", length = 100)
    private String ciudad;
}

