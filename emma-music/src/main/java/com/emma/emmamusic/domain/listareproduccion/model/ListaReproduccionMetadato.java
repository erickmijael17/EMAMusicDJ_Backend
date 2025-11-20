package com.emma.emmamusic.domain.listareproduccion.model;

import com.emma.emmamusic.domain.auth.model.Usuario;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "listas_reproduccion_metadatos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListaReproduccionMetadato {

    @EmbeddedId
    private ListaReproduccionMetadatoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("listaId")
    @JoinColumn(name = "lista_id")
    private ListaReproduccion listaReproduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("metadatoId")
    @JoinColumn(name = "metadato_id")
    private MetadatoYoutube metadatoYoutube;

    @Column(name = "posicion", nullable = false)
    private Integer posicion;

    @CreationTimestamp
    @Column(name = "fecha_adicion", updatable = false)
    private Instant fechaAdicion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anadido_por_usuario_id")
    private Usuario anadidoPorUsuario;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListaReproduccionMetadatoId implements Serializable {
        @Column(name = "lista_id")
        private Long listaId;

        @Column(name = "metadato_id")
        private Long metadatoId;
    }
}

