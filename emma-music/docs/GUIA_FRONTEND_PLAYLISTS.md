# GUÍA API FRONTEND - MÓDULO DE PLAYLISTS/LISTAS DE REPRODUCCIÓN

**Fecha**: 2025-11-20  
**Estado**: IMPLEMENTADO Y FUNCIONAL  
**Versión Backend**: Spring Boot 3.5.7  
**Base URL**: `http://localhost:8080/api/v1/playlists`

---

## ÍNDICE

1. [Endpoints Disponibles](#endpoints-disponibles)
2. [Modelos de Datos](#modelos-de-datos)
3. [Ejemplos de Peticiones HTTP](#ejemplos-de-peticiones-http)
4. [Manejo de Errores](#manejo-de-errores)
5. [Flujos de Usuario](#flujos-de-usuario)
6. [Implementación TypeScript](#implementación-typescript)

---

## ENDPOINTS DISPONIBLES

### 1. CREAR PLAYLIST

**Endpoint**: `POST /api/v1/playlists`  
**Autenticación**: JWT requerido  
**Descripción**: Crea una nueva playlist para un usuario

**Query Parameters**:
```typescript
{
  usuarioId: number  // ID del usuario propietario
}
```

**Request Body**:
```json
{
  "titulo": "Mis Favoritas 2025",
  "descripcion": "Las mejores canciones del año",
  "urlImagenPortada": "https://ejemplo.com/imagen.jpg",
  "esPublica": false,
  "esColaborativa": false
}
```

**Response 201 CREATED**:
```json
{
  "listaId": 1,
  "usuarioId": 1,
  "nombreUsuario": "usuario123",
  "titulo": "Mis Favoritas 2025",
  "descripcion": "Las mejores canciones del año",
  "urlImagenPortada": "https://ejemplo.com/imagen.jpg",
  "esPublica": false,
  "esColaborativa": false,
  "fechaCreacion": "2025-11-20T00:00:00Z",
  "fechaActualizacion": "2025-11-20T00:00:00Z",
  "totalCanciones": 0,
  "canciones": []
}
```

---

### 2. OBTENER PLAYLIST POR ID

**Endpoint**: `GET /api/v1/playlists/{listaId}`  
**Autenticación**: JWT requerido  
**Descripción**: Obtiene los detalles completos de una playlist

**Path Parameters**:
```typescript
{
  listaId: number  // ID de la playlist
}
```

**Response 200 OK**:
```json
{
  "listaId": 1,
  "usuarioId": 1,
  "nombreUsuario": "usuario123",
  "titulo": "Mis Favoritas 2025",
  "descripcion": "Las mejores canciones del año",
  "urlImagenPortada": "https://ejemplo.com/imagen.jpg",
  "esPublica": false,
  "esColaborativa": false,
  "fechaCreacion": "2025-11-20T00:00:00Z",
  "fechaActualizacion": "2025-11-20T00:00:00Z",
  "totalCanciones": 5,
  "canciones": [
    {
      "metadatoId": 1,
      "idVideoYoutube": "rJ0D1GbDq1Q",
      "titulo": "Let Me Love You",
      "canal": "DJ Snake",
      "duracionSegundos": 206,
      "duracionTexto": "3:26",
      "miniaturaUrl": "https://i.ytimg.com/vi/rJ0D1GbDq1Q/maxresdefault.jpg",
      "esExplicito": false,
      "posicion": 0,
      "fechaAdicion": "2025-11-20T00:00:00Z",
      "anadidoPor": "usuario123"
    }
  ]
}
```

---

### 3. OBTENER PLAYLISTS DE USUARIO

**Endpoint**: `GET /api/v1/playlists/usuario/{usuarioId}`  
**Autenticación**: JWT requerido  
**Descripción**: Obtiene todas las playlists de un usuario

**Path Parameters**:
```typescript
{
  usuarioId: number  // ID del usuario
}
```

**Response 200 OK**:
```json
[
  {
    "listaId": 1,
    "usuarioId": 1,
    "nombreUsuario": "usuario123",
    "titulo": "Mis Favoritas 2025",
    "descripcion": "Las mejores canciones del año",
    "urlImagenPortada": "https://ejemplo.com/imagen.jpg",
    "esPublica": false,
    "esColaborativa": false,
    "fechaCreacion": "2025-11-20T00:00:00Z",
    "fechaActualizacion": "2025-11-20T00:00:00Z",
    "totalCanciones": 5,
    "canciones": null
  },
  {
    "listaId": 2,
    "usuarioId": 1,
    "nombreUsuario": "usuario123",
    "titulo": "Workout Mix",
    "descripcion": "Energía para entrenar",
    "urlImagenPortada": null,
    "esPublica": true,
    "esColaborativa": false,
    "fechaCreacion": "2025-11-20T01:00:00Z",
    "fechaActualizacion": "2025-11-20T01:00:00Z",
    "totalCanciones": 12,
    "canciones": null
  }
]
```

---

### 4. CONTAR PLAYLISTS DE USUARIO

**Endpoint**: `GET /api/v1/playlists/usuario/{usuarioId}/count`  
**Autenticación**: JWT requerido  
**Descripción**: Obtiene el número total de playlists de un usuario

**Path Parameters**:
```typescript
{
  usuarioId: number  // ID del usuario
}
```

**Response 200 OK**:
```json
{
  "count": 5,
  "usuarioId": 1
}
```

---

### 5. OBTENER PLAYLISTS PÚBLICAS

**Endpoint**: `GET /api/v1/playlists/publicas`  
**Autenticación**: JWT requerido  
**Descripción**: Obtiene todas las playlists públicas del sistema

**Response 200 OK**:
```json
[
  {
    "listaId": 3,
    "usuarioId": 2,
    "nombreUsuario": "djmaster",
    "titulo": "Top Hits 2025",
    "descripcion": "Las canciones más populares",
    "urlImagenPortada": "https://ejemplo.com/tophits.jpg",
    "esPublica": true,
    "esColaborativa": false,
    "fechaCreacion": "2025-11-19T00:00:00Z",
    "fechaActualizacion": "2025-11-20T00:00:00Z",
    "totalCanciones": 25,
    "canciones": null
  }
]
```

---

### 6. ACTUALIZAR PLAYLIST

**Endpoint**: `PUT /api/v1/playlists/{listaId}`  
**Autenticación**: JWT requerido  
**Descripción**: Actualiza los datos de una playlist

**Path Parameters**:
```typescript
{
  listaId: number  // ID de la playlist
}
```

**Query Parameters**:
```typescript
{
  usuarioId: number  // ID del usuario propietario
}
```

**Request Body**:
```json
{
  "titulo": "Mis Favoritas 2025 - Actualizado",
  "descripcion": "Las mejores canciones del año - Edición especial",
  "urlImagenPortada": "https://ejemplo.com/imagen-nueva.jpg",
  "esPublica": true,
  "esColaborativa": true
}
```

**Response 200 OK**:
```json
{
  "listaId": 1,
  "usuarioId": 1,
  "nombreUsuario": "usuario123",
  "titulo": "Mis Favoritas 2025 - Actualizado",
  "descripcion": "Las mejores canciones del año - Edición especial",
  "urlImagenPortada": "https://ejemplo.com/imagen-nueva.jpg",
  "esPublica": true,
  "esColaborativa": true,
  "fechaCreacion": "2025-11-20T00:00:00Z",
  "fechaActualizacion": "2025-11-20T02:00:00Z",
  "totalCanciones": 5,
  "canciones": null
}
```

---

### 7. ELIMINAR PLAYLIST

**Endpoint**: `DELETE /api/v1/playlists/{listaId}`  
**Autenticación**: JWT requerido  
**Descripción**: Elimina una playlist y todas sus canciones

**Path Parameters**:
```typescript
{
  listaId: number  // ID de la playlist
}
```

**Query Parameters**:
```typescript
{
  usuarioId: number  // ID del usuario propietario
}
```

**Response 200 OK**:
```json
{
  "mensaje": "Playlist eliminada"
}
```

---

### 8. AGREGAR CANCIÓN A PLAYLIST

**Endpoint**: `POST /api/v1/playlists/{listaId}/canciones`  
**Autenticación**: JWT requerido  
**Descripción**: Agrega una canción a una playlist

**Path Parameters**:
```typescript
{
  listaId: number  // ID de la playlist
}
```

**Query Parameters**:
```typescript
{
  videoId: string,   // ID del video de YouTube
  usuarioId: number  // ID del usuario
}
```

**Response 201 CREATED**:
```json
{
  "metadatoId": 1,
  "idVideoYoutube": "rJ0D1GbDq1Q",
  "titulo": "Let Me Love You",
  "canal": "DJ Snake",
  "duracionSegundos": 206,
  "duracionTexto": "3:26",
  "miniaturaUrl": "https://i.ytimg.com/vi/rJ0D1GbDq1Q/maxresdefault.jpg",
  "esExplicito": false,
  "posicion": 5,
  "fechaAdicion": "2025-11-20T02:00:00Z",
  "anadidoPor": "usuario123"
}
```

---

### 9. OBTENER CANCIONES DE PLAYLIST

**Endpoint**: `GET /api/v1/playlists/{listaId}/canciones`  
**Autenticación**: JWT requerido  
**Descripción**: Obtiene todas las canciones de una playlist ordenadas por posición

**Path Parameters**:
```typescript
{
  listaId: number  // ID de la playlist
}
```

**Response 200 OK**:
```json
[
  {
    "metadatoId": 1,
    "idVideoYoutube": "rJ0D1GbDq1Q",
    "titulo": "Let Me Love You",
    "canal": "DJ Snake",
    "duracionSegundos": 206,
    "duracionTexto": "3:26",
    "miniaturaUrl": "https://i.ytimg.com/vi/rJ0D1GbDq1Q/maxresdefault.jpg",
    "esExplicito": false,
    "posicion": 0,
    "fechaAdicion": "2025-11-20T00:00:00Z",
    "anadidoPor": "usuario123"
  },
  {
    "metadatoId": 2,
    "idVideoYoutube": "xyz123",
    "titulo": "Sorry",
    "canal": "Justin Bieber",
    "duracionSegundos": 201,
    "duracionTexto": "3:21",
    "miniaturaUrl": "https://i.ytimg.com/vi/xyz123/maxresdefault.jpg",
    "esExplicito": false,
    "posicion": 1,
    "fechaAdicion": "2025-11-20T00:30:00Z",
    "anadidoPor": "usuario123"
  }
]
```

---

### 10. BUSCAR CANCIONES PARA AGREGAR

**Endpoint**: `GET /api/v1/playlists/{listaId}/canciones/buscar`  
**Autenticación**: JWT requerido  
**Descripción**: Busca canciones disponibles para agregar a la playlist (excluye las que ya están)

**Path Parameters**:
```typescript
{
  listaId: number  // ID de la playlist
}
```

**Query Parameters**:
```typescript
{
  consulta: string  // Término de búsqueda
}
```

**Response 200 OK**:
```json
[
  {
    "metadatoId": 10,
    "idVideoYoutube": "abc456",
    "titulo": "Shape of You",
    "canal": "Ed Sheeran",
    "duracionSegundos": 234,
    "duracionTexto": "3:54",
    "miniaturaUrl": "https://i.ytimg.com/vi/abc456/maxresdefault.jpg",
    "esExplicito": false,
    "posicion": null,
    "fechaAdicion": null,
    "anadidoPor": null
  }
]
```

---

### 11. ELIMINAR CANCIÓN DE PLAYLIST

**Endpoint**: `DELETE /api/v1/playlists/{listaId}/canciones/{videoId}`  
**Autenticación**: JWT requerido  
**Descripción**: Elimina una canción de la playlist

**Path Parameters**:
```typescript
{
  listaId: number,  // ID de la playlist
  videoId: string   // ID del video de YouTube
}
```

**Query Parameters**:
```typescript
{
  usuarioId: number  // ID del usuario
}
```

**Response 200 OK**:
```json
{
  "mensaje": "Canción eliminada de playlist"
}
```

---

### 12. REORDENAR CANCIÓN EN PLAYLIST

**Endpoint**: `PUT /api/v1/playlists/{listaId}/canciones/{videoId}/reordenar`  
**Autenticación**: JWT requerido  
**Descripción**: Cambia la posición de una canción dentro de la playlist

**Path Parameters**:
```typescript
{
  listaId: number,  // ID de la playlist
  videoId: string   // ID del video de YouTube
}
```

**Query Parameters**:
```typescript
{
  nuevaPosicion: number,  // Nueva posición (0-indexed)
  usuarioId: number       // ID del usuario
}
```

**Response 200 OK**:
```json
{
  "mensaje": "Canción reordenada"
}
```

---

### 13. CONTAR CANCIONES EN PLAYLIST

**Endpoint**: `GET /api/v1/playlists/{listaId}/total`  
**Autenticación**: JWT requerido  
**Descripción**: Obtiene el número total de canciones en una playlist

**Path Parameters**:
```typescript
{
  listaId: number  // ID de la playlist
}
```

**Response 200 OK**:
```json
{
  "total": 12
}
```

---

## MODELOS DE DATOS

### ListaReproduccionDTO

```typescript
interface ListaReproduccionDTO {
  listaId: number;
  usuarioId: number;
  nombreUsuario: string;
  titulo: string;
  descripcion: string | null;
  urlImagenPortada: string | null;
  esPublica: boolean;
  esColaborativa: boolean;
  fechaCreacion: string;  // ISO 8601 timestamp
  fechaActualizacion: string;  // ISO 8601 timestamp
  totalCanciones: number;
  canciones: CancionPlaylistDTO[] | null;
}
```

### CancionPlaylistDTO

```typescript
interface CancionPlaylistDTO {
  metadatoId: number;
  idVideoYoutube: string;
  titulo: string;
  canal: string;
  duracionSegundos: number;
  duracionTexto: string;  // Formato "mm:ss"
  miniaturaUrl: string;
  esExplicito: boolean;
  posicion: number | null;  // null cuando es resultado de búsqueda
  fechaAdicion: string | null;  // ISO 8601 timestamp, null cuando es resultado de búsqueda
  anadidoPor: string | null;  // null cuando es resultado de búsqueda
}
```

### ListaReproduccionCrearDTO

```typescript
interface ListaReproduccionCrearDTO {
  titulo: string;  // Obligatorio, máximo 255 caracteres
  descripcion: string | null;  // Opcional
  urlImagenPortada: string | null;  // Opcional
  esPublica: boolean;  // Default: false
  esColaborativa: boolean;  // Default: false
}
```

---

## EJEMPLOS DE PETICIONES HTTP

### Ejemplo 1: Crear una Playlist

```typescript
// Angular / TypeScript
const crearPlaylist = async () => {
  const usuarioId = 1;
  
  const data: ListaReproduccionCrearDTO = {
    titulo: "Mi Nueva Playlist",
    descripcion: "Descripción de la playlist",
    urlImagenPortada: null,
    esPublica: false,
    esColaborativa: false
  };

  const response = await fetch(`/api/v1/playlists?usuarioId=${usuarioId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(data)
  });

  const playlist: ListaReproduccionDTO = await response.json();
  console.log('Playlist creada:', playlist);
};
```

### Ejemplo 2: Obtener Playlists del Usuario

```typescript
// Angular / TypeScript
const obtenerMisPlaylists = async () => {
  const usuarioId = 1;
  
  const response = await fetch(`/api/v1/playlists/usuario/${usuarioId}`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  const playlists: ListaReproduccionDTO[] = await response.json();
  console.log('Mis playlists:', playlists);
};
```

### Ejemplo 3: Agregar Canción a Playlist

```typescript
// Angular / TypeScript
const agregarCancion = async () => {
  const listaId = 1;
  const videoId = "rJ0D1GbDq1Q";
  const usuarioId = 1;
  
  const response = await fetch(
    `/api/v1/playlists/${listaId}/canciones?videoId=${videoId}&usuarioId=${usuarioId}`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );

  const cancion: CancionPlaylistDTO = await response.json();
  console.log('Canción agregada:', cancion);
};
```

### Ejemplo 4: Buscar Canciones para Agregar

```typescript
// Angular / TypeScript
const buscarCanciones = async () => {
  const listaId = 1;
  const consulta = "justin bieber";
  
  const response = await fetch(
    `/api/v1/playlists/${listaId}/canciones/buscar?consulta=${encodeURIComponent(consulta)}`,
    {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );

  const canciones: CancionPlaylistDTO[] = await response.json();
  console.log('Resultados de búsqueda:', canciones);
};
```

### Ejemplo 5: Reordenar Canción

```typescript
// Angular / TypeScript
const reordenarCancion = async () => {
  const listaId = 1;
  const videoId = "rJ0D1GbDq1Q";
  const nuevaPosicion = 3;
  const usuarioId = 1;
  
  const response = await fetch(
    `/api/v1/playlists/${listaId}/canciones/${videoId}/reordenar?nuevaPosicion=${nuevaPosicion}&usuarioId=${usuarioId}`,
    {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );

  const resultado = await response.json();
  console.log('Resultado:', resultado);
};
```

---

## MANEJO DE ERRORES

### Códigos de Error Comunes

| Código | Significado | Causa Común |
|--------|-------------|-------------|
| 400 | Bad Request | Datos inválidos en la petición |
| 401 | Unauthorized | Token JWT inválido o expirado |
| 403 | Forbidden | Usuario sin permisos para la operación |
| 404 | Not Found | Playlist o canción no encontrada |
| 409 | Conflict | Canción ya existe en la playlist |
| 500 | Internal Server Error | Error del servidor |

### Estructura de Respuesta de Error

```json
{
  "timestamp": "2025-11-20T00:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Playlist no encontrada con ID: 999",
  "path": "/api/v1/playlists/999"
}
```

### Ejemplo de Manejo de Errores

```typescript
const obtenerPlaylist = async (listaId: number) => {
  try {
    const response = await fetch(`/api/v1/playlists/${listaId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.message || 'Error desconocido');
    }

    const playlist: ListaReproduccionDTO = await response.json();
    return playlist;

  } catch (error) {
    console.error('Error obteniendo playlist:', error);
    throw error;
  }
};
```

---

## FLUJOS DE USUARIO

### Flujo 1: Crear Playlist y Agregar Canciones

```typescript
// 1. Usuario crea una nueva playlist
const playlist = await crearPlaylist({
  titulo: "Mi Playlist",
  descripcion: "Descripción",
  urlImagenPortada: null,
  esPublica: false,
  esColaborativa: false
});

// 2. Usuario busca canciones para agregar
const resultados = await buscarCancionesParaAgregar(
  playlist.listaId, 
  "justin bieber"
);

// 3. Usuario agrega varias canciones
for (const resultado of resultados.slice(0, 5)) {
  await agregarCancion(
    playlist.listaId, 
    resultado.idVideoYoutube, 
    usuarioId
  );
}

// 4. Usuario visualiza su playlist completa
const playlistCompleta = await obtenerPlaylist(playlist.listaId);
console.log('Playlist con canciones:', playlistCompleta);
```

### Flujo 2: Organizar Canciones en Playlist

```typescript
// 1. Usuario obtiene las canciones actuales
const canciones = await obtenerCancionesDePlaylist(listaId);

// 2. Usuario reordena una canción (mover de posición 5 a posición 0)
await reordenarCancion(
  listaId,
  canciones[5].idVideoYoutube,
  0,
  usuarioId
);

// 3. Usuario elimina canciones que ya no quiere
await eliminarCancion(
  listaId,
  canciones[3].idVideoYoutube,
  usuarioId
);

// 4. Usuario verifica los cambios
const cancionesActualizadas = await obtenerCancionesDePlaylist(listaId);
console.log('Canciones actualizadas:', cancionesActualizadas);
```

### Flujo 3: Compartir Playlist

```typescript
// 1. Usuario actualiza playlist para hacerla pública
const playlistPublica = await actualizarPlaylist(
  listaId,
  usuarioId,
  {
    titulo: playlist.titulo,
    descripcion: playlist.descripcion,
    urlImagenPortada: playlist.urlImagenPortada,
    esPublica: true,  // Cambiar a pública
    esColaborativa: false
  }
);

// 2. Otros usuarios pueden ver las playlists públicas
const playlistsPublicas = await obtenerPlaylistsPublicas();
console.log('Playlists públicas disponibles:', playlistsPublicas);
```

---

## IMPLEMENTACIÓN TYPESCRIPT

### Servicio Angular Completo

```typescript
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

interface ListaReproduccionDTO {
  listaId: number;
  usuarioId: number;
  nombreUsuario: string;
  titulo: string;
  descripcion: string | null;
  urlImagenPortada: string | null;
  esPublica: boolean;
  esColaborativa: boolean;
  fechaCreacion: string;
  fechaActualizacion: string;
  totalCanciones: number;
  canciones: CancionPlaylistDTO[] | null;
}

interface CancionPlaylistDTO {
  metadatoId: number;
  idVideoYoutube: string;
  titulo: string;
  canal: string;
  duracionSegundos: number;
  duracionTexto: string;
  miniaturaUrl: string;
  esExplicito: boolean;
  posicion: number | null;
  fechaAdicion: string | null;
  anadidoPor: string | null;
}

interface ListaReproduccionCrearDTO {
  titulo: string;
  descripcion: string | null;
  urlImagenPortada: string | null;
  esPublica: boolean;
  esColaborativa: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {
  private readonly baseUrl = 'http://localhost:8080/api/v1/playlists';

  constructor(private http: HttpClient) {}

  crearPlaylist(
    usuarioId: number, 
    data: ListaReproduccionCrearDTO
  ): Observable<ListaReproduccionDTO> {
    const params = new HttpParams().set('usuarioId', usuarioId.toString());
    return this.http.post<ListaReproduccionDTO>(this.baseUrl, data, { params });
  }

  obtenerPlaylist(listaId: number): Observable<ListaReproduccionDTO> {
    return this.http.get<ListaReproduccionDTO>(`${this.baseUrl}/${listaId}`);
  }

  obtenerPlaylistsPorUsuario(usuarioId: number): Observable<ListaReproduccionDTO[]> {
    return this.http.get<ListaReproduccionDTO[]>(`${this.baseUrl}/usuario/${usuarioId}`);
  }

  contarPlaylistsPorUsuario(usuarioId: number): Observable<{ count: number; usuarioId: number }> {
    return this.http.get<{ count: number; usuarioId: number }>(
      `${this.baseUrl}/usuario/${usuarioId}/count`
    );
  }

  obtenerPlaylistsPublicas(): Observable<ListaReproduccionDTO[]> {
    return this.http.get<ListaReproduccionDTO[]>(`${this.baseUrl}/publicas`);
  }

  actualizarPlaylist(
    listaId: number,
    usuarioId: number,
    data: ListaReproduccionCrearDTO
  ): Observable<ListaReproduccionDTO> {
    const params = new HttpParams().set('usuarioId', usuarioId.toString());
    return this.http.put<ListaReproduccionDTO>(`${this.baseUrl}/${listaId}`, data, { params });
  }

  eliminarPlaylist(
    listaId: number,
    usuarioId: number
  ): Observable<{ mensaje: string }> {
    const params = new HttpParams().set('usuarioId', usuarioId.toString());
    return this.http.delete<{ mensaje: string }>(`${this.baseUrl}/${listaId}`, { params });
  }

  agregarCancion(
    listaId: number,
    videoId: string,
    usuarioId: number
  ): Observable<CancionPlaylistDTO> {
    const params = new HttpParams()
      .set('videoId', videoId)
      .set('usuarioId', usuarioId.toString());
    return this.http.post<CancionPlaylistDTO>(
      `${this.baseUrl}/${listaId}/canciones`,
      null,
      { params }
    );
  }

  obtenerCanciones(listaId: number): Observable<CancionPlaylistDTO[]> {
    return this.http.get<CancionPlaylistDTO[]>(`${this.baseUrl}/${listaId}/canciones`);
  }

  buscarCancionesParaAgregar(
    listaId: number,
    consulta: string
  ): Observable<CancionPlaylistDTO[]> {
    const params = new HttpParams().set('consulta', consulta);
    return this.http.get<CancionPlaylistDTO[]>(
      `${this.baseUrl}/${listaId}/canciones/buscar`,
      { params }
    );
  }

  eliminarCancion(
    listaId: number,
    videoId: string,
    usuarioId: number
  ): Observable<{ mensaje: string }> {
    const params = new HttpParams().set('usuarioId', usuarioId.toString());
    return this.http.delete<{ mensaje: string }>(
      `${this.baseUrl}/${listaId}/canciones/${videoId}`,
      { params }
    );
  }

  reordenarCancion(
    listaId: number,
    videoId: string,
    nuevaPosicion: number,
    usuarioId: number
  ): Observable<{ mensaje: string }> {
    const params = new HttpParams()
      .set('nuevaPosicion', nuevaPosicion.toString())
      .set('usuarioId', usuarioId.toString());
    return this.http.put<{ mensaje: string }>(
      `${this.baseUrl}/${listaId}/canciones/${videoId}/reordenar`,
      null,
      { params }
    );
  }

  contarCanciones(listaId: number): Observable<{ total: number }> {
    return this.http.get<{ total: number }>(`${this.baseUrl}/${listaId}/total`);
  }
}
```

### Componente de Ejemplo

```typescript
import { Component, OnInit } from '@angular/core';
import { PlaylistService } from './playlist.service';

@Component({
  selector: 'app-playlists',
  templateUrl: './playlists.component.html'
})
export class PlaylistsComponent implements OnInit {
  playlists: ListaReproduccionDTO[] = [];
  playlistSeleccionada: ListaReproduccionDTO | null = null;
  canciones: CancionPlaylistDTO[] = [];
  resultadosBusqueda: CancionPlaylistDTO[] = [];
  loading = false;

  constructor(private playlistService: PlaylistService) {}

  ngOnInit(): void {
    this.cargarPlaylists();
  }

  cargarPlaylists(): void {
    this.loading = true;
    const usuarioId = 1; // Obtener del servicio de autenticación

    this.playlistService.obtenerPlaylistsPorUsuario(usuarioId)
      .subscribe({
        next: (playlists) => {
          this.playlists = playlists;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error cargando playlists:', error);
          this.loading = false;
        }
      });
  }

  seleccionarPlaylist(listaId: number): void {
    this.playlistService.obtenerPlaylist(listaId)
      .subscribe({
        next: (playlist) => {
          this.playlistSeleccionada = playlist;
          this.cargarCanciones(listaId);
        },
        error: (error) => {
          console.error('Error cargando playlist:', error);
        }
      });
  }

  cargarCanciones(listaId: number): void {
    this.playlistService.obtenerCanciones(listaId)
      .subscribe({
        next: (canciones) => {
          this.canciones = canciones;
        },
        error: (error) => {
          console.error('Error cargando canciones:', error);
        }
      });
  }

  crearNuevaPlaylist(): void {
    const usuarioId = 1; // Obtener del servicio de autenticación
    
    const data: ListaReproduccionCrearDTO = {
      titulo: "Nueva Playlist",
      descripcion: null,
      urlImagenPortada: null,
      esPublica: false,
      esColaborativa: false
    };

    this.playlistService.crearPlaylist(usuarioId, data)
      .subscribe({
        next: (playlist) => {
          console.log('Playlist creada:', playlist);
          this.cargarPlaylists();
        },
        error: (error) => {
          console.error('Error creando playlist:', error);
        }
      });
  }

  buscarCanciones(consulta: string): void {
    if (!this.playlistSeleccionada) return;

    this.playlistService.buscarCancionesParaAgregar(
      this.playlistSeleccionada.listaId,
      consulta
    )
    .subscribe({
      next: (resultados) => {
        this.resultadosBusqueda = resultados;
      },
      error: (error) => {
        console.error('Error buscando canciones:', error);
      }
    });
  }

  agregarCancion(videoId: string): void {
    if (!this.playlistSeleccionada) return;
    
    const usuarioId = 1; // Obtener del servicio de autenticación

    this.playlistService.agregarCancion(
      this.playlistSeleccionada.listaId,
      videoId,
      usuarioId
    )
    .subscribe({
      next: (cancion) => {
        console.log('Canción agregada:', cancion);
        this.cargarCanciones(this.playlistSeleccionada!.listaId);
      },
      error: (error) => {
        console.error('Error agregando canción:', error);
      }
    });
  }

  eliminarCancion(videoId: string): void {
    if (!this.playlistSeleccionada) return;
    
    const usuarioId = 1; // Obtener del servicio de autenticación

    this.playlistService.eliminarCancion(
      this.playlistSeleccionada.listaId,
      videoId,
      usuarioId
    )
    .subscribe({
      next: (resultado) => {
        console.log(resultado.mensaje);
        this.cargarCanciones(this.playlistSeleccionada!.listaId);
      },
      error: (error) => {
        console.error('Error eliminando canción:', error);
      }
    });
  }

  reordenarCancion(videoId: string, nuevaPosicion: number): void {
    if (!this.playlistSeleccionada) return;
    
    const usuarioId = 1; // Obtener del servicio de autenticación

    this.playlistService.reordenarCancion(
      this.playlistSeleccionada.listaId,
      videoId,
      nuevaPosicion,
      usuarioId
    )
    .subscribe({
      next: (resultado) => {
        console.log(resultado.mensaje);
        this.cargarCanciones(this.playlistSeleccionada!.listaId);
      },
      error: (error) => {
        console.error('Error reordenando canción:', error);
      }
    });
  }
}
```

---

## NOTAS IMPORTANTES

### Validaciones del Backend

1. **Título de Playlist**: Obligatorio, máximo 255 caracteres
2. **Descripción**: Opcional, texto largo
3. **URL Imagen**: Opcional, debe ser URL válida
4. **Permisos**: Solo el propietario puede modificar/eliminar playlist
5. **Canciones Duplicadas**: No se permite agregar la misma canción dos veces
6. **Posiciones**: Las posiciones se reajustan automáticamente al eliminar/reordenar

### Consideraciones de Performance

1. **Paginación**: Actualmente no implementada, considerar para playlists con muchas canciones
2. **Cache**: Implementar cache en frontend para playlists frecuentemente consultadas
3. **Búsqueda**: Debounce recomendado de 500ms para búsquedas

### Roadmap Futuro

1. Implementar playlists colaborativas (múltiples usuarios editando)
2. Agregar sistema de likes/comentarios en playlists públicas
3. Implementar recomendaciones de canciones basadas en contenido de playlist
4. Agregar soporte para importar/exportar playlists

---

## RESUMEN

Este módulo de playlists proporciona funcionalidad completa para:

- Crear y gestionar playlists personalizadas
- Agregar, eliminar y reordenar canciones
- Buscar canciones para agregar
- Compartir playlists públicamente
- Gestionar permisos de edición

Todos los endpoints están protegidos con JWT y validados en el backend. El sistema garantiza integridad de datos y manejo correcto de permisos de usuario.

**Documentación actualizada**: 2025-11-20  
**Estado**: Producción Ready ✓

