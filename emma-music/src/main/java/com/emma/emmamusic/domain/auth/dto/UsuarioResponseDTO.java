package com.emma.emmamusic.domain.auth.dto;

import com.emma.emmamusic.domain.auth.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long usuarioId;
    private String nombreUsuario;
    private String email;
    private String nombre;
    private String apellido;
    private String urlImagenPerfil;
    private Usuario.NivelSuscripcion nivelSuscripcion;
    private Boolean estaActivo;
    private Instant fechaCreacion;
    private Instant fechaActualizacion;
    private Instant ultimoLogin;
    private String token;

}