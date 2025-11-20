package com.emma.emmamusic.domain.reproducir.repository;

import com.emma.emmamusic.domain.reproducir.model.ColaReproduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColaReproduccionRepository extends JpaRepository<ColaReproduccion, Long> {

    @Query("SELECT c FROM ColaReproduccion c WHERE c.usuario.usuarioId = :usuarioId")
    Optional<ColaReproduccion> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(c) FROM ColaReproduccion c WHERE c.usuario.usuarioId = :usuarioId")
    boolean existsByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Query("DELETE FROM ColaReproduccion c WHERE c.usuario.usuarioId = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}


