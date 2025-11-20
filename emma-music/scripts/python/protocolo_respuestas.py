from datetime import datetime, timezone
from typing import Literal, Any

VERSION_API = "1.0.0"
TipoRespuesta = Literal["busqueda", "stream"]


def _timestamp_iso() -> str:
    """Devuelve el timestamp actual en formato ISO 8601 UTC."""
    return datetime.now(timezone.utc).isoformat()


def _construir_respuesta_base(tipo_respuesta: TipoRespuesta) -> dict[str, Any]:
    """Crea el diccionario base para todas las respuestas."""
    return {
        "tipo_respuesta": tipo_respuesta,
        "version_api": VERSION_API,
        "timestamp": _timestamp_iso(),
    }


def construir_respuesta_ok(tipo_respuesta: TipoRespuesta, datos: Any) -> dict[str, Any]:
    """
    Crea una respuesta estándar de éxito.
    """
    respuesta = _construir_respuesta_base(tipo_respuesta)
    respuesta["exito"] = True
    respuesta["datos"] = datos
    return respuesta


def construir_respuesta_error(
    tipo_respuesta: TipoRespuesta,
    mensaje_error: str,
    detalle: str | None = None,
) -> dict[str, Any]:
    """
    Crea una respuesta estándar de error.
    """
    respuesta = _construir_respuesta_base(tipo_respuesta)
    respuesta["exito"] = False
    respuesta["error"] = mensaje_error

    if detalle:
        respuesta["detalle"] = detalle
    return respuesta