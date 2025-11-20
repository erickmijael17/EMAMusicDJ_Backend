import sys
import json
import time
from functools import lru_cache

import yt_dlp

from protocolo_respuestas import (
    construir_respuesta_ok,
    construir_respuesta_error,
)

MAX_INTENTOS_YTDLP = 3
ESPERA_REINTENTO_STREAM = 1.5

OPCIONES_DESCARGA_BASE = {
    "format": "bestaudio/best",
    "quiet": True,
    "no_warnings": True,
    "socket_timeout": 10,  # timeout de red en segundos
}


def obtener_info_stream_con_reintentos(url_video: str) -> dict:
    """
    Llama a yt_dlp con reintentos para obtener la info del video.
    """
    ultimo_error = None

    for intento in range(1, MAX_INTENTOS_YTDLP + 1):
        try:
            with yt_dlp.YoutubeDL(OPCIONES_DESCARGA_BASE) as descargador:
                info = descargador.extract_info(url_video, download=False)
                return info
        except Exception as error:
            ultimo_error = error
            if intento < MAX_INTENTOS_YTDLP:
                time.sleep(ESPERA_REINTENTO_STREAM)

    raise RuntimeError(
        f"Falló la obtención del stream tras {MAX_INTENTOS_YTDLP} intentos",
    ) from ultimo_error


def construir_datos_stream(info: dict) -> dict:
    """
    A partir del dict 'info' de yt_dlp construye el diccionario normalizado
    con todos los metadatos que nos interesan.
    """
    formatos_solicitados = info.get("requested_downloads")
    formato_audio = formatos_solicitados[0] if formatos_solicitados else info

    tasa_bits_audio = (
            formato_audio.get("abr")   # audio bitrate específico
            or formato_audio.get("tbr")  # total bitrate si no hay abr
            or 0
    )

    if not tasa_bits_audio:
        calidad = "desconocida"
    elif tasa_bits_audio >= 256:
        calidad = "hifi"
    else:
        calidad = "normal"

    datos_stream = {
        # Identificación básica del video
        "id_video": info.get("id"),
        "url_stream": formato_audio.get("url") or info.get("url"),
        "titulo": info.get("title"),
        "descripcion": info.get("description"),
        "duracion_segundos": info.get("duration"),
        "miniatura": info.get("thumbnail"),

        # Información del canal / uploader
        "canal": info.get("channel"),
        "id_canal": info.get("channel_id"),
        "uploader": info.get("uploader"),

        # Métricas
        "vistas": info.get("view_count"),
        "likes": info.get("like_count"),
        "fecha_subida": info.get("upload_date"),

        # Detalles del formato de audio seleccionado
        "extension": formato_audio.get("ext"),
        "codec_audio": formato_audio.get("acodec"),
        "frecuencia_muestreo_hz": formato_audio.get("asr"),
        "bitrate_kbps": tasa_bits_audio,
        "calidad": calidad,  # normal / hifi / desconocida
    }

    return datos_stream


@lru_cache(maxsize=256)
def _get_stream_url_cacheada(id_video_normalizado: str) -> dict:
    """
    Versión cacheada de la obtención de stream.
    """
    url_video = f"https://www.youtube.com/watch?v={id_video_normalizado}"
    info = obtener_info_stream_con_reintentos(url_video)
    return construir_datos_stream(info)


def get_stream_url(id_video: str) -> dict:
    """
    Punto de entrada lógico para obtener la URL de stream y metadatos.
    """
    try:
        id_video_normalizado = id_video.strip()

        if not id_video_normalizado:
            return construir_respuesta_error(
                "stream",
                "El id de video está vacío",
            )

        datos_stream = _get_stream_url_cacheada(id_video_normalizado)

        return construir_respuesta_ok("stream", datos_stream)

    except Exception as error:
        return construir_respuesta_error(
            "stream",
            "Error al obtener el stream de audio",
            str(error),
        )


if __name__ == "__main__":
    # 1. Validamos que se haya pasado al menos un id de video
    if len(sys.argv) < 2:
        respuesta_error = construir_respuesta_error(
            "stream",
            "No se proporcionó ningún id de video",
        )
        print(json.dumps(respuesta_error, ensure_ascii=True))
        sys.exit(1)

    id_video_cli = sys.argv[1].strip()

    if not id_video_cli:
        respuesta_error = construir_respuesta_error(
            "stream",
            "El id de video está vacío",
        )
        print(json.dumps(respuesta_error, ensure_ascii=True))
        sys.exit(1)

    # 3. Llamamos a la función principal
    resultado = get_stream_url(id_video_cli)

    # 4. Código de salida según 'exito'
    codigo_salida = 0 if resultado.get("exito") else 1

    # 5. Imprimimos el JSON para que lo lea Java
    output = json.dumps(resultado, indent=2, ensure_ascii=True)
    print(output)

    # 6. Salimos con el código adecuado
    sys.exit(codigo_salida)
