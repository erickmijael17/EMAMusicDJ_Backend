# Guía de Integración Frontend - Módulo Reproductor

## Fecha: 2025-11-20
## Estado: DOCUMENTACIÓN OFICIAL

---

## ÍNDICE

1. Arquitectura General
2. Endpoints REST
3. Modelos de Datos (DTOs)
4. WebSocket
5. Flujos de Trabajo
6. Ejemplos de Peticiones
7. Manejo de Errores
8. Estados del Reproductor

---

## 1. ARQUITECTURA GENERAL

### Base URL
```
http://localhost:8080/api/v1/reproductor
```

### Autenticación
Todas las peticiones requieren JWT en el header:
```
Authorization: Bearer {token}
```

### Tecnologías
- REST API para comandos
- WebSocket (STOMP) para actualizaciones en tiempo real
- JSON como formato de datos

---

## 2. ENDPOINTS REST

### 2.1 Obtener Estado del Reproductor

```http
GET /api/v1/reproductor/estado/{usuarioId}
```

**Respuesta exitosa (200):**
```json
{
  "estadoId": 1,
  "usuarioId": 1,
  "videoIdActual": "rJ0D1GbDq1Q",
  "tituloActual": "Let Me Love You",
  "canalActual": "DJ Snake",
  "miniaturaUrl": "https://i.ytimg.com/vi/rJ0D1GbDq1Q/hqdefault.jpg",
  "duracionSegundos": 206,
  "estaReproduciendo": true,
  "posicionSegundos": 45,
  "volumen": 80,
  "esFavorita": false,
  "fechaActualizacion": "2025-11-20T04:13:51.949192Z",
  "urlReproduccion": "https://...",
  "tipoReproduccion": "STREAM_ONLINE",
  "indiceEnCola": 0,
  "totalEnCola": 20,
  "tieneSiguiente": true,
  "tieneAnterior": false,
  "modoReproduccion": "NORMAL"
}
```

---

### 2.2 Reproducir Canción Simple

```http
POST /api/v1/reproductor/reproducir
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1,
  "videoId": "rJ0D1GbDq1Q"
}
```

**Respuesta exitosa (200):**
```json
{
  "estadoId": 1,
  "usuarioId": 1,
  "videoIdActual": "rJ0D1GbDq1Q",
  "tituloActual": "Cargando...",
  "estaReproduciendo": true,
  "posicionSegundos": 0,
  "volumen": 80,
  "urlReproduccion": null
}
```

**Notas:**
- La respuesta inicial tiene `tituloActual: "Cargando..."`
- El `urlReproduccion` llega posteriormente vía WebSocket (evento STREAM_LISTO)

---

### 2.3 Reproducir desde Búsqueda

```http
POST /api/v1/reproductor/reproducir/desde-busqueda
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1,
  "videoId": "rJ0D1GbDq1Q",
  "terminoBusqueda": "justin bieber",
  "indiceEnBusqueda": 0
}
```

**Alias alternativo:**
```http
POST /api/v1/reproductor/reproducir-busqueda
```

**Respuesta exitosa (200):**
```json
{
  "estadoId": 1,
  "usuarioId": 1,
  "videoIdActual": "rJ0D1GbDq1Q",
  "tituloActual": "Cargando...",
  "estaReproduciendo": true,
  "indiceEnCola": 0,
  "totalEnCola": 20,
  "tieneSiguiente": true,
  "tieneAnterior": false
}
```

**Notas:**
- Carga automáticamente toda la lista de búsqueda (hasta 20 canciones) en la cola
- El `videoId` puede ser `null` para reproducir el primer resultado
- Si `videoId` es `null`, usa `indiceEnBusqueda` para seleccionar la canción

---

### 2.4 Controles de Reproducción

#### Play
```http
POST /api/v1/reproductor/play
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1
}
```

#### Pause
```http
POST /api/v1/reproductor/pause
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1
}
```

#### Siguiente
```http
POST /api/v1/reproductor/siguiente
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1
}
```

#### Anterior
```http
POST /api/v1/reproductor/anterior
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1
}
```

---

### 2.5 Actualizar Posición

```http
POST /api/v1/reproductor/posicion
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1,
  "posicionSegundos": 120
}
```

**Validación:**
- `posicionSegundos` debe ser >= 0
- `posicionSegundos` debe ser <= duracionSegundos

---

### 2.6 Actualizar Volumen

```http
POST /api/v1/reproductor/volumen
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1,
  "volumen": 75
}
```

**Validación:**
- `volumen` debe estar entre 0 y 100

---

### 2.7 Toggle Favorito

```http
POST /api/v1/reproductor/favorito/toggle
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1
}
```

**Respuesta exitosa (200):**
```json
{
  "mensaje": "Agregado a favoritos",
  "esFavorita": true,
  "estado": {
    "estadoId": 1,
    "usuarioId": 1,
    "videoIdActual": "rJ0D1GbDq1Q",
    "esFavorita": true
  }
}
```

---

### 2.8 Gestión de Cola

#### Obtener Cola
```http
GET /api/v1/reproductor/cola/{usuarioId}
```

**Respuesta exitosa (200):**
```json
{
  "colaId": 1,
  "usuarioId": 1,
  "canciones": [
    {
      "videoId": "rJ0D1GbDq1Q",
      "titulo": "Let Me Love You",
      "canal": "DJ Snake",
      "duracionSegundos": 206,
      "duracionTexto": "3:26",
      "miniaturaUrl": "https://i.ytimg.com/vi/rJ0D1GbDq1Q/hqdefault.jpg",
      "esExplicita": false,
      "tipoCancion": "YOUTUBE"
    }
  ],
  "indiceActual": 0,
  "modoReproduccion": "NORMAL",
  "contextoOrigen": "BUSQUEDA",
  "terminoBusqueda": "justin bieber",
  "playlistId": null,
  "albumId": null,
  "fechaCreacion": "2025-11-20T04:13:51.949192Z",
  "fechaActualizacion": "2025-11-20T04:13:51.949192Z"
}
```

#### Agregar a Cola
```http
POST /api/v1/reproductor/cola/agregar
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1,
  "videoIds": ["videoId1", "videoId2", "videoId3"],
  "reproducirAhora": false
}
```

**Parámetros:**
- `reproducirAhora`: Si es `true`, reproduce inmediatamente la primera canción agregada

**Respuesta exitosa (201):**
```json
{
  "mensaje": "Canciones agregadas exitosamente",
  "cola": {
    "colaId": 1,
    "canciones": []
  },
  "estado": "SUCCESS"
}
```

#### Eliminar de Cola
```http
DELETE /api/v1/reproductor/cola/eliminar
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1,
  "indice": 5
}
```

**Respuesta exitosa (200):**
```json
{
  "mensaje": "Canción eliminada de la cola",
  "cola": {},
  "estado": "SUCCESS"
}
```

#### Limpiar Cola
```http
DELETE /api/v1/reproductor/cola/limpiar
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1
}
```

**Respuesta exitosa (200):**
```json
{
  "mensaje": "Cola limpiada exitosamente",
  "estado": "SUCCESS"
}
```

#### Reordenar Cola
```http
POST /api/v1/reproductor/cola/reordenar
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1,
  "indiceOrigen": 2,
  "indiceDestino": 5
}
```

#### Cambiar Modo
```http
POST /api/v1/reproductor/modo
Content-Type: application/json
```

**Body:**
```json
{
  "usuarioId": 1,
  "modo": "ALEATORIO"
}
```

**Modos disponibles:**
- `NORMAL`: Reproducción secuencial
- `ALEATORIO`: Reproducción aleatoria
- `REPETIR_UNA`: Repite la canción actual
- `REPETIR_TODAS`: Repite toda la cola

---

## 3. MODELOS DE DATOS (DTOs)

### 3.1 EstadoReproductorDTO

```typescript
interface EstadoReproductorDto {
  estadoId: number;
  usuarioId: number;
  videoIdActual: string;
  tituloActual: string;
  canalActual: string | null;
  miniaturaUrl: string | null;
  duracionSegundos: number | null;
  estaReproduciendo: boolean;
  posicionSegundos: number;
  volumen: number;
  esFavorita: boolean;
  fechaActualizacion: string; // ISO 8601
  urlReproduccion: string | null;
  tipoReproduccion: string | null;
  indiceEnCola: number;
  totalEnCola: number;
  tieneSiguiente: boolean;
  tieneAnterior: boolean;
  modoReproduccion: ModoReproduccion;
}
```

### 3.2 ColaReproduccionDTO

```typescript
interface ColaReproduccionDto {
  colaId: number;
  usuarioId: number;
  canciones: CancionColaDto[];
  indiceActual: number;
  modoReproduccion: ModoReproduccion;
  contextoOrigen?: ContextoOrigen;
  terminoBusqueda?: string;
  playlistId?: number;
  albumId?: number;
  fechaCreacion: string;
  fechaActualizacion: string;
}
```

### 3.3 CancionColaDTO

```typescript
interface CancionColaDto {
  videoId: string;
  titulo: string;
  canal: string;
  duracionSegundos: number;
  duracionTexto: string;
  miniaturaUrl: string;
  esExplicita: boolean;
  tipoCancion: string;
}
```

### 3.4 Enums

```typescript
enum ModoReproduccion {
  NORMAL = 'NORMAL',
  ALEATORIO = 'ALEATORIO',
  REPETIR_UNA = 'REPETIR_UNA',
  REPETIR_TODAS = 'REPETIR_TODAS'
}

enum ContextoOrigen {
  BUSQUEDA = 'BUSQUEDA',
  PLAYLIST = 'PLAYLIST',
  ALBUM = 'ALBUM',
  ARTISTA = 'ARTISTA',
  FAVORITOS = 'FAVORITOS',
  HISTORIAL = 'HISTORIAL',
  MANUAL = 'MANUAL'
}

enum TipoReproduccion {
  STREAM_ONLINE = 'STREAM_ONLINE'
}

enum TipoEventoWebSocket {
  CARGANDO = 'CARGANDO',
  STREAM_LISTO = 'STREAM_LISTO',
  ERROR = 'ERROR'
}
```

---

## 4. WEBSOCKET

### 4.1 Configuración

**URL de conexión:**
```
ws://localhost:8080/ws
```

**Protocolo:** STOMP over SockJS

**Headers de conexión:**
```typescript
const headers = {
  Authorization: `Bearer ${token}`
};
```

### 4.2 Suscripción a Eventos

**Topic:**
```
/topic/reproductor/{usuarioId}
```

**Ejemplo con STOMP:**
```typescript
const client = new StompClient({
  brokerURL: 'ws://localhost:8080/ws',
  connectHeaders: {
    Authorization: `Bearer ${token}`
  },
  debug: (str) => console.log(str),
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000
});

client.onConnect = () => {
  client.subscribe(`/topic/reproductor/${usuarioId}`, (message) => {
    const data = JSON.parse(message.body);
    procesarMensajeWebSocket(data);
  });
};

client.activate();
```

### 4.3 Estructura de Mensajes WebSocket

```typescript
interface MensajeWebSocket {
  tipo: TipoEventoWebSocket;
  estado: EstadoReproductorDto;
  mensaje: string | null;
  timestamp: number;
}
```

### 4.4 Tipos de Eventos

#### CARGANDO
Se recibe cuando se inicia la carga de una canción:
```json
{
  "tipo": "CARGANDO",
  "estado": {
    "videoIdActual": "rJ0D1GbDq1Q",
    "tituloActual": "Cargando...",
    "estaReproduciendo": true,
    "urlReproduccion": null
  },
  "mensaje": null,
  "timestamp": 1700000000000
}
```

#### STREAM_LISTO
Se recibe cuando el stream está listo para reproducir (2-5 segundos después):
```json
{
  "tipo": "STREAM_LISTO",
  "estado": {
    "videoIdActual": "rJ0D1GbDq1Q",
    "tituloActual": "Let Me Love You",
    "canalActual": "DJ Snake",
    "duracionSegundos": 206,
    "estaReproduciendo": true,
    "urlReproduccion": "https://..."
  },
  "mensaje": null,
  "timestamp": 1700000000000
}
```

#### ERROR
Se recibe cuando hay un error:
```json
{
  "tipo": "ERROR",
  "estado": null,
  "mensaje": "No se pudo obtener el stream",
  "timestamp": 1700000000000
}
```

---

## 5. FLUJOS DE TRABAJO

### 5.1 Flujo: Reproducir desde Búsqueda

```
1. Usuario busca "justin bieber"
   GET /api/v1/hibrido/buscar?q=justin bieber

2. Usuario hace clic en resultado (índice 0)
   POST /api/v1/reproductor/reproducir/desde-busqueda
   Body: {
     usuarioId: 1,
     videoId: "rJ0D1GbDq1Q",
     terminoBusqueda: "justin bieber",
     indiceEnBusqueda: 0
   }

3. Backend responde inmediatamente:
   HTTP 200
   Body: {
     videoIdActual: "rJ0D1GbDq1Q",
     tituloActual: "Cargando...",
     estaReproduciendo: true,
     urlReproduccion: null
   }

4. Frontend muestra spinner/loading

5. Backend envía vía WebSocket (tipo: CARGANDO):
   {
     tipo: "CARGANDO",
     estado: { ... }
   }

6. Backend procesa stream (2-5 segundos)

7. Backend envía vía WebSocket (tipo: STREAM_LISTO):
   {
     tipo: "STREAM_LISTO",
     estado: {
       videoIdActual: "rJ0D1GbDq1Q",
       tituloActual: "Let Me Love You",
       duracionSegundos: 206,
       urlReproduccion: "https://...",
       indiceEnCola: 0,
       totalEnCola: 20
     }
   }

8. Frontend reproduce audio con urlReproduccion
```

### 5.2 Flujo: Play/Pause

```
1. Usuario hace clic en "Pause"
   POST /api/v1/reproductor/pause
   Body: { usuarioId: 1 }

2. Backend responde inmediatamente:
   HTTP 200
   Body: {
     estaReproduciendo: false
   }

3. Frontend pausa el audio

4. Usuario hace clic en "Play"
   POST /api/v1/reproductor/play
   Body: { usuarioId: 1 }

5. Backend responde:
   HTTP 200
   Body: {
     estaReproduciendo: true
   }

6. Frontend reanuda el audio
```

### 5.3 Flujo: Siguiente Canción

```
1. Usuario hace clic en "Siguiente"
   POST /api/v1/reproductor/siguiente
   Body: { usuarioId: 1 }

2. Backend responde con estado inicial:
   HTTP 200
   Body: {
     videoIdActual: "nuevoVideoId",
     tituloActual: "Cargando...",
     indiceEnCola: 1,
     totalEnCola: 20
   }

3. Frontend muestra spinner

4. Backend envía WebSocket (STREAM_LISTO)

5. Frontend reproduce nueva canción
```

### 5.4 Flujo: Actualizar Posición (Seek)

```
1. Usuario arrastra la barra de progreso a 2:30 (150 segundos)
   POST /api/v1/reproductor/posicion
   Body: {
     usuarioId: 1,
     posicionSegundos: 150
   }

2. Backend responde:
   HTTP 200
   Body: {
     posicionSegundos: 150
   }

3. Frontend actualiza posición del audio
```

### 5.5 Flujo: Agregar a Favoritos

```
1. Usuario hace clic en botón favorito
   POST /api/v1/reproductor/favorito/toggle
   Body: { usuarioId: 1 }

2. Backend responde:
   HTTP 200
   Body: {
     mensaje: "Agregado a favoritos",
     esFavorita: true,
     estado: { ... }
   }

3. Frontend actualiza ícono a "favorito activo"
```

---

## 6. EJEMPLOS DE PETICIONES

### 6.1 Inicializar Reproductor al Cargar Página

```typescript
async function inicializarReproductor(usuarioId: number) {
  try {
    // 1. Obtener estado actual
    const estadoResponse = await fetch(
      `http://localhost:8080/api/v1/reproductor/estado/${usuarioId}`,
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );
    const estado = await estadoResponse.json();

    // 2. Obtener cola
    const colaResponse = await fetch(
      `http://localhost:8080/api/v1/reproductor/cola/${usuarioId}`,
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );
    const cola = await colaResponse.json();

    // 3. Conectar WebSocket
    conectarWebSocket(usuarioId);

    // 4. Actualizar UI
    actualizarUI(estado, cola);

  } catch (error) {
    console.error('Error inicializando reproductor:', error);
  }
}
```

### 6.2 Reproducir Canción desde Búsqueda

```typescript
async function reproducirDesdeBusqueda(
  usuarioId: number,
  videoId: string,
  terminoBusqueda: string,
  indice: number
) {
  try {
    const response = await fetch(
      'http://localhost:8080/api/v1/reproductor/reproducir/desde-busqueda',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          usuarioId,
          videoId,
          terminoBusqueda,
          indiceEnBusqueda: indice
        })
      }
    );

    const estado = await response.json();

    // Mostrar spinner
    mostrarCargando();

    // El evento STREAM_LISTO llegará por WebSocket

  } catch (error) {
    console.error('Error reproduciendo:', error);
    mostrarError('No se pudo reproducir la canción');
  }
}
```

### 6.3 Controles Básicos

```typescript
async function play(usuarioId: number) {
  await fetch('http://localhost:8080/api/v1/reproductor/play', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ usuarioId })
  });
}

async function pause(usuarioId: number) {
  await fetch('http://localhost:8080/api/v1/reproductor/pause', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ usuarioId })
  });
}

async function siguiente(usuarioId: number) {
  await fetch('http://localhost:8080/api/v1/reproductor/siguiente', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ usuarioId })
  });
}

async function anterior(usuarioId: number) {
  await fetch('http://localhost:8080/api/v1/reproductor/anterior', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ usuarioId })
  });
}
```

### 6.4 Actualizar Posición

```typescript
async function actualizarPosicion(usuarioId: number, segundos: number) {
  await fetch('http://localhost:8080/api/v1/reproductor/posicion', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      usuarioId,
      posicionSegundos: segundos
    })
  });
}
```

### 6.5 Actualizar Volumen

```typescript
async function actualizarVolumen(usuarioId: number, volumen: number) {
  await fetch('http://localhost:8080/api/v1/reproductor/volumen', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      usuarioId,
      volumen
    })
  });
}
```

### 6.6 Toggle Favorito

```typescript
async function toggleFavorito(usuarioId: number) {
  try {
    const response = await fetch(
      'http://localhost:8080/api/v1/reproductor/favorito/toggle',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ usuarioId })
      }
    );

    const result = await response.json();

    // result.esFavorita contiene el nuevo estado
    actualizarIconoFavorito(result.esFavorita);
    mostrarNotificacion(result.mensaje);

  } catch (error) {
    console.error('Error toggle favorito:', error);
  }
}
```

---

## 7. MANEJO DE ERRORES

### 7.1 Códigos de Error HTTP

| Código | Descripción | Acción Frontend |
|--------|-------------|-----------------|
| 200 | OK | Procesar respuesta normalmente |
| 201 | Created | Recurso creado exitosamente |
| 400 | Bad Request | Validación falló, mostrar mensaje de error |
| 404 | Not Found | Recurso no encontrado |
| 500 | Internal Server Error | Error del servidor, mostrar mensaje genérico |

### 7.2 Estructura de Error

```json
{
  "mensaje": "Descripción del error",
  "estado": "ERROR"
}
```

### 7.3 Ejemplos de Errores Comunes

#### Error: No hay canción en reproducción
```json
{
  "mensaje": "No hay ninguna canción en reproducción",
  "estado": "ERROR"
}
```

#### Error: Volumen inválido
```json
{
  "mensaje": "El volumen debe estar entre 0 y 100",
  "estado": "ERROR"
}
```

#### Error: Cola vacía
```json
{
  "mensaje": "La cola está vacía",
  "estado": "ERROR"
}
```

### 7.4 Manejo de Errores en Frontend

```typescript
async function manejarPeticion(url: string, options: RequestInit) {
  try {
    const response = await fetch(url, options);

    if (!response.ok) {
      if (response.status === 400) {
        const error = await response.json();
        mostrarError(error.mensaje);
        return null;
      }

      if (response.status === 500) {
        mostrarError('Error del servidor. Intente nuevamente.');
        return null;
      }

      throw new Error(`HTTP ${response.status}`);
    }

    return await response.json();

  } catch (error) {
    console.error('Error en petición:', error);
    mostrarError('Error de conexión');
    return null;
  }
}
```

---

## 8. ESTADOS DEL REPRODUCTOR

### 8.1 Estados Posibles

```typescript
type EstadoUI = 
  | 'INICIAL'          // Cargando datos iniciales
  | 'CARGANDO'         // Obteniendo stream
  | 'REPRODUCIENDO'    // Reproduciendo audio
  | 'PAUSADO'          // Audio pausado
  | 'ERROR'            // Error
  | 'VACIO';           // Sin canción
```

### 8.2 Transiciones de Estado

```
INICIAL → CARGANDO → REPRODUCIENDO
                   ↓
                 ERROR

REPRODUCIENDO ↔ PAUSADO

REPRODUCIENDO → CARGANDO (siguiente/anterior)
```

### 8.3 UI según Estado

| Estado | Título | Botón Principal | Spinner | Controles |
|--------|--------|-----------------|---------|-----------|
| INICIAL | "Cargando..." | Deshabilitado | Visible | Deshabilitados |
| CARGANDO | "Cargando..." | Deshabilitado | Visible | Deshabilitados |
| REPRODUCIENDO | Título real | "Pause" | Oculto | Habilitados |
| PAUSADO | Título real | "Play" | Oculto | Habilitados |
| ERROR | "Error" | "Reintentar" | Oculto | Deshabilitados |
| VACIO | "Sin canción" | Deshabilitado | Oculto | Deshabilitados |

---

## 9. CONSIDERACIONES IMPORTANTES

### 9.1 Tiempos de Respuesta

- **Estado del reproductor:** < 100ms
- **Play/Pause/Siguiente/Anterior:** < 200ms
- **Stream listo (WebSocket):** 2-5 segundos
- **Búsqueda:** 1-3 segundos

### 9.2 Manejo de WebSocket

- **Reconexión automática:** Sí (cada 5 segundos)
- **Heartbeat:** 4000ms entrada/salida
- **Timeout:** 5 segundos antes de mostrar error

### 9.3 Caché y Optimización

- Cachear estado del reproductor por 1 segundo
- Debounce en actualización de posición (500ms)
- Throttle en actualización de volumen (100ms)

### 9.4 Validaciones Frontend

Antes de enviar peticiones, validar:
- `usuarioId` > 0
- `posicionSegundos` >= 0
- `volumen` entre 0 y 100
- `videoId` no vacío

### 9.5 UX/UI Recomendaciones

- Mostrar spinner durante estado CARGANDO
- Deshabilitar controles durante CARGANDO
- Mostrar tooltip con título completo si es muy largo
- Actualizar barra de progreso cada 100ms
- Mostrar notificación al agregar a favoritos
- Confirmar antes de limpiar cola

---

## 10. CHECKLIST DE IMPLEMENTACIÓN

### Frontend debe implementar:

- [ ] Servicio HTTP para todos los endpoints
- [ ] Servicio WebSocket con reconexión
- [ ] Componente reproductor con todos los controles
- [ ] Componente cola de reproducción
- [ ] Manejo de estados (CARGANDO, REPRODUCIENDO, etc.)
- [ ] Manejo de errores con mensajes claros
- [ ] Validaciones antes de peticiones
- [ ] Spinner durante carga
- [ ] Notificaciones toast
- [ ] Actualización automática de UI vía WebSocket
- [ ] Barra de progreso con drag
- [ ] Control de volumen
- [ ] Botón favorito con toggle visual
- [ ] Lista de cola con reordenamiento drag&drop

---

## 11. SOPORTE Y CONTACTO

Para dudas técnicas sobre la API, contactar al equipo de backend.

**Endpoints de prueba disponibles en:**
- Desarrollo: `http://localhost:8080`
- Staging: `http://staging.emma-music.com`
- Producción: `http://api.emma-music.com`

---

**Fin de la documentación**

