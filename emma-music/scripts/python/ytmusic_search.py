import sys
import json
import time
from functools import lru_cache

from ytmusicapi import YTMusic

from protocolo_respuestas import (
    construir_respuesta_ok,
    construir_respuesta_error,
)

LIMITE_RESULTADOS_BUSQUEDA = 10
MAX_INTENTOS_YTMUSIC = 3
ESPERA_REINTENTO_SEGUNDOS = 1.5
TIEMPO_MAXIMO_SEGUNDOS = 10.0


def normalizar_consulta(consulta: str) -> str:
    """
    Normaliza la consulta para usarla como clave de caché.
    """
    return " ".join(consulta.lower().split())


def buscar_canciones_con_reintentos(consulta_normalizada: str, limite_resultados: int):
    """
    Llama a YTMusic.search con reintentos y mide el tiempo de respuesta.
    Lanza excepción si falla en todos los intentos.
    """
    ultimo_error = None

    for intento in range(1, MAX_INTENTOS_YTMUSIC + 1):
        inicio = time.perf_counter()
        try:
            cliente = YTMusic()
            resultados = cliente.search(
                consulta_normalizada,
                filter="songs",
                limit=limite_resultados,
            )
            duracion = time.perf_counter() - inicio

            if duracion > TIEMPO_MAXIMO_SEGUNDOS:
                raise TimeoutError(
                    f"La llamada a YouTube Music tardó {duracion:.2f}s, "
                    f"por encima del umbral de {TIEMPO_MAXIMO_SEGUNDOS}s",
                )

            return resultados
        except Exception as error:
            ultimo_error = error
            if intento < MAX_INTENTOS_YTMUSIC:
                time.sleep(ESPERA_REINTENTO_SEGUNDOS)

    raise RuntimeError(
        f"Falló la llamada a YouTube Music tras {MAX_INTENTOS_YTMUSIC} intentos",
    ) from ultimo_error


def normalizar_resultados_a_pistas(resultados_canciones: list[dict]) -> list[dict]:
    """
    Convierte la lista de resultados crudos de YTMusic en una lista de 'pistas' normalizadas.
    """
    pistas: list[dict] = []

    for resultado in resultados_canciones:
        pista = {
            # Información básica
            "id_video": resultado.get("videoId"),
            "titulo": resultado.get("title"),
            "categoria": resultado.get("category"),
            "tipo_resultado": resultado.get("resultType"),

            # Artistas (nombre e id de canal, si existen)
            "artistas": [
                {
                    "nombre": artista.get("name"),
                    "id_artista": artista.get("id"),
                }
                for artista in resultado.get("artists", [])
                if artista.get("name")
            ],

            # Álbum (nombre e id, si existen)
            "album": {
                "nombre": resultado.get("album", {}).get("name"),
                "id_album": resultado.get("album", {}).get("id"),
            } if resultado.get("album") else None,

            # Duración (texto y en segundos, si viene)
            "duracion": resultado.get("duration"),
            "duracion_segundos": resultado.get("duration_seconds"),

            # Info adicional útil
            "vistas": resultado.get("views"),
            "es_explicito": resultado.get("isExplicit"),

            # Miniaturas (lista de URLs)
            "miniaturas": [
                miniatura.get("url")
                for miniatura in resultado.get("thumbnails", [])
                if miniatura.get("url")
            ],

            # Tokens de feedback (cuando la API los devuelva)
            "tokens_feedback": resultado.get("feedbackTokens"),
        }

        pistas.append(pista)

    return pistas


@lru_cache(maxsize=128)
def _search_music_cacheada(consulta_normalizada: str) -> list[dict]:
    """
    Versión cacheada de la lógica de búsqueda.
    """
    resultados_canciones = buscar_canciones_con_reintentos(
        consulta_normalizada,
        LIMITE_RESULTADOS_BUSQUEDA,
    )
    return normalizar_resultados_a_pistas(resultados_canciones)


def search_music(consulta: str) -> dict:
    """
    Punto de entrada lógico: recibe la consulta original y devuelve el JSON estándar.
    """
    try:
        consulta_normalizada = normalizar_consulta(consulta)

        if not consulta_normalizada:
            return construir_respuesta_error(
                "busqueda",
                "La consulta de búsqueda está vacía",
            )

        pistas = _search_music_cacheada(consulta_normalizada)

        return construir_respuesta_ok("busqueda", pistas)

    except Exception as error:
        return construir_respuesta_error(
            "busqueda",
            "Error al buscar en YouTube Music",
            str(error),
        )


if __name__ == '__main__':
    # 1. Validamos que se haya pasado al menos un término de búsqueda
    if len(sys.argv) < 2:
        respuesta_error = construir_respuesta_error(
            "busqueda",
            "No se proporcionó ningún término de búsqueda",
        )
        print(json.dumps(respuesta_error, ensure_ascii=False))
        sys.exit(1)

    # 2. Construimos la consulta de búsqueda a partir de los argumentos
    consulta_cli = " ".join(sys.argv[1:]).strip()

    if not consulta_cli:
        respuesta_error = construir_respuesta_error(
            "busqueda",
            "La consulta de búsqueda está vacía",
        )
        print(json.dumps(respuesta_error, ensure_ascii=False))
        sys.exit(1)

    # 3. Llamamos a la función principal
    resultado = search_music(consulta_cli)

    # 4. Código de salida según 'exito'
    codigo_salida = 0 if resultado.get("exito") else 1

    # 5. Imprimimos el JSON para que lo lea Java
    print(json.dumps(resultado, indent=2, ensure_ascii=False))

    # 6. Salimos con el código adecuado
    sys.exit(codigo_salida)
