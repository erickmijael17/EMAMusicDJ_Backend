package com.emma.emmamusic.domain.auth.service.impl;

import com.emma.emmamusic.domain.auth.dto.UsuarioCreacionDTO;
import com.emma.emmamusic.domain.auth.dto.UsuarioIniciarSesionDTO;
import com.emma.emmamusic.domain.auth.dto.UsuarioResponseDTO;
import com.emma.emmamusic.domain.auth.mapper.UsuarioMapper;
import com.emma.emmamusic.domain.auth.model.Usuario;
import com.emma.emmamusic.domain.auth.repository.UsuarioRepository;
import com.emma.emmamusic.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.emma.emmamusic.security.JwtUtilities;
import java.util.Collections;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioMapper usuarioMapper;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtilities jwtUtilities;

    @Override
    public UsuarioResponseDTO crearUsuario(UsuarioCreacionDTO data) {
        log.info("iniciando la creacion de usuario: {}", data);

        if (usuarioRepository.findByEmail(data.getEmail()).isPresent()) {
            throw new IllegalStateException("El email ya está en uso.");
        }

        Usuario usuario = usuarioMapper.toEntity(data);
        usuario.setHashContrasena(passwordEncoder.encode(data.getContrasena()));
        usuario.setEstaActivo(true);
        var usuarioGuardado = usuarioRepository.save(usuario);

        // Generar y asignar el token para que el usuario pueda iniciar sesión automáticamente.
        String token = jwtUtilities.generateToken(usuario.getEmail(), Collections.singletonList("ROLE_USER"));
        UsuarioResponseDTO responseDTO = usuarioMapper.toDTO(usuarioGuardado);
        responseDTO.setToken(token);
        return responseDTO;
    }

    @Override
    public UsuarioResponseDTO iniciarSesion(UsuarioIniciarSesionDTO data) {
        log.info("iniciando Logeo: {}", data);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        data.getEmail(),
                        data.getContrasena()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario usuario = usuarioRepository.findByEmail(data.getEmail())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado después de la autenticación."));

        String token = jwtUtilities.generateToken(usuario.getEmail(), Collections.singletonList("ROLE_USER"));
        UsuarioResponseDTO responseDTO = usuarioMapper.toDTO(usuario);
        responseDTO.setToken(token);
        return responseDTO;
    }
}
