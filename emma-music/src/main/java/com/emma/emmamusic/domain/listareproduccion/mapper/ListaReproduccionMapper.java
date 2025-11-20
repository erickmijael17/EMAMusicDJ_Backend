package com.emma.emmamusic.domain.listareproduccion.mapper;

import com.emma.emmamusic.domain.auth.model.Usuario;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import com.emma.emmamusic.domain.listareproduccion.dto.CancionPlaylistDTO;
import com.emma.emmamusic.domain.listareproduccion.dto.ListaReproduccionCrearDTO;
import com.emma.emmamusic.domain.listareproduccion.dto.ListaReproduccionDTO;
import com.emma.emmamusic.domain.listareproduccion.model.ListaReproduccion;
import com.emma.emmamusic.domain.listareproduccion.model.ListaReproduccionMetadato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ListaReproduccionMapper {

    @Mapping(target = "listaId", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    ListaReproduccion toEntity(ListaReproduccionCrearDTO dto);

    @Mapping(target = "usuarioId", source = "usuario.usuarioId")
    @Mapping(target = "nombreUsuario", source = "usuario.nombreUsuario")
    @Mapping(target = "totalCanciones", ignore = true)
    @Mapping(target = "canciones", ignore = true)
    ListaReproduccionDTO toDTO(ListaReproduccion entity);

    @Mapping(target = "listaId", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    void actualizarDesdeDTO(ListaReproduccionCrearDTO dto, @MappingTarget ListaReproduccion entity);

    @Mapping(target = "metadatoId", source = "metadatoYoutube.metadatoId")
    @Mapping(target = "idVideoYoutube", source = "metadatoYoutube.idVideoYoutube")
    @Mapping(target = "titulo", source = "metadatoYoutube.titulo")
    @Mapping(target = "canal", source = "metadatoYoutube.canal")
    @Mapping(target = "duracionSegundos", source = "metadatoYoutube.duracionSegundos")
    @Mapping(target = "duracionTexto", source = "metadatoYoutube.duracionTexto")
    @Mapping(target = "miniaturaUrl", source = "metadatoYoutube.miniaturaUrl")
    @Mapping(target = "esExplicito", source = "metadatoYoutube.esExplicito")
    @Mapping(target = "posicion", source = "posicion")
    @Mapping(target = "fechaAdicion", source = "fechaAdicion")
    @Mapping(target = "anadidoPor", source = "anadidoPorUsuario.nombreUsuario")
    CancionPlaylistDTO toCancionDTO(ListaReproduccionMetadato listaMetadato);

    List<CancionPlaylistDTO> toCancionDTOList(List<ListaReproduccionMetadato> listaMetadatos);

    default Usuario mapUsuario(Long usuarioId) {
        if (usuarioId == null) return null;
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(usuarioId);
        return usuario;
    }

    default MetadatoYoutube mapMetadato(Long metadatoId) {
        if (metadatoId == null) return null;
        MetadatoYoutube metadato = new MetadatoYoutube();
        metadato.setMetadatoId(metadatoId);
        return metadato;
    }

    default Long mapUsuarioId(Usuario usuario) {
        return usuario != null ? usuario.getUsuarioId() : null;
    }

    default Long mapMetadatoId(MetadatoYoutube metadato) {
        return metadato != null ? metadato.getMetadatoId() : null;
    }
}

