# Informe Técnico — Incrementos v0.2.0 a v0.6.0 (Semanas 5–9)

Informe técnico y funcional de los incrementos desarrollados según la guía de aprendizaje integradora PréstamoLab CTMA.

---

## 1. Resumen

La aplicación evolucionó de un prototipo con repositorio en memoria (v0.1.0) a un incremento cercano a condiciones reales (v0.6.0) con persistencia local, arquitectura reactiva, integración REST local-first, capacidades del dispositivo (cámara/Photo Picker, GPS y notificaciones) y controles de seguridad y calidad continua.

| Versión | Semana | Incremento |
|---------|--------|------------|
| v0.2.0 | 5 | Flujo heredado estabilizado: Catálogo → Detalle → Solicitud → Mis solicitudes |
| v0.3.0 | 6 | Persistencia local con Room + DataStore y Repository único |
| v0.4.0 | 7 | Asincronía con corrutinas, Flow/StateFlow y estados Loading/Content/Empty/Error |
| v0.5.0 | 8 | API REST con Retrofit/OkHttp, local-first y automatización de pruebas (MockWebServer) |
| v0.6.0 | 9 | Photo Picker, GPS, notificaciones, seguridad (Keystore/HTTPS) y CI |

---

## 2. Arquitectura

La UI Compose no accede directamente a Room ni Retrofit. El `ViewModel` interactúa con un `PrestamoRepository` (interfaz) cuyo punto único de acceso a datos es el `RoomPrestamoRepository`, que expone flujos Room/DataStore y coordina la sincronización remota.

```
UI (Compose, Material 3)
  ↓ collectAsStateWithLifecycle
ViewModel (PrestamoViewModel, UiState/StateFlow)
  ↓
PrestamoRepository (interfaz)
  ├── RoomPrestamoRepository (local-first, con withTransaction)
  │     ├── Room: EquipoDao / SolicitudDao / EquipoEntity / SolicitudEntity
  │     ├── DataStore: UserPreferencesStore (filtro de catálogo)
  │     └── Remote sync: PrestamoApiService (Retrofit/OkHttp)
  └── InMemoryPrestamoRepository (fake para pruebas y desarrollo)
```

* **Room como fuente local canónica**: el estado visible de la UI siempre refleja la base local; la red solo la actualiza (local-first). Las UI síncronas se abandonan y se trabaja 100 % con `Flow`/`StateFlow` en el `ViewModel`.
* **`withTransaction`**: las operaciones "crear solicitud + marcar equipo RESERVADO" y "devolución + equipo DISPONIBLE" son atómicas.
* **DataStore**: preferencias simples (filtro de categoría) como flujo expuesto por el Repository.

---

## 3. Semana 5 — Línea base heredada (v0.2.0)

* Se consolidó el flujo Catálogo → Detalle → Solicitud → Mis solicitudes sobre un repositorio simulado, conservando la trazabilidad previa.
* El proyecto reutiliza el Workflow de CI existente (`assembleDebug`, `testDebugUnitTest`, `lintDebug`).

## 4. Semana 6 — Persistencia local (v0.3.0)

* **Room** (2.6.1 + KSP): entidades `EquipoEntity` y `SolicitudEntity` con mapeos `toModel()`/`toEntity()`, DAOs para catálogo, solicitudes/devoluciones y semilla inicial de 6 equipos en el `onCreate` de la base.
* **Método de migración**: `fallbackToDestructiveMigration()` y arreglo `MIGRACIONES` vacío como punto de extensión para versiones futuras.
* **DataStore**: `UserPreferencesStore` guarda el filtro de categoría del catálogo (`prestamolab_preferencias`).
* `PrestamoDatabase.crear(context)` centraliza la construcción de la base (Ambiente: no `allowMainThreadQueries`).

## 5. Semana 7 — Asincronía y estado reactivo (v0.4.0)

* DAOs y Repository que devuelven `Flow`; el `ViewModel` recolecta con `collectAsStateWithLifecycle` en la UI.
* `PrestamoUiState` + `EstadoPagina` (CARGANDO / CONTENIDO / VACIO / ERROR) y `operacionEnCurso`/`sincronizando` para operaciones y sincronización.
* Manejo de errores recuperables con `ejecutar()` (no rompe el estado de la UI) y mensajes en Snackbar con `limpiarMensaje()`.
* El `ViewModel` se construye mediante `PrestamoViewModelFactory` con `repository`, `ubicacionProvider` y `notificador`.

## 6. Semana 8 — API REST y automatización (v0.5.0)

* Contrato de endpoints (REST/JSON):
  * `GET /equipos`, `GET /equipos/{id}`
  * `GET /solicitudes`, `POST /equipos/{equipoId}/solicitudes`
  * `POST /solicitudes/{id}/devolucion`
* **DTO ↔ dominio ↔ Entity** con `Dtos.kt`, `Mappers.kt` y `SolicitudEntity.toDto()`.
* `NetworkModule`: OkHttp con **HTTPS obligatorio** (`usesCleartextTraffic=false`), timeouts de 10 s, header `Authorization: Bearer` con `TokenStore` y `BuildConfig.API_BASE_URL` por ambiente.
* **Local-first**: la red descarga/actualiza la base; los errores de red y códigos HTTP (401/404/5xx) se traducen a estados y mensajes de error en la UI.
* **MockWebServer** (dependencia de prueba) disponible para el test automatizado del cliente HTTP (la capa de pruebas la completa el equipo).

## 7. Semana 9 — Capacidades del dispositivo, seguridad y CI (v0.6.0)

### 7.1 Evidencia fotográfica (Photo Picker)
* Selección con `ActivityResultContracts.PickVisualMedia` (sin permiso de cámara y sin permisos de almacenamiento → mínimo privilegio).
* URI persistida en `SolicitudEntity.evidenciaUri` y exportada a la galería mediante `FileProvider` (`file_paths.xml`).
* Estados de evidencia `LOCAL → SUBIENDO → SINCRONIZADA/FALLIDA` gestionados en el Repository durante la devolución/sincronización.

### 7.2 Capacidad física adicional: GPS (geolocalización)
* **Propósito**: registrar en la solicitud el lugar donde se entrega el equipo de laboratorio (permite auditoría de préstamo).
* **API utilizada**: `LocationManager` (GPS/NETWORK_PROVIDER) mediante el fun-interface `UbicacionProvider` / `GpsUbicacionProvider` (con proveedor `NoDisponible` para emuladores sin GPS).
* **Permisos**: `ACCESS_FINE_LOCATION` y `ACCESS_COARSE_LOCATION`, solicitados en tiempo de ejecución al pulsar "Usar ubicación actual" (mínimo privilegio; `requestLocationUpdates` no requiere actualización continua).
* **Privacidad**: solo se muestran coordenadas en el detalle; no se registran datos personales ni se comparte ubicación con terceros.
* **Errores**: sin permiso → `SecurityException` con mensaje; sin proveedor GPS → `IllegalStateException`; ambos se muestran en Snackbar.

### 7.3 Notificaciones
* `AlarmRecordatorio` (AlarmManager inexacto) programa un recordatorio de devolución; `RecordatorioReceiver` muestra la notificación (`Notificaciones`).
* Permiso `POST_NOTIFICATIONS` solicitado en tiempo de ejecución.
* `NotificadorRecordatorio.NoOp` permite pruebas y emuladores sin el servicio.

### 7.4 Seguridad
* **Token cifrado**: `TokenStore` genera una clave AES/GCM en Android Keystore y guarda el token cifrado en `SharedPreferences` (`prestamolab_seguro`). Conceptualmente reemplaza un token de sesión sin exponer secretos.
* **HTTPS exclusivo** y URLs por ambiente vía `buildConfigField` (no se versionan secretos).
* Mínimo privilegio: solo permisos imprescindibles y solicitados según necesidad.

### 7.5 Ambientes dev/stage/prod
Sin product flavors (para no romper las tareas de CI `assembleDebug`/`testDebugUnitTest`), la separación se hace por `buildType`:

| BuildType | API_BASE_URL |
|-----------|--------------|
| debug | http://10.0.2.2:8080/api/v1/ (emulador → host local) |
| release | https://api.prestamolab.example.com/api/v1/ (placeholder producido) |

## 8. Calidad continua y CI

* `.github/workflows/android-ci.yml` ejecuta `assembleDebug`, `testDebugUnitTest` y `lintDebug`, publicando artefactos (`app-debug.apk`).
* La estrategia de pruebas prevista: unitarias (JUnit + coroutines-test), integración del cliente HTTP (MockWebServer), instrumentadas Room (in-memory) y un recorrido crítico de UI (Compose UI Test) sobre Catálogo → Detalle.
* **Gates mínimos del PR**: build ✓, unit tests, lint.

## 9. Limitaciones y riesgos residuales

* **Sin backend real**: la sincronización depende de un servicio/mock; sin red, el catálogo se mantiene por la semilla local de Room.
* **GPS con última ubicación conocida** (`getLastKnownLocation`): en interiores puede devolver coordenadas desactualizadas; se prevé mejora a localización activa si el caso de uso lo exige.
* **AlarmManager inexacto**: los recordatorios pueden retrasarse unos minutos por políticas del sistema Android.
* **Dependencias**: KSP/Room/Kotlin (2.0.21-1.0.28 / 2.6.1 / 2.0.21) fijadas y verificadas en la compilación.
* **Migraciones Room** pendientes de definir (`MIGRACIONES` vacío con `fallbackToDestructiveMigration`).

## 10. Uso de IA

La herramienta de IA de desarrollo (opencode) asistió en la implementación de la capa de datos (Room/DataStore), la integración REST, las capacidades del dispositivo y la documentación técnica, siguiendo las decisiones de arquitectura acordadas (local-first, mínimo privilegio, HTTPS). Todo el código fue revisado, compilado y es modificable por el equipo; no se reportan PASS/FAIL inventados.