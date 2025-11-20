package com.emma.emmamusic.domain.reproducir.service.impl;

import com.emma.emmamusic.config.PythonProperties;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import com.emma.emmamusic.domain.buscar.repository.MetadatoYoutubeRepository;
import com.emma.emmamusic.domain.reproducir.dto.ResultadoReproduccionDTO;
import com.emma.emmamusic.domain.reproducir.service.StreamService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamServiceImpl implements StreamService {

    private final MetadatoYoutubeRepository metadatoYoutubeRepository;
    private final PythonProperties pythonProperties;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ResultadoReproduccionDTO obtenerUrlReproduccion(String videoId, Long usuarioId) {
        log.info("╔═══════════════════════════════════════════════════════════════╗");
        log.info("║ OBTENIENDO URL DE REPRODUCCIÓN                                ║");
        log.info("╠═══════════════════════════════════════════════════════════════╣");
        log.info("║ VideoId: {}", videoId);
        log.info("║ UsuarioId: {}", usuarioId);
        log.info("╚═══════════════════════════════════════════════════════════════╝");

        Optional<MetadatoYoutube> metadatoOpt = metadatoYoutubeRepository.findByIdVideoYoutube(videoId);

        if (metadatoOpt.isEmpty()) {
            log.error("❌ Metadato no encontrado para video: {}", videoId);
            return construirErrorNoEncontrado(videoId);
        }

        MetadatoYoutube metadato = metadatoOpt.get();
        log.info("✓ Metadato encontrado: {}", metadato.getTitulo());

        incrementarContadorReproducciones(metadato);

        try {
            log.info("→ Ejecutando script Python para obtener stream...");
            String urlStream = ejecutarScriptPythonStream(videoId);

            if (urlStream == null || urlStream.isEmpty()) {
                log.error("❌ Script Python no retornó URL válida");
                return construirErrorStream(videoId, metadato.getTitulo());
            }

            log.info("✓ URL de stream obtenida exitosamente");
            log.info("✓ Longitud URL: {} caracteres", urlStream.length());

            ResultadoReproduccionDTO resultado = ResultadoReproduccionDTO.builder()
                .idVideo(videoId)
                .titulo(metadato.getTitulo())
                .urlReproduccion(urlStream)
                .tipoReproduccion(ResultadoReproduccionDTO.TipoReproduccion.STREAM_ONLINE)
                .calidad("medium")
                .duracionSegundos(metadato.getDuracionSegundos())
                .mensaje("Reproducción desde YouTube")
                .build();

            log.info("✓ ResultadoReproduccionDTO creado exitosamente");
            log.info("✓ Tipo: {}", resultado.getTipoReproduccion());

            return resultado;

        } catch (Exception e) {
            log.error("❌ ERROR CRÍTICO obteniendo stream para {}: {}", videoId, e.getMessage(), e);
            return construirErrorStream(videoId, metadato.getTitulo());
        }
    }

    private String ejecutarScriptPythonStream(String videoId) {
        log.info("→→→ Ejecutando script Python para obtener stream: {}", videoId);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                pythonProperties.executable(),
                pythonProperties.scripts().get("stream"),
                videoId
            );

            processBuilder.directory(new java.io.File(System.getProperty("user.dir")));
            processBuilder.redirectErrorStream(true);

            log.info("Directorio de trabajo: {}", processBuilder.directory());
            log.info("Comando completo: {} {} {}",
                pythonProperties.executable(),
                pythonProperties.scripts().get("stream"),
                videoId);

            Process process = processBuilder.start();
            log.info("✓ Proceso Python iniciado");

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    output.append(line).append("\n");
                    log.info("Python [línea {}]: {}", lineNumber, line);
                }
            }

            int exitCode = process.waitFor();
            log.info("✓ Proceso Python finalizado con exit code: {}", exitCode);

            if (exitCode != 0) {
                log.error("❌ Script Python finalizó con error. Exit code: {}", exitCode);
                log.error("Output completo:\n{}", output);
                return null;
            }

            String jsonOutput = output.toString().trim();

            if (jsonOutput.isEmpty()) {
                log.error("❌ Script Python no retornó ningún output");
                return null;
            }

            log.info("✓ Respuesta JSON recibida (primeros 500 chars): {}",
                jsonOutput.substring(0, Math.min(500, jsonOutput.length())));

            Map<String, Object> response = objectMapper.readValue(jsonOutput,
                new TypeReference<>() {});

            Boolean exito = (Boolean) response.get("exito");
            log.info("Respuesta parseada - Éxito: {}", exito);

            if (Boolean.TRUE.equals(exito)) {
                Map<String, Object> datos = (Map<String, Object>) response.get("datos");
                String url = (String) datos.get("url_stream");
                log.info("✓✓✓ URL DE STREAM OBTENIDA EXITOSAMENTE");
                log.info("URL length: {} caracteres", url != null ? url.length() : 0);
                return url;
            } else {
                String mensajeError = (String) response.get("error");
                String detalle = (String) response.get("detalle");
                log.error("❌ Error en script Python: {} - Detalle: {}", mensajeError, detalle);
                return null;
            }

        } catch (Exception e) {
            log.error("❌ EXCEPCIÓN ejecutando script Python: {}", e.getMessage(), e);
            return null;
        }
    }

    private void incrementarContadorReproducciones(MetadatoYoutube metadato) {
        metadato.setContadorReproducciones(metadato.getContadorReproducciones() + 1);
        metadato.setUltimaActividad(Instant.now());
        metadatoYoutubeRepository.save(metadato);
        log.debug("Contador de reproducciones actualizado para: {}", metadato.getIdVideoYoutube());
    }

    private ResultadoReproduccionDTO construirErrorNoEncontrado(String videoId) {
        return ResultadoReproduccionDTO.builder()
            .idVideo(videoId)
            .titulo("No encontrado")
            .urlReproduccion(null)
            .tipoReproduccion(ResultadoReproduccionDTO.TipoReproduccion.ERROR)
            .mensaje("El video no existe en la base de datos")
            .build();
    }

    private ResultadoReproduccionDTO construirErrorStream(String videoId, String titulo) {
        return ResultadoReproduccionDTO.builder()
            .idVideo(videoId)
            .titulo(titulo)
            .urlReproduccion(null)
            .tipoReproduccion(ResultadoReproduccionDTO.TipoReproduccion.ERROR)
            .mensaje("Error obteniendo URL de reproducción")
            .build();
    }
}

