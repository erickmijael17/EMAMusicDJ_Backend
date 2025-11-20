package com.emma.emmamusic.domain.listareproduccion.repository;

import com.emma.emmamusic.domain.listareproduccion.model.ListaReproduccionMetadato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListaReproduccionMetadatoRepository extends JpaRepository<ListaReproduccionMetadato, ListaReproduccionMetadato.ListaReproduccionMetadatoId> {

    @Query("SELECT lm FROM ListaReproduccionMetadato lm WHERE lm.listaReproduccion.listaId = :listaId ORDER BY lm.posicion ASC")
    List<ListaReproduccionMetadato> findByListaIdOrderByPosicion(@Param("listaId") Long listaId);

    @Query("SELECT MAX(lm.posicion) FROM ListaReproduccionMetadato lm WHERE lm.listaReproduccion.listaId = :listaId")
    Optional<Integer> findMaxPosicionByListaId(@Param("listaId") Long listaId);

    @Query("SELECT COUNT(lm) FROM ListaReproduccionMetadato lm WHERE lm.listaReproduccion.listaId = :listaId")
    long countByListaId(@Param("listaId") Long listaId);

    boolean existsByListaReproduccionListaIdAndMetadatoYoutubeMetadatoId(Long listaId, Long metadatoId);

    @Modifying
    @Query("DELETE FROM ListaReproduccionMetadato lm WHERE lm.listaReproduccion.listaId = :listaId AND lm.metadatoYoutube.metadatoId = :metadatoId")
    void deleteByListaIdAndMetadatoId(@Param("listaId") Long listaId, @Param("metadatoId") Long metadatoId);

    @Modifying
    @Query("UPDATE ListaReproduccionMetadato lm SET lm.posicion = lm.posicion - 1 WHERE lm.listaReproduccion.listaId = :listaId AND lm.posicion > :posicion")
    void actualizarPosicionesDesdeEliminar(@Param("listaId") Long listaId, @Param("posicion") Integer posicion);

    @Modifying
    @Query("UPDATE ListaReproduccionMetadato lm SET lm.posicion = :nuevaPosicion WHERE lm.listaReproduccion.listaId = :listaId AND lm.metadatoYoutube.metadatoId = :metadatoId")
    void actualizarPosicion(@Param("listaId") Long listaId, @Param("metadatoId") Long metadatoId, @Param("nuevaPosicion") Integer nuevaPosicion);

    @Modifying
    @Query("UPDATE ListaReproduccionMetadato lm SET lm.posicion = lm.posicion + 1 WHERE lm.listaReproduccion.listaId = :listaId AND lm.posicion >= :posicionInicio AND lm.posicion < :posicionFin")
    void desplazarPosicionesHaciaAbajo(@Param("listaId") Long listaId, @Param("posicionInicio") Integer posicionInicio, @Param("posicionFin") Integer posicionFin);

    @Modifying
    @Query("UPDATE ListaReproduccionMetadato lm SET lm.posicion = lm.posicion - 1 WHERE lm.listaReproduccion.listaId = :listaId AND lm.posicion > :posicionInicio AND lm.posicion <= :posicionFin")
    void desplazarPosicionesHaciaArriba(@Param("listaId") Long listaId, @Param("posicionInicio") Integer posicionInicio, @Param("posicionFin") Integer posicionFin);
}

