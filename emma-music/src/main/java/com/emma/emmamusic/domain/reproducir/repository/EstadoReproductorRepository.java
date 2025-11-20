package com.emma.emmamusic.domain.reproducir.repository;

import com.emma.emmamusic.domain.reproducir.model.EstadoReproductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoReproductorRepository extends JpaRepository<EstadoReproductor, Long> {

    @Query("SELECT e FROM EstadoReproductor e WHERE e.usuario.usuarioId = :usuarioId")
    Optional<EstadoReproductor> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(e) FROM EstadoReproductor e WHERE e.usuario.usuarioId = :usuarioId")
    boolean existsByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("DELETE FROM EstadoReproductor e WHERE e.usuario.usuarioId = :usuarioId")
    void deleteByUsuarioId(@Param("usuarioId") Long usuarioId);
}

