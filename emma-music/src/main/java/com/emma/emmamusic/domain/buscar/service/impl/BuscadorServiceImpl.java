package com.emma.emmamusic.domain.buscar.service.impl;

import com.emma.emmamusic.config.PythonProperties;
import com.emma.emmamusic.domain.buscar.dto.ResultadoBusquedaDTO;
import com.emma.emmamusic.domain.buscar.model.MetadatoYoutube;
import com.emma.emmamusic.domain.buscar.repository.MetadatoYoutubeRepository;
import com.emma.emmamusic.domain.buscar.service.BuscadorService;
import com.emma.emmamusic.shared.external.youtube.dto.PistaBusquedaDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuscadorServiceImpl implements BuscadorService {

    private final MetadatoYoutubeRepository metadatoYoutubeRepository;
    private final PythonProperties pythonProperties;
    private final ObjectMapper objectMapper;

    @Value("${app.hibrido.limite-resultados:20}")
    private int limiteResultados;

    @Override
    @Transactional
    public List<ResultadoBusquedaDTO> buscarCanciones(String consulta) {
        log.info("Iniciando búsqueda híbrida para: '{}'", consulta);

        List<ResultadoBusquedaDTO> resultados = new ArrayList<>();

        log.debug("PASO 1: Buscando en base de datos local (metadatos_youtube)");
        List<MetadatoYoutube> metadatosLocales = metadatoYoutubeRepository.buscarPorTituloOCanal(consulta);

        for (MetadatoYoutube metadato : metadatosLocales) {
            metadato.setContadorBusquedas(metadato.getContadorBusquedas() + 1);
            resultados.add(convertirMetadatoADTO(metadato, ResultadoBusquedaDTO.OrigenDatos.BASE_DATOS_LOCAL));
        }

        log.info("PASO 1 completado: {} resultados encontrados en BD local", metadatosLocales.size());

        if (resultados.size() < limiteResultados) {
            int faltantes = limiteResultados - resultados.size();
            log.debug("PASO 2: Faltan {} resultados, consultando YouTube API", faltantes);

            try {
                List<PistaBusquedaDTO> resultadosApi = ejecutarScriptPythonBusqueda(consulta);
                log.debug("YouTube API respondió con {} resultados", resultadosApi.size());

                log.debug("PASO 3: Guardando nuevos resultados en base de datos");
                List<String> idsExistentes = metadatosLocales.stream()
                    .map(MetadatoYoutube::getIdVideoYoutube)
                    .collect(Collectors.toList());

                int guardados = 0;
                for (PistaBusquedaDTO pistaApi : resultadosApi) {
                    if (!idsExistentes.contains(pistaApi.idVideo())) {
                        MetadatoYoutube nuevoMetadato = guardarMetadatoDesdeApi(pistaApi);
                        resultados.add(convertirMetadatoADTO(nuevoMetadato, ResultadoBusquedaDTO.OrigenDatos.API_YOUTUBE));
                        guardados++;

                        if (resultados.size() >= limiteResultados) {
                            break;
                        }
                    }
                }

                log.info("PASO 3 completado: {} nuevos metadatos guardados en BD", guardados);

            } catch (Exception e) {
                log.error("Error consultando YouTube API: {}", e.getMessage(), e);
            }
        }

        List<ResultadoBusquedaDTO> resultadosFinales = resultados.stream()
            .limit(limiteResultados)
            .collect(Collectors.toList());

        log.info("Búsqueda finalizada: {} resultados totales para '{}'", resultadosFinales.size(), consulta);
        return resultadosFinales;
    }

    private List<PistaBusquedaDTO> ejecutarScriptPythonBusqueda(String consulta) {
        log.debug("Ejecutando script Python: {}", pythonProperties.scripts().get("search"));

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                pythonProperties.executable(),
                pythonProperties.scripts().get("search"),
                consulta
            );

            processBuilder.directory(new java.io.File(System.getProperty("user.dir")));
            processBuilder.redirectErrorStream(true);

            log.debug("Directorio de trabajo: {}", processBuilder.directory());
            log.debug("Comando: {} {} {}",
                pythonProperties.executable(),
                pythonProperties.scripts().get("search"),
                consulta);

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    log.debug("Python output: {}", line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                log.error("Script Python finalizó con error. Exit code: {}", exitCode);
                log.error("Output completo: {}", output.toString());
                return Collections.emptyList();
            }

            String jsonOutput = output.toString().trim();

            if (jsonOutput.isEmpty()) {
                log.error("Script Python no retornó ningún output");
                return Collections.emptyList();
            }

            log.debug("Respuesta JSON de Python recibida: {}", jsonOutput.substring(0, Math.min(200, jsonOutput.length())));

            Map<String, Object> response = objectMapper.readValue(jsonOutput,
                new TypeReference<>() {});

            Boolean exito = (Boolean) response.get("exito");

            if (Boolean.TRUE.equals(exito)) {
                List<PistaBusquedaDTO> resultados = objectMapper.convertValue(
                    response.get("datos"),
                    new TypeReference<>() {}
                );
                log.info("Búsqueda en YouTube exitosa: {} resultados", resultados.size());
                return resultados;
            } else {
                String mensajeError = (String) response.get("error");
                String detalle = (String) response.get("detalle");
                log.error("Error en script Python: {} - Detalle: {}", mensajeError, detalle);
                return Collections.emptyList();
            }

        } catch (Exception e) {
            log.error("Error ejecutando script Python: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private ResultadoBusquedaDTO convertirMetadatoADTO(MetadatoYoutube metadato, ResultadoBusquedaDTO.OrigenDatos origen) {
        List<String> miniaturas = generarUrlsMiniaturas(metadato.getIdVideoYoutube());

        return ResultadoBusquedaDTO.builder()
            .idVideo(metadato.getIdVideoYoutube())
            .titulo(metadato.getTitulo())
            .canal(metadato.getCanal())
            .duracionSegundos(metadato.getDuracionSegundos())
            .duracionTexto(metadato.getDuracionTexto())
            .miniaturas(miniaturas)
            .esExplicito(metadato.getEsExplicito())
            .contadorReproducciones(metadato.getContadorReproducciones())
            .contadorBusquedas(metadato.getContadorBusquedas())
            .ultimaActividad(metadato.getUltimaActividad())
            .origen(origen)
            .build();
    }

    private MetadatoYoutube guardarMetadatoDesdeApi(PistaBusquedaDTO pistaApi) {
        if (metadatoYoutubeRepository.existsByIdVideoYoutube(pistaApi.idVideo())) {
            log.debug("El metadato {} ya existe en BD, omitiendo duplicado", pistaApi.idVideo());
            return metadatoYoutubeRepository.findByIdVideoYoutube(pistaApi.idVideo()).orElseThrow();
        }

        String canal = extraerNombreCanal(pistaApi);

        MetadatoYoutube nuevoMetadato = MetadatoYoutube.builder()
            .idVideoYoutube(pistaApi.idVideo())
            .titulo(pistaApi.titulo())
            .canal(canal)
            .duracionSegundos(pistaApi.duracionSegundos())
            .duracionTexto(pistaApi.duracionTexto())
            .miniaturaUrl(obtenerMejorMiniatura(pistaApi.miniaturas()))
            .esExplicito(pistaApi.esExplicito() != null ? pistaApi.esExplicito() : false)
            .build();

        MetadatoYoutube guardado = metadatoYoutubeRepository.save(nuevoMetadato);
        log.debug("Metadato guardado: {} - {}", guardado.getIdVideoYoutube(), guardado.getTitulo());

        return guardado;
    }

    private String extraerNombreCanal(PistaBusquedaDTO pista) {
        if (pista.artistas() != null && !pista.artistas().isEmpty()) {
            return pista.artistas().get(0).nombre();
        }
        return "Desconocido";
    }

    private String obtenerMejorMiniatura(List<String> miniaturas) {
        if (miniaturas == null || miniaturas.isEmpty()) {
            return null;
        }
        return miniaturas.get(0);
    }

    private List<String> generarUrlsMiniaturas(String videoId) {
        return List.of(
            "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg",
            "https://i.ytimg.com/vi/" + videoId + "/sddefault.jpg",
            "https://i.ytimg.com/vi/" + videoId + "/maxresdefault.jpg"
        );
    }
}