package com.emma.emmamusic.domain.listareproduccion.repository;

import com.emma.emmamusic.domain.listareproduccion.model.ListaReproduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaReproduccionRepository extends JpaRepository<ListaReproduccion, Long> {

    List<ListaReproduccion> findByUsuarioUsuarioId(Long usuarioId);

    List<ListaReproduccion> findByEsPublicaTrue();

    List<ListaReproduccion> findByUsuarioUsuarioIdAndEsPublica(Long usuarioId, Boolean esPublica);

    @Query("SELECT l FROM ListaReproduccion l WHERE l.esPublica = true ORDER BY l.fechaCreacion DESC")
    List<ListaReproduccion> findListasPublicas();

    @Query("SELECT l FROM ListaReproduccion l WHERE l.titulo ILIKE %:termino% AND l.esPublica = true")
    List<ListaReproduccion> buscarListasPublicasPorTitulo(@Param("termino") String termino);

    long countByUsuarioUsuarioId(Long usuarioId);
}
