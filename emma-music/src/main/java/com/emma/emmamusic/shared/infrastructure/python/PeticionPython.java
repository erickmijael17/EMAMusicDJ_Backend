package com.emma.emmamusic.shared.infrastructure.python;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PeticionPython {
    // El path completo al script que se va a ejecutar
    private String scriptPath;
    // Los argumentos que se pasarán al script en la línea de comandos
    private List<String> argumentos;
}