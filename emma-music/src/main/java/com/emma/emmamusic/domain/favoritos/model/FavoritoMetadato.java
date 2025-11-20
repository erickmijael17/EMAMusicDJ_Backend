package com.emma.emmamusic.domain.favoritos.model;

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
@Table(name = "favoritos_metadatos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoritoMetadato {

    @EmbeddedId
    private FavoritoMetadatoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("metadatoId")
    @JoinColumn(name = "metadato_id", nullable = false)
    private MetadatoYoutube metadatoYoutube;

    @CreationTimestamp
    @Column(name = "fecha_adicion", nullable = false, updatable = false)
    private Instant fechaAdicion;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoritoMetadatoId implements Serializable {
        @Column(name = "usuario_id")
        private Long usuarioId;

        @Column(name = "metadato_id")
        private Long metadatoId;
    }
}

