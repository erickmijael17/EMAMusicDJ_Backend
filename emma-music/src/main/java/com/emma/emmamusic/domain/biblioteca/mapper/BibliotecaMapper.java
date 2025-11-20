package com.emma.emmamusic.domain.biblioteca.mapper;

import com.emma.emmamusic.domain.biblioteca.dto.BibliotecaPaginadaDTO;
import com.emma.emmamusic.domain.biblioteca.dto.CancionBibliotecaDTO;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BibliotecaMapper {

    DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

    @Mapping(target = "metadatoId", source = "metadato.metadatoId")
    @Mapping(target = "videoId", source = "metadato.idVideoYoutube")
    @Mapping(target = "titulo", source = "metadato.titulo")
    @Mapping(target = "canal", source = "metadato.canal")
    @Mapping(target = "duracionSegundos", source = "metadato.duracionSegundos")
    @Mapping(target = "duracionTexto", source = "metadato.duracionTexto")
    @Mapping(target = "miniaturaUrl", source = "metadato.miniaturaUrl")
    @Mapping(target = "esExplicita", source = "metadato.esExplicito")
    @Mapping(target = "fechaAdicion", expression = "java(formatearFecha(fechaAdicion))")
    @Mapping(target = "esFavorita", constant = "false")
    @Mapping(target = "vecesReproducida", constant = "0")
    CancionBibliotecaDTO toCancionDTO(MetadatoYoutube metadato, Instant fechaAdicion);

    List<CancionBibliotecaDTO> toCancionDTOList(List<MetadatoYoutube> metadatos);

    default BibliotecaPaginadaDTO toPaginadaDTO(Page<MetadatoYoutube> page) {
        return BibliotecaPaginadaDTO.builder()
                .canciones(toCancionDTOList(page.getContent()))
                .paginaActual(page.getNumber())
                .tamanoPagina(page.getSize())
                .totalPaginas(page.getTotalPages())
                .totalElementos(page.getTotalElements())
                .esUltimaPagina(page.isLast())
                .esPrimeraPagina(page.isFirst())
                .build();
    }

    default String formatearFecha(Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.atZone(ZoneId.of("UTC")).format(FORMATTER);
    }
}

