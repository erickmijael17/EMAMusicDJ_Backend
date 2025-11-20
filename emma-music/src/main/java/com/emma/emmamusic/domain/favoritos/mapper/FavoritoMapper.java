package com.emma.emmamusic.domain.favoritos.mapper;

import com.emma.emmamusic.domain.auth.model.Usuario;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import com.emma.emmamusic.domain.favoritos.dto.FavoritoDTO;
import com.emma.emmamusic.domain.favoritos.model.FavoritoMetadato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FavoritoMapper {

    @Mapping(target = "idVideoYoutube", source = "metadatoYoutube.idVideoYoutube")
    @Mapping(target = "titulo", source = "metadatoYoutube.titulo")
    @Mapping(target = "canal", source = "metadatoYoutube.canal")
    @Mapping(target = "duracionSegundos", source = "metadatoYoutube.duracionSegundos")
    @Mapping(target = "duracionTexto", source = "metadatoYoutube.duracionTexto")
    @Mapping(target = "miniaturaUrl", source = "metadatoYoutube.miniaturaUrl")
    @Mapping(target = "esExplicito", source = "metadatoYoutube.esExplicito")
    @Mapping(target = "fechaAdicion", source = "fechaAdicion")
    @Mapping(target = "origen", constant = "METADATO")
    FavoritoDTO toDTO(FavoritoMetadato favoritoMetadato);

    List<FavoritoDTO> toDTOList(List<FavoritoMetadato> favoritos);

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

