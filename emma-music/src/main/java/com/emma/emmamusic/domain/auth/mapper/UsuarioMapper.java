package com.emma.emmamusic.domain.auth.mapper;

import com.emma.emmamusic.domain.auth.dto.UsuarioCreacionDTO;
import com.emma.emmamusic.domain.auth.dto.UsuarioResponseDTO;
import com.emma.emmamusic.domain.auth.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    //conversion de entidad a dto y viceversa , siempre empieza de la derecha a la izquierda
    //creacion
    @Mapping(target = "hashContrasena", source = "contrasena")
    @Mapping(target = "usuarioId", ignore = true)
    @Mapping(target = "urlImagenPerfil", ignore = true)
    @Mapping(target = "nivelSuscripcion", ignore = true)
    @Mapping(target = "estaActivo", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "ultimoLogin", ignore = true)
    @Mapping(target = "tokenReseteoContrasena", ignore = true)
    @Mapping(target = "expiracionTokenReseteo", ignore = true)
    Usuario toEntity(UsuarioCreacionDTO dto);
    //response
    UsuarioResponseDTO toDTO(Usuario entity);




}
