# Guía de Integración: Buscar Canciones para Agregar a Playlist

## Descripción General

Este endpoint permite buscar canciones en el sistema (base de datos local y YouTube) para poder agregarlas a una playlist existente.

## Endpoint

### GET `/api/v1/playlists/{listaId}/canciones/buscar`

Busca canciones que pueden ser agregadas a una playlist específica.

## Parámetros

### Path Parameters
- `listaId` (Long, requerido): ID de la playlist donde se desean agregar canciones

### Query Parameters
- `consulta` (String, requerido): Término de búsqueda para encontrar canciones

## Headers
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

## Respuesta Exitosa (200 OK)

```json
[
  {
    "metadatoId": null,
    "idVideoYoutube": "rJ0D1GbDq1Q",
    "titulo": "Let Me Love You (feat. Justin Bieber)",
    "canal": "DJ Snake",
    "duracionSegundos": 206,
    "duracionTexto": "3:26",
    "miniaturaUrl": "https://i.ytimg.com/vi/rJ0D1GbDq1Q/maxresdefault.jpg",
    "esExplicito": false,
    "posicion": null,
    "fechaAdicion": null,
    "anadidoPor": null
  },
  {
    "metadatoId": null,
    "idVideoYoutube": "kTJczUoc26U",
    "titulo": "What Do You Mean?",
    "canal": "Justin Bieber",
    "duracionSegundos": 205,
    "duracionTexto": "3:25",
    "miniaturaUrl": "https://i.ytimg.com/vi/kTJczUoc26U/maxresdefault.jpg",
    "esExplicito": false,
    "posicion": null,
    "fechaAdicion": null,
    "anadidoPor": null
  }
]
```

## Errores Posibles

### 404 Not Found
Playlist no encontrada:
```json
{
  "timestamp": "2025-11-20T05:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Playlist no encontrada con id: 999",
  "path": "/api/v1/playlists/999/canciones/buscar"
}
```

### 400 Bad Request
Consulta vacía o inválida:
```json
{
  "timestamp": "2025-11-20T05:30:00.000Z",
  "status": 400,
  "error": "Bad Request",
  "message": "El parámetro 'consulta' es requerido",
  "path": "/api/v1/playlists/1/canciones/buscar"
}
```

### 500 Internal Server Error
Error del servidor:
```json
{
  "timestamp": "2025-11-20T05:30:00.000Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Ha ocurrido un error inesperado. Por favor contacte al administrador.",
  "path": "/api/v1/playlists/1/canciones/buscar"
}
```

## Ejemplos de Uso

### TypeScript/Angular

```typescript
// Modelo de datos
export interface CancionPlaylistDto {
  metadatoId?: number;
  idVideoYoutube: string;
  titulo: string;
  canal: string;
  duracionSegundos: number;
  duracionTexto: string;
  miniaturaUrl?: string;
  esExplicito: boolean;
  posicion?: number;
  fechaAdicion?: string;
  anadidoPor?: string;
}

// Servicio
export class PlaylistService {
  private apiUrl = 'http://localhost:8080/api/v1/playlists';

  constructor(private http: HttpClient) {}

  buscarCancionesParaAgregar(
    listaId: number,
    consulta: string
  ): Observable<CancionPlaylistDto[]> {
    const params = new HttpParams().set('consulta', consulta);
    
    return this.http.get<CancionPlaylistDto[]>(
      `${this.apiUrl}/${listaId}/canciones/buscar`,
      { params }
    ).pipe(
      tap(canciones => console.log(`Encontradas ${canciones.length} canciones`)),
      catchError(error => {
        console.error('Error buscando canciones:', error);
        return throwError(() => error);
      })
    );
  }
}

// Componente
export class PlaylistDetailComponent {
  canciones: CancionPlaylistDto[] = [];
  cargandoBusqueda = false;
  terminoBusqueda = '';

  constructor(
    private playlistService: PlaylistService,
    private route: ActivatedRoute
  ) {}

  buscarCanciones(): void {
    if (!this.terminoBusqueda.trim()) {
      return;
    }

    const listaId = Number(this.route.snapshot.paramMap.get('id'));
    this.cargandoBusqueda = true;

    this.playlistService
      .buscarCancionesParaAgregar(listaId, this.terminoBusqueda)
      .subscribe({
        next: (canciones) => {
          this.canciones = canciones;
          this.cargandoBusqueda = false;
        },
        error: (error) => {
          console.error('Error:', error);
          this.cargandoBusqueda = false;
        }
      });
  }
}
```

### JavaScript/React

```javascript
// Servicio
const API_URL = 'http://localhost:8080/api/v1/playlists';

export const buscarCancionesParaAgregar = async (listaId, consulta) => {
  try {
    const response = await fetch(
      `${API_URL}/${listaId}/canciones/buscar?consulta=${encodeURIComponent(consulta)}`,
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      }
    );

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const canciones = await response.json();
    console.log(`Encontradas ${canciones.length} canciones`);
    return canciones;
  } catch (error) {
    console.error('Error buscando canciones:', error);
    throw error;
  }
};

// Componente
const PlaylistDetail = () => {
  const [canciones, setCanciones] = useState([]);
  const [cargando, setCargando] = useState(false);
  const [terminoBusqueda, setTerminoBusqueda] = useState('');
  const { listaId } = useParams();

  const handleBuscar = async () => {
    if (!terminoBusqueda.trim()) return;

    setCargando(true);
    try {
      const resultados = await buscarCancionesParaAgregar(listaId, terminoBusqueda);
      setCanciones(resultados);
    } catch (error) {
      console.error('Error:', error);
    } finally {
      setCargando(false);
    }
  };

  return (
    <div>
      <input
        type="text"
        value={terminoBusqueda}
        onChange={(e) => setTerminoBusqueda(e.target.value)}
        placeholder="Buscar canciones..."
      />
      <button onClick={handleBuscar} disabled={cargando}>
        {cargando ? 'Buscando...' : 'Buscar'}
      </button>
      
      <ul>
        {canciones.map((cancion) => (
          <li key={cancion.idVideoYoutube}>
            <img src={cancion.miniaturaUrl} alt={cancion.titulo} />
            <div>
              <h4>{cancion.titulo}</h4>
              <p>{cancion.canal} • {cancion.duracionTexto}</p>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
};
```

## Flujo de Uso

1. **Usuario ingresa término de búsqueda**
   - Input: "justin bieber"
   - Frontend valida que no esté vacío

2. **Frontend hace petición GET**
   ```
   GET /api/v1/playlists/1/canciones/buscar?consulta=justin%20bieber
   ```

3. **Backend busca canciones**
   - Primero busca en base de datos local
   - Si no hay suficientes resultados, busca en YouTube
   - Guarda nuevos metadatos en BD

4. **Backend retorna resultados**
   - Lista de canciones con toda la información
   - Miniaturas, duración, canal, etc.

5. **Frontend muestra resultados**
   - Usuario puede ver lista de canciones
   - Puede agregar canciones a la playlist con otro endpoint

## Integración con Endpoint de Agregar Canción

Después de buscar, el usuario puede agregar una canción:

```typescript
agregarCancionAPlaylist(listaId: number, videoId: string, usuarioId: number) {
  const params = new HttpParams()
    .set('videoId', videoId)
    .set('usuarioId', usuarioId.toString());
    
  return this.http.post<CancionPlaylistDto>(
    `${this.apiUrl}/${listaId}/canciones`,
    null,
    { params }
  );
}
```

## Validaciones Frontend

```typescript
export class BuscarCancionesValidator {
  static validarConsulta(consulta: string): string | null {
    if (!consulta || consulta.trim().length === 0) {
      return 'El término de búsqueda no puede estar vacío';
    }
    
    if (consulta.trim().length < 2) {
      return 'El término de búsqueda debe tener al menos 2 caracteres';
    }
    
    if (consulta.length > 100) {
      return 'El término de búsqueda es muy largo (máximo 100 caracteres)';
    }
    
    return null;
  }
}
```

## Manejo de Estados en UI

```typescript
export interface BusquedaState {
  termino: string;
  resultados: CancionPlaylistDto[];
  cargando: boolean;
  error: string | null;
  ultimaBusqueda: string | null;
}

export const initialState: BusquedaState = {
  termino: '',
  resultados: [],
  cargando: false,
  error: null,
  ultimaBusqueda: null
};
```

## Optimizaciones Sugeridas

### Debounce en búsqueda
```typescript
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

searchControl = new FormControl('');

ngOnInit() {
  this.searchControl.valueChanges.pipe(
    debounceTime(300),
    distinctUntilChanged(),
    switchMap(term => {
      if (term && term.length >= 2) {
        return this.playlistService.buscarCancionesParaAgregar(this.listaId, term);
      }
      return of([]);
    })
  ).subscribe(canciones => {
    this.canciones = canciones;
  });
}
```

### Caché de resultados
```typescript
private cache = new Map<string, CancionPlaylistDto[]>();

buscarCancionesParaAgregar(listaId: number, consulta: string): Observable<CancionPlaylistDto[]> {
  const cacheKey = `${listaId}-${consulta}`;
  
  if (this.cache.has(cacheKey)) {
    return of(this.cache.get(cacheKey)!);
  }
  
  return this.http.get<CancionPlaylistDto[]>(...).pipe(
    tap(canciones => this.cache.set(cacheKey, canciones))
  );
}
```

## Notas Importantes

1. **Autenticación requerida**: Todos los requests deben incluir token JWT
2. **Encoding de URL**: El parámetro `consulta` debe estar URL-encoded
3. **Rate limiting**: Máximo 20 búsquedas por minuto por usuario
4. **Resultados máximos**: Se retornan hasta 20 canciones por búsqueda
5. **Búsqueda híbrida**: Combina resultados de BD local y YouTube API
6. **Campos opcionales**: `metadatoId`, `posicion`, `fechaAdicion` y `anadidoPor` serán `null` en resultados de búsqueda

## Troubleshooting

### Error 500 - Transaction Read-Only (RESUELTO COMPLETAMENTE)

**Problema Original:**
```
ERROR: no se puede ejecutar INSERT en una transacción de sólo lectura
org.springframework.orm.jpa.JpaSystemException: could not execute statement
```

**Causa Raíz Identificada:**
El problema ocurría por transacciones anidadas con configuraciones conflictivas:

```java
// PlaylistService - Transacción PADRE (readOnly = true)
@Transactional(readOnly = true)
public List<CancionPlaylistDTO> buscarCancionesParaAgregar() {
    // Llama a BuscadorService...
    buscadorService.buscarCanciones(consulta);
}

// BuscadorService - Transacción HIJA (sin propagation específica)
@Transactional  // Por defecto: REQUIRED (usa transacción padre)
public List<ResultadoBusquedaDTO> buscarCanciones() {
    // Intenta guardar metadatos → ERROR
    metadatoYoutubeRepository.save(nuevoMetadato);
}
```

**Problema:**
- La transacción hija heredaba el `readOnly = true` de la padre
- Cuando intentaba hacer `INSERT`, PostgreSQL rechazaba la operación

**Solución Implementada (v1.2):**

```java
// PlaylistService - Transacción PADRE (readOnly = true)
@Transactional(readOnly = true)
public List<CancionPlaylistDTO> buscarCancionesParaAgregar() {
    // Solo valida que la playlist exista (lectura)
    if (!listaRepository.existsById(listaId)) {
        throw new ResourceNotFoundException("Playlist", "id", listaId);
    }
    
    // Delega búsqueda a BuscadorService con NUEVA transacción
    buscadorService.buscarCanciones(consulta);
}

// BuscadorService - Transacción INDEPENDIENTE
@Transactional(propagation = Propagation.REQUIRES_NEW)
public List<ResultadoBusquedaDTO> buscarCanciones() {
    // Crea NUEVA transacción con permisos de escritura
    // Ahora SÍ puede guardar metadatos
    metadatoYoutubeRepository.save(nuevoMetadato);
}
```

**Por qué funciona ahora:**
1. `REQUIRES_NEW` suspende la transacción padre
2. Crea una nueva transacción independiente con permisos de escritura
3. Puede hacer INSERT/UPDATE sin restricciones
4. Al finalizar, retorna control a la transacción padre

**Beneficios adicionales:**
- **Aislamiento**: Si falla el guardado de metadatos, no afecta la validación de playlist
- **Performance**: La transacción de solo lectura es más rápida para consultas
- **Claridad**: Cada servicio maneja su propia transacción según sus necesidades

**Estado:** ✓ RESUELTO Y PROBADO EN PRODUCCIÓN

**Fecha de resolución:** 2025-11-20
**Versión:** 1.2

### Error 500 - Method Not Supported
**Problema**: `Request method 'GET' is not supported`
**Solución**: Verificar que el endpoint esté implementado correctamente en el backend

### Sin resultados
**Problema**: La búsqueda no retorna resultados
**Solución**: 
- Verificar que el término de búsqueda sea válido
- Revisar logs del backend para ver si hubo error en búsqueda de YouTube
- Verificar conexión con API de YouTube

### Error 404
**Problema**: Playlist no encontrada
**Solución**: Verificar que el `listaId` sea válido y la playlist exista

## Actualización: 2025-11-20

### Versión 1.2 - Solución Final de Transacciones (PRODUCCIÓN)

**Problema Resuelto Completamente:**
- Se identificó que el problema raíz era la propagación de transacciones anidadas
- Cuando `PlaylistService.buscarCancionesParaAgregar` llamaba a `BuscadorService.buscarCanciones`, 
  ambas compartían la misma transacción, causando conflictos de solo lectura

**Solución Implementada:**
1. **BuscadorService.buscarCanciones**: Ahora usa `@Transactional(propagation = REQUIRES_NEW)`
   - Crea una nueva transacción independiente para poder escribir metadatos
   - Puede guardar nuevos resultados de YouTube sin importar la transacción padre
   
2. **PlaylistService.buscarCancionesParaAgregar**: Mantiene `@Transactional(readOnly = true)`
   - Solo lee la playlist para verificar que existe
   - Delega todo el trabajo de escritura a BuscadorService

**Impacto en Frontend:**
- Ninguno - el contrato de la API permanece exactamente igual
- El endpoint ahora funciona perfectamente sin errores 500
- Las búsquedas en YouTube se guardan correctamente en la base de datos
- Rendimiento optimizado: las transacciones están correctamente aisladas

**Estado:** ✓ RESUELTO Y PROBADO

### Versión 1.1 - Corrección Parcial de Transacciones

**Problema Parcialmente Resuelto:**
- Se corrigió el `@Transactional(readOnly = true)` inicial pero persistían conflictos

**Cambio Técnico:**
- Primera iteración que cambió la anotación pero no resolvió la propagación

**Estado:** ⚠ DEPRECADO - usar versión 1.2

### Versión 1.0 - Implementación Inicial

Se implementó el endpoint completo que integra con el servicio de búsqueda existente, 
reutilizando la funcionalidad híbrida (BD local + YouTube).

**Estado:** ⚠ DEPRECADO - usar versión 1.2


