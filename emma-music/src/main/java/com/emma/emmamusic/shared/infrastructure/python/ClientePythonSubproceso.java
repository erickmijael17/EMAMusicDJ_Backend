package com.emma.emmamusic.shared.infrastructure.python;

import com.emma.emmamusic.config.PythonProperties;
import com.emma.emmamusic.shared.external.youtube.dto.RespuestaPythonDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientePythonSubproceso implements ClientePython {

    private final PythonProperties pythonProperties;
    private final ObjectMapper objectMapper;

    @Override
    public <T> RespuestaPythonDTO<T> ejecutar(PeticionPython peticion, TypeReference<RespuestaPythonDTO<T>> tipoRespuesta) {

        // Construimos el comando: <python_executable> <script_path> [args...]
        List<String> comando = new ArrayList<>();
        comando.add(pythonProperties.executable());
        comando.add(peticion.getScriptPath());
        comando.addAll(peticion.getArgumentos()); // por ejemplo: ["Dua Lipa"] o ["dQw4w9WgXcQ"]

        ProcessBuilder constructorProceso = new ProcessBuilder(comando)
                .directory(new java.io.File(".")); // establecemos directorio de trabajo

        log.info("Ejecutando script de Python: {} con el ejecutable: {} y argumentos: {}",
                peticion.getScriptPath(), pythonProperties.executable(), peticion.getArgumentos());

        // --- INICIO: CÓDIGO DE DIAGNÓSTICO TEMPORAL ---
        try {
            log.info("RUTA ABSOLUTA DE PYTHON DETECTADA: {}", new java.io.File(pythonProperties.executable()).getCanonicalPath());
            log.info("DIRECTORIO DE TRABAJO: {}", new java.io.File(".").getCanonicalPath());
            log.info("RUTA DEL SCRIPT: {}", peticion.getScriptPath());
            log.info("SCRIPT EXISTS: {}", new java.io.File(peticion.getScriptPath()).exists());
        } catch (java.io.IOException e) {
            log.warn("No se pudo resolver rutas canónicas.", e);
        }
        // --- FIN: CÓDIGO DE DIAGNÓSTICO TEMPORAL ---

        try {
            Process proceso = constructorProceso.start();

            // Leemos la salida estándar (stdout) como UTF-8
            StringBuilder salida = new StringBuilder();
            try (BufferedReader salidaEstandar = new BufferedReader(
                    new InputStreamReader(proceso.getInputStream(), StandardCharsets.UTF_8))) {

                String linea;
                while ((linea = salidaEstandar.readLine()) != null) {
                    salida.append(linea);
                }
            }

            // Leemos la salida de error (stderr) como UTF-8
            StringBuilder salidaError = new StringBuilder();
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(proceso.getErrorStream(), StandardCharsets.UTF_8))) {

                String linea;
                while ((linea = errorReader.readLine()) != null) {
                    salidaError.append(linea).append("\n");
                }
            }

            // Añadimos un timeout para evitar que el proceso se quede colgado indefinidamente.
            if (!proceso.waitFor(30, TimeUnit.SECONDS)) {
                proceso.destroy(); // Matamos el proceso si excede el tiempo
                throw new RuntimeException("El script de Python excedió el tiempo de espera de 30 segundos.");
            }
            int codigoSalida = proceso.exitValue();
            String jsonSalida = salida.toString();

            log.info("Salida completa de Python (codigoSalida={}): {}", codigoSalida, jsonSalida);
            
            // Si hay errores en stderr, los logueamos
            if (salidaError.length() > 0) {
                log.error("Errores de Python (stderr): {}", salidaError.toString());
            }

            // Verificamos si la salida está vacía
            if (jsonSalida == null || jsonSalida.trim().isEmpty()) {
                String errorMsg = "El script de Python no devolvió ninguna salida";
                if (salidaError.length() > 0) {
                    errorMsg += ". Errores: " + salidaError.toString();
                }
                throw new RuntimeException(errorMsg);
            }
            
            // Parseamos siempre el JSON
            RespuestaPythonDTO<T> respuesta = objectMapper.readValue(
                    jsonSalida,
                    tipoRespuesta
            );

            // Si Python indica error lógico, lo propagamos o lo mapeamos
            if (!respuesta.exito()) {
                log.warn("Python devolvió error lógico: tipo={}, error={}, detalle={}",
                        respuesta.tipoRespuesta(), respuesta.error(), respuesta.detalle());
                // Aquí decides si lanzas excepción o devuelves la respuesta tal cual
                // Ejemplo: devolver la respuesta tal cual y que la capa superior gestione el error
            }

            // Si el exitCode es distinto de 0 pero exito=true, lo logueamos como warning
            if (codigoSalida != 0 && respuesta.exito() == true) {
                log.warn("Python devolvió exitCode={} pero exito=true en el JSON", codigoSalida);
            }

            return respuesta;

        } catch (Exception e) {
            log.error("Error al ejecutar el script de Python", e);
            throw new RuntimeException("Fallo en la comunicación con el script de Python.", e);
        }
    }
}
