package com.emma.emmamusic.domain.reproducir.historial.mapper;

import com.emma.emmamusic.domain.reproducir.historial.dto.HistorialPaginadoDTO;
import com.emma.emmamusic.domain.reproducir.historial.dto.HistorialReproduccionDTO;
import com.emma.emmamusic.domain.reproducir.historial.model.HistorialReproduccion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistorialReproduccionMapper {

    @Mapping(source = "usuario.usuarioId", target = "usuarioId")
    @Mapping(source = "idVideoYoutube", target = "videoId")
    @Mapping(source = "metadatoYoutube.titulo", target = "titulo")
    @Mapping(source = "metadatoYoutube.canal", target = "canal")
    @Mapping(source = "metadatoYoutube.miniaturaUrl", target = "miniaturaUrl")
    @Mapping(source = "metadatoYoutube.duracionSegundos", target = "duracionSegundos")
    @Mapping(source = "metadatoYoutube.duracionTexto", target = "duracionTexto")
    HistorialReproduccionDTO toDTO(HistorialReproduccion historial);

    List<HistorialReproduccionDTO> toDTOList(List<HistorialReproduccion> historiales);

    @Named("toPaginadoDTO")
    default HistorialPaginadoDTO toPaginadoDTO(Page<HistorialReproduccion> page) {
        if (page == null) {
            return HistorialPaginadoDTO.builder()
                    .contenido(List.of())
                    .paginaActual(0)
                    .tamanioPagina(0)
                    .totalElementos(0)
                    .totalPaginas(0)
                    .esUltimaPagina(true)
                    .esPrimeraPagina(true)
                    .build();
        }

        return HistorialPaginadoDTO.builder()
                .contenido(toDTOList(page.getContent()))
                .paginaActual(page.getNumber())
                .tamanioPagina(page.getSize())
                .totalElementos(page.getTotalElements())
                .totalPaginas(page.getTotalPages())
                .esUltimaPagina(page.isLast())
                .esPrimeraPagina(page.isFirst())
                .build();
    }
}

