GUIA DE INTEGRACION API BIBLIOTECA PARA FRONTEND

DESCRIPCION GENERAL
La API de Biblioteca proporciona acceso a la colección completa de canciones del usuario incluyendo favoritos, historial de reproducciones y estadísticas de escucha.

ENDPOINTS DISPONIBLES

1. OBTENER BIBLIOTECA COMPLETA

Endpoint: GET /api/v1/biblioteca/usuario/{usuarioId}
Autenticación: JWT Token requerido
Descripción: Retorna la biblioteca completa del usuario con favoritos, recientes y estadísticas

Parámetros de URL:
- usuarioId: Long (ID del usuario)

Ejemplo de Request:
GET /api/v1/biblioteca/usuario/1
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Ejemplo de Response 200 OK:
{
  "usuarioId": 1,
  "favoritos": [
    {
      "metadatoId": 1,
      "videoId": "rJ0D1GbDq1Q",
      "titulo": "Let Me Love You",
      "canal": "DJ Snake",
      "duracionSegundos": 206,
      "duracionTexto": "3:26",
      "miniaturaUrl": "https://i.ytimg.com/vi/rJ0D1GbDq1Q/maxresdefault.jpg",
      "esExplicita": false,
      "contadorReproducciones": 15,
      "fechaAdicion": "2025-01-15T10:30:00Z",
      "esFavorita": true,
      "vecesReproducida": 15
    }
  ],
  "recientementeReproducidas": [
    {
      "metadatoId": 2,
      "videoId": "kTJczUoc26U",
      "titulo": "Sorry",
      "canal": "Justin Bieber",
      "duracionSegundos": 200,
      "duracionTexto": "3:20",
      "miniaturaUrl": "https://i.ytimg.com/vi/kTJczUoc26U/maxresdefault.jpg",
      "esExplicita": false,
      "contadorReproducciones": 8,
      "fechaAdicion": "2025-01-19T14:20:00Z",
      "esFavorita": false,
      "vecesReproducida": 8
    }
  ],
  "estadisticas": {
    "totalFavoritos": 25,
    "totalReproducciones": 145,
    "totalPlaylists": 5,
    "totalCancionesUnicas": 78,
    "cancionMasReproducida": "Let Me Love You",
    "artistaFavorito": "DJ Snake",
    "minutosEscuchados": 480
  },
  "totalCanciones": 78
}

Códigos de Estado:
- 200: Biblioteca obtenida exitosamente
- 401: No autorizado (token inválido o expirado)
- 500: Error interno del servidor

2. OBTENER CANCIONES PAGINADAS

Endpoint: GET /api/v1/biblioteca/usuario/{usuarioId}/canciones
Autenticación: JWT Token requerido
Descripción: Retorna todas las canciones únicas del usuario paginadas

Parámetros de URL:
- usuarioId: Long (ID del usuario)

Parámetros de Query:
- pagina: Integer (opcional, default: 0)
- tamanio: Integer (opcional, default: 20)

Ejemplo de Request:
GET /api/v1/biblioteca/usuario/1/canciones?pagina=0&tamanio=20
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Ejemplo de Response 200 OK:
{
  "canciones": [
    {
      "metadatoId": 1,
      "videoId": "rJ0D1GbDq1Q",
      "titulo": "Let Me Love You",
      "canal": "DJ Snake",
      "duracionSegundos": 206,
      "duracionTexto": "3:26",
      "miniaturaUrl": "https://i.ytimg.com/vi/rJ0D1GbDq1Q/maxresdefault.jpg",
      "esExplicita": false,
      "contadorReproducciones": 15,
      "fechaAdicion": "2025-01-15T10:30:00Z",
      "esFavorita": true,
      "vecesReproducida": 15
    }
  ],
  "paginaActual": 0,
  "tamanoPagina": 20,
  "totalPaginas": 4,
  "totalElementos": 78,
  "esUltimaPagina": false,
  "esPrimeraPagina": true
}

Códigos de Estado:
- 200: Canciones obtenidas exitosamente
- 401: No autorizado
- 500: Error interno del servidor

3. OBTENER FAVORITOS PAGINADOS

Endpoint: GET /api/v1/biblioteca/usuario/{usuarioId}/favoritos
Autenticación: JWT Token requerido
Descripción: Retorna solo las canciones favoritas del usuario paginadas

Parámetros de URL:
- usuarioId: Long (ID del usuario)

Parámetros de Query:
- pagina: Integer (opcional, default: 0)
- tamanio: Integer (opcional, default: 20)

Ejemplo de Request:
GET /api/v1/biblioteca/usuario/1/favoritos?pagina=0&tamanio=20
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Ejemplo de Response: (mismo formato que canciones paginadas)

4. OBTENER RECIENTES PAGINADAS

Endpoint: GET /api/v1/biblioteca/usuario/{usuarioId}/recientes
Autenticación: JWT Token requerido
Descripción: Retorna el historial de reproducciones recientes del usuario paginado

Parámetros de URL:
- usuarioId: Long (ID del usuario)

Parámetros de Query:
- pagina: Integer (opcional, default: 0)
- tamanio: Integer (opcional, default: 20)

Ejemplo de Request:
GET /api/v1/biblioteca/usuario/1/recientes?pagina=0&tamanio=20
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Ejemplo de Response: (mismo formato que canciones paginadas)

5. OBTENER ESTADISTICAS

Endpoint: GET /api/v1/biblioteca/usuario/{usuarioId}/estadisticas
Autenticación: JWT Token requerido
Descripción: Retorna estadísticas detalladas de escucha del usuario

Parámetros de URL:
- usuarioId: Long (ID del usuario)

Ejemplo de Request:
GET /api/v1/biblioteca/usuario/1/estadisticas
Headers:
  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Ejemplo de Response 200 OK:
{
  "totalFavoritos": 25,
  "totalReproducciones": 145,
  "totalPlaylists": 5,
  "totalCancionesUnicas": 78,
  "cancionMasReproducida": "Let Me Love You",
  "artistaFavorito": "DJ Snake",
  "minutosEscuchados": 480
}

MODELOS DE DATOS

BibliotecaDTO:
{
  usuarioId: number
  favoritos: CancionBibliotecaDTO[]
  recientementeReproducidas: CancionBibliotecaDTO[]
  estadisticas: EstadisticasBibliotecaDTO
  totalCanciones: number
}

CancionBibliotecaDTO:
{
  metadatoId: number
  videoId: string
  titulo: string
  canal: string
  duracionSegundos: number
  duracionTexto: string
  miniaturaUrl: string
  esExplicita: boolean
  contadorReproducciones: number
  fechaAdicion: string (ISO 8601)
  esFavorita: boolean
  vecesReproducida: number
}

BibliotecaPaginadaDTO:
{
  canciones: CancionBibliotecaDTO[]
  paginaActual: number
  tamanoPagina: number
  totalPaginas: number
  totalElementos: number
  esUltimaPagina: boolean
  esPrimeraPagina: boolean
}

EstadisticasBibliotecaDTO:
{
  totalFavoritos: number
  totalReproducciones: number
  totalPlaylists: number
  totalCancionesUnicas: number
  cancionMasReproducida: string
  artistaFavorito: string
  minutosEscuchados: number
}

EJEMPLOS DE CODIGO

TypeScript/Angular:

export interface CancionBiblioteca {
  metadatoId: number;
  videoId: string;
  titulo: string;
  canal: string;
  duracionSegundos: number;
  duracionTexto: string;
  miniaturaUrl: string;
  esExplicita: boolean;
  contadorReproducciones: number;
  fechaAdicion: string;
  esFavorita: boolean;
  vecesReproducida: number;
}

export interface Biblioteca {
  usuarioId: number;
  favoritos: CancionBiblioteca[];
  recientementeReproducidas: CancionBiblioteca[];
  estadisticas: EstadisticasBiblioteca;
  totalCanciones: number;
}

export interface BibliotecaPaginada {
  canciones: CancionBiblioteca[];
  paginaActual: number;
  tamanoPagina: number;
  totalPaginas: number;
  totalElementos: number;
  esUltimaPagina: boolean;
  esPrimeraPagina: boolean;
}

export interface EstadisticasBiblioteca {
  totalFavoritos: number;
  totalReproducciones: number;
  totalPlaylists: number;
  totalCancionesUnicas: number;
  cancionMasReproducida: string;
  artistaFavorito: string;
  minutosEscuchados: number;
}

Servicio Angular:

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BibliotecaService {
  private baseUrl = 'http://localhost:8080/api/v1/biblioteca';

  constructor(private http: HttpClient) {}

  obtenerBibliotecaCompleta(usuarioId: number): Observable<Biblioteca> {
    return this.http.get<Biblioteca>(`${this.baseUrl}/usuario/${usuarioId}`);
  }

  obtenerCancionesPaginadas(
    usuarioId: number,
    pagina: number = 0,
    tamanio: number = 20
  ): Observable<BibliotecaPaginada> {
    return this.http.get<BibliotecaPaginada>(
      `${this.baseUrl}/usuario/${usuarioId}/canciones`,
      { params: { pagina: pagina.toString(), tamanio: tamanio.toString() } }
    );
  }

  obtenerFavoritosPaginados(
    usuarioId: number,
    pagina: number = 0,
    tamanio: number = 20
  ): Observable<BibliotecaPaginada> {
    return this.http.get<BibliotecaPaginada>(
      `${this.baseUrl}/usuario/${usuarioId}/favoritos`,
      { params: { pagina: pagina.toString(), tamanio: tamanio.toString() } }
    );
  }

  obtenerRecientesPaginadas(
    usuarioId: number,
    pagina: number = 0,
    tamanio: number = 20
  ): Observable<BibliotecaPaginada> {
    return this.http.get<BibliotecaPaginada>(
      `${this.baseUrl}/usuario/${usuarioId}/recientes`,
      { params: { pagina: pagina.toString(), tamanio: tamanio.toString() } }
    );
  }

  obtenerEstadisticas(usuarioId: number): Observable<EstadisticasBiblioteca> {
    return this.http.get<EstadisticasBiblioteca>(
      `${this.baseUrl}/usuario/${usuarioId}/estadisticas`
    );
  }
}

Componente Angular:

import { Component, OnInit } from '@angular/core';
import { BibliotecaService } from './biblioteca.service';

@Component({
  selector: 'app-biblioteca',
  templateUrl: './biblioteca.component.html'
})
export class BibliotecaComponent implements OnInit {
  biblioteca: Biblioteca;
  cancionesPaginadas: BibliotecaPaginada;
  cargando = false;
  paginaActual = 0;
  tamanoPagina = 20;

  constructor(private bibliotecaService: BibliotecaService) {}

  ngOnInit() {
    this.cargarBiblioteca();
  }

  cargarBiblioteca() {
    this.cargando = true;
    const usuarioId = 1;

    this.bibliotecaService.obtenerBibliotecaCompleta(usuarioId).subscribe({
      next: (data) => {
        this.biblioteca = data;
        this.cargando = false;
      },
      error: (error) => {
        console.error('Error cargando biblioteca:', error);
        this.cargando = false;
      }
    });
  }

  cargarCancionesPaginadas(pagina: number) {
    this.cargando = true;
    const usuarioId = 1;

    this.bibliotecaService
      .obtenerCancionesPaginadas(usuarioId, pagina, this.tamanoPagina)
      .subscribe({
        next: (data) => {
          this.cancionesPaginadas = data;
          this.paginaActual = data.paginaActual;
          this.cargando = false;
        },
        error: (error) => {
          console.error('Error cargando canciones:', error);
          this.cargando = false;
        }
      });
  }

  paginaSiguiente() {
    if (!this.cancionesPaginadas?.esUltimaPagina) {
      this.cargarCancionesPaginadas(this.paginaActual + 1);
    }
  }

  paginaAnterior() {
    if (!this.cancionesPaginadas?.esPrimeraPagina) {
      this.cargarCancionesPaginadas(this.paginaActual - 1);
    }
  }
}

MANEJO DE ERRORES

Todos los endpoints pueden retornar los siguientes errores:

Error 401 Unauthorized:
{
  "timestamp": "2025-01-19T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT inválido o expirado",
  "path": "/api/v1/biblioteca/usuario/1"
}

Error 500 Internal Server Error:
{
  "timestamp": "2025-01-19T10:30:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error procesando solicitud",
  "path": "/api/v1/biblioteca/usuario/1"
}

Recomendaciones:
- Implementar retry logic para errores 500
- Mostrar mensaje amigable al usuario en caso de error
- Guardar estado de paginación en el componente
- Implementar skeleton loaders durante la carga
- Cachear datos de estadísticas por 5 minutos

PAGINACION

La API usa paginación basada en offset con los siguientes parámetros:
- pagina: Número de página (base 0)
- tamanio: Elementos por página (default: 20, máximo recomendado: 50)

Ejemplo de navegación:
1. Primera página: pagina=0
2. Segunda página: pagina=1
3. Tercera página: pagina=2

La respuesta incluye:
- paginaActual: Página actual
- totalPaginas: Total de páginas disponibles
- totalElementos: Total de elementos en todos los resultados
- esUltimaPagina: true si es la última página
- esPrimeraPagina: true si es la primera página

OPTIMIZACION Y RENDIMIENTO

1. Carga Inicial:
   - Usar obtenerBibliotecaCompleta() solo en la vista principal
   - Limitar a 50 elementos máximo en favoritos y recientes

2. Navegación:
   - Usar endpoints paginados para listados grandes
   - Implementar virtual scrolling para listas largas

3. Estadísticas:
   - Cachear estadísticas por 5 minutos
   - Solo actualizar al agregar/quitar favoritos

4. Imágenes:
   - Lazy loading para miniaturas
   - Usar placeholder mientras carga

CONSIDERACIONES ESPECIALES

1. Fechas:
   - Todas las fechas están en formato ISO 8601 UTC
   - Convertir a zona horaria local en el frontend

2. Duración:
   - duracionSegundos: Para cálculos
   - duracionTexto: Para mostrar al usuario

3. Favoritos:
   - Campo esFavorita indica si está en favoritos
   - Sincronizar con cambios en módulo de favoritos

4. Estadísticas:
   - Basadas en últimos 30 días
   - Actualizadas en tiempo real

FLUJO RECOMENDADO

1. Al cargar vista de biblioteca:
   - Llamar obtenerBibliotecaCompleta()
   - Mostrar favoritos (máximo 10)
   - Mostrar recientes (máximo 10)
   - Mostrar estadísticas

2. Al navegar a "Todas las canciones":
   - Llamar obtenerCancionesPaginadas()
   - Implementar scroll infinito o paginación

3. Al navegar a "Favoritos":
   - Llamar obtenerFavoritosPaginados()
   - Permitir filtrado y búsqueda

4. Al navegar a "Historial":
   - Llamar obtenerRecientesPaginadas()
   - Mostrar ordenado por fecha

INTEGRACION CON OTROS MODULOS

1. Reproducción:
   - Al hacer clic en canción, usar videoId para reproducir
   - Sincronizar estado esFavorita con reproductor

2. Favoritos:
   - Al agregar/quitar favorito, recargar biblioteca
   - Actualizar campo esFavorita en tiempo real

3. Búsqueda:
   - Permitir buscar dentro de la biblioteca
   - Filtrar por título, canal o favoritos

TESTING

Casos de prueba recomendados:
1. Biblioteca vacía (usuario nuevo)
2. Biblioteca con pocos elementos (< 20)
3. Biblioteca con muchos elementos (> 100)
4. Paginación correcta
5. Manejo de errores de red
6. Actualización en tiempo real
7. Performance con muchas imágenes

URLS DE PRODUCCION

Desarrollo: http://localhost:8080/api/v1/biblioteca
Producción: https://api.emma-music.com/api/v1/biblioteca

VERSIONAMIENTO

Versión actual: 1.0
Última actualización: 2025-01-19
Próxima revisión: 2025-02-01

