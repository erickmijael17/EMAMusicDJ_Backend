package com.emma.emmamusic.domain.auth.controller;

import com.emma.emmamusic.domain.auth.dto.UsuarioCreacionDTO;
import com.emma.emmamusic.domain.auth.dto.UsuarioIniciarSesionDTO;
import com.emma.emmamusic.domain.auth.dto.UsuarioResponseDTO;
import com.emma.emmamusic.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/crear")
    public ResponseEntity<UsuarioResponseDTO> crearUsuario(@RequestBody UsuarioCreacionDTO data) {
        return new ResponseEntity<>(authService.crearUsuario(data), HttpStatus.CREATED);
    }

    @PostMapping("/iniciarSesion")
    public ResponseEntity<UsuarioResponseDTO> iniciarSesion(@RequestBody UsuarioIniciarSesionDTO data) {
        return ResponseEntity.ok(authService.iniciarSesion(data));
    }
}
