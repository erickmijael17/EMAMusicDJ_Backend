package com.emma.emmamusic.domain.reproducir.historial.repository;

import com.emma.emmamusic.domain.reproducir.historial.model.HistorialReproduccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface HistorialReproduccionRepository extends JpaRepository<HistorialReproduccion, Long> {

    Page<HistorialReproduccion> findByUsuarioUsuarioIdOrderByFechaReproduccionDesc(Long usuarioId, Pageable pageable);

    List<HistorialReproduccion> findTop50ByUsuarioUsuarioIdOrderByFechaReproduccionDesc(Long usuarioId);

    @Query("SELECT h FROM HistorialReproduccion h WHERE h.usuario.usuarioId = :usuarioId " +
           "AND h.fechaReproduccion >= :desde " +
           "ORDER BY h.fechaReproduccion DESC")
    List<HistorialReproduccion> findByUsuarioIdAndFechaDesde(
            @Param("usuarioId") Long usuarioId,
            @Param("desde") Instant desde
    );

    @Query("SELECT COUNT(h) FROM HistorialReproduccion h WHERE h.usuario.usuarioId = :usuarioId")
    Long contarPorUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT h FROM HistorialReproduccion h " +
           "WHERE h.usuario.usuarioId = :usuarioId " +
           "AND h.metadatoYoutube.idVideoYoutube = :videoId " +
           "ORDER BY h.fechaReproduccion DESC")
    List<HistorialReproduccion> findByUsuarioIdAndVideoId(
            @Param("usuarioId") Long usuarioId,
            @Param("videoId") String videoId
    );

    void deleteByUsuarioUsuarioId(Long usuarioId);

    @Query("SELECT h FROM HistorialReproduccion h " +
           "WHERE h.usuario.usuarioId = :usuarioId " +
           "GROUP BY h.metadatoYoutube.idVideoYoutube, h.metadatoYoutube.titulo, h.metadatoYoutube.canal, " +
           "h.metadatoYoutube.miniaturaUrl, h.metadatoYoutube.duracionSegundos, h.metadatoYoutube.duracionTexto " +
           "ORDER BY MAX(h.fechaReproduccion) DESC")
    Page<HistorialReproduccion> findCancionesUnicasPorUsuario(
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );
}

