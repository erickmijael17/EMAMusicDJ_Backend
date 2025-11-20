package com.emma.emmamusic.domain.buscar.repository;

import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MetadatoYoutubeRepository extends JpaRepository<MetadatoYoutube, Long> {

    Optional<MetadatoYoutube> findByIdVideoYoutube(String idVideoYoutube);

    @Query("SELECT m FROM MetadatoYoutube m " +
           "WHERE LOWER(m.titulo) LIKE LOWER(CONCAT('%', :consulta, '%')) " +
           "OR LOWER(m.canal) LIKE LOWER(CONCAT('%', :consulta, '%')) " +
           "ORDER BY m.contadorReproducciones DESC, m.ultimaActividad DESC")
    List<MetadatoYoutube> buscarPorTituloOCanal(@Param("consulta") String consulta);


    boolean existsByIdVideoYoutube(String idVideoYoutube);
}

