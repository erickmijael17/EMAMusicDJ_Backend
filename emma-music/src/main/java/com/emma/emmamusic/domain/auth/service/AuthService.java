package com.emma.emmamusic.domain.auth.service;

import com.emma.emmamusic.domain.auth.dto.UsuarioCreacionDTO;
import com.emma.emmamusic.domain.auth.dto.UsuarioIniciarSesionDTO;
import com.emma.emmamusic.domain.auth.dto.UsuarioResponseDTO;

public interface AuthService {
    UsuarioResponseDTO crearUsuario(UsuarioCreacionDTO data);
    UsuarioResponseDTO iniciarSesion(UsuarioIniciarSesionDTO data);
}
