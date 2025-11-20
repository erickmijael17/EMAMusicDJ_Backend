package com.emma.emmamusic.domain.favoritos.repository;

import com.emma.emmamusic.domain.favoritos.model.FavoritoMetadato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritoMetadatoRepository extends JpaRepository<FavoritoMetadato, FavoritoMetadato.FavoritoMetadatoId> {

    @Query("SELECT fm FROM FavoritoMetadato fm WHERE fm.usuario.usuarioId = :usuarioId")
    List<FavoritoMetadato> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT fm FROM FavoritoMetadato fm WHERE fm.usuario.usuarioId = :usuarioId AND fm.metadatoYoutube.metadatoId = :metadatoId")
    Optional<FavoritoMetadato> findByUsuarioIdAndMetadatoId(@Param("usuarioId") Long usuarioId, @Param("metadatoId") Long metadatoId);

    @Query("SELECT fm FROM FavoritoMetadato fm WHERE fm.usuario.usuarioId = :usuarioId AND fm.metadatoYoutube.idVideoYoutube = :videoId")
    Optional<FavoritoMetadato> findByUsuarioIdAndVideoId(@Param("usuarioId") Long usuarioId, @Param("videoId") String videoId);

    @Query("SELECT COUNT(fm) FROM FavoritoMetadato fm WHERE fm.usuario.usuarioId = :usuarioId")
    long countByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(fm) FROM FavoritoMetadato fm WHERE fm.metadatoYoutube.metadatoId = :metadatoId")
    long countByMetadatoId(@Param("metadatoId") Long metadatoId);

    @Query("SELECT CASE WHEN COUNT(fm) > 0 THEN true ELSE false END FROM FavoritoMetadato fm WHERE fm.usuario.usuarioId = :usuarioId AND fm.metadatoYoutube.idVideoYoutube = :videoId")
    boolean existsByUsuarioIdAndMetadatoIdVideoYoutube(@Param("usuarioId") Long usuarioId, @Param("videoId") String videoId);
}

