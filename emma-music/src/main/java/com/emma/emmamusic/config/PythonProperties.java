package com.emma.emmamusic.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.Map;

@ConfigurationProperties(prefix = "app.python")
public record PythonProperties(
    String executable,
    // Usamos un Map para poder definir múltiples scripts con un nombre clave
    Map<String, String> scripts
) {
}