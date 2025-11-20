package com.emma.emmamusic.domain.reproducir.service;

import com.emma.emmamusic.domain.reproducir.dto.EstadoReproductorDTO;
import com.emma.emmamusic.domain.reproducir.dto.ReproductorWebSocketDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReproductorWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notificarCargando(Long usuarioId, EstadoReproductorDTO estado) {
        enviar(usuarioId, ReproductorWebSocketDTO.cargando(estado));
        log.debug("Notificación CARGANDO enviada a usuario {}", usuarioId);
    }

    public void notificarListo(Long usuarioId, EstadoReproductorDTO estado) {
        enviar(usuarioId, ReproductorWebSocketDTO.listo(estado));
        log.debug("Notificación LISTO enviada a usuario {}", usuarioId);
    }

    public void notificarError(Long usuarioId, String mensajeError) {
        enviar(usuarioId, ReproductorWebSocketDTO.error(mensajeError));
        log.warn("Notificación ERROR enviada a usuario {}: {}", usuarioId, mensajeError);
    }

    public void notificarActualizado(Long usuarioId, EstadoReproductorDTO estado) {
        enviar(usuarioId, ReproductorWebSocketDTO.actualizado(estado));
        log.debug("Notificación ACTUALIZADO enviada a usuario {}", usuarioId);
    }

    private void enviar(Long usuarioId, ReproductorWebSocketDTO mensaje) {
        String destino = "/topic/reproductor/" + usuarioId;
        messagingTemplate.convertAndSend(destino, mensaje);
    }
}

