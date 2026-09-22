# Matriz de Trazabilidad

Trazabilidad de la línea base v0.1.0 y los incrementos v0.2.0 a v0.6.0 de la guía PréstamoLab CTMA.

| Requisito / HU | Descripción (criterios clave) | Riesgo | Diseño | Código | Verificación | Estado |
|----------------|-------------------------------|--------|--------|--------|--------------|--------|
| REQ-001 | Ver catálogo de equipos | R01 | CatalogoScreen | `EquipoDao.obtenerTodos()` + `CatalogoScreen()` | TC-001 | Implementado |
| REQ-002 | Solicitar préstamo de un equipo disponible | R03 | SolicitarScreen | `onCrearSolicitud` → `crearSolicitud()` | TC-002 | Implementado |
| REQ-003 | Validar campos (destino obligatorio, propósito 10–180, duración 1–8 h) | R05 | Validaciones | `Validaciones.kt` | TC-003 | Implementado |
| REQ-004 | Confirmación/error de guardado | R02 | UiState | `operacionEnCurso`, `mensaje` | TC-004 | Implementado |
| REQ-005 | Navegación Catálogo → Detalle → Solicitud → Mis solicitudes | R03 | NavHost | `PrestamoLabApp.kt` | TC-005 | Implementado |
| REQ-006 | Ver detalle del equipo | R01 | EquipoDetalleScreen | `EquipoDetalleScreen()` | TC-006 | Implementado |
| REQ-007 | Consultar y cancelar solicitudes de préstamo | R02 | MisSolicitudesScreen | `cancelarSolicitud()` (libera equipo) | TC-007 | Implementado |
| REQ-008 | Persistir catálogo y solicitudes en Room (fuente canónica local) | R01 | data/local | `EquipoEntity`, `SolicitudEntity`, `EquipoDao`, `SolicitudDao`, `PrestamoDatabase` | Prueba DAO/reinicio app | Implementado (S6, v0.3.0) |
| REQ-009 | Preferencias de usuario con DataStore (filtro de catálogo) | R01 | data/local | `UserPreferencesStore` (`filtro_categoria`) | Prueba DataStore | Implementado (S6, v0.3.0) |
| REQ-010 | Repository como único punto de acceso a flujos Room/DataStore/red | R01 | data/repository | `PrestamoRepository` + `RoomPrestamoRepository` | Prueba Repository | Implementado (S7, v0.4.0) |
| REQ-011 | Estados reactivos Loading / Content / Empty / Error / Operation | R02 | viewmodel | `EstadoPagina`, `PrestamoUiState`, `collectAsStateWithLifecycle` | TC asíncrono | Implementado (S7, v0.4.0) |
| REQ-012 | Integración REST con Retrofit/OkHttp y mapeo DTO ↔ dominio | R02 | data/remote | `PrestamoApiService`, `NetworkModule`, `Dtos.kt`, `Mappers.kt` | MockWebServer | Implementado (S8, v0.5.0) |
| REQ-013 | Estrategia local-first: Room visible, red sincroniza | R02 | room + remote | `sincronizarCatalogo()`, `sincronizarSolicitudes()` | Prueba Repository/red | Implementado (S8, v0.5.0) |
| REQ-014 | Errores de red/HTTP recuperables en UiState | R05 | datos | `timeouts 10s`, `ejecutar()`, mensajes de error | TC error de red | Implementado (S8, v0.5.0) |
| REQ-015 | Ambientes dev/stage/prod sin exponer secretos | R01 | BuildConfig | `buildConfigField API_BASE_URL` (debug/release), HTTPS exclusivo | Revisión build | Implementado (S8–9, v0.5.0–0.6.0) |
| REQ-016 | Evidencia fotográfica con Photo Picker y FileProvider | R03 | ui | `PickVisualMedia`, `SolicitudEvidenciaUri`, `file_paths.xml` | Prueba manual flujo | Implementado (S9, v0.6.0) |
| REQ-017 | Estados de evidencia Local / Subiendo / Sincronizada / Fallida | R02 | model + repository | `EstadoEvidencia`, update en `registrarDevolucion`/`sincronizarSolicitudes` | Prueba Repository | Implementado (S9, v0.6.0) |
| REQ-018 | Capacidad física adicional: GPS (ubicación de la solicitud) | R04 | util + ui | `UbicacionProvider`/`GpsUbicacionProvider`, `ACCESS_FINE/COARSE_LOCATION` + permiso runtime, lat/lng en SolicitudEntity | Prueba en dispositivo | Implementado (S9, v0.6.0) |
| REQ-019 | Notificaciones de recordatorio con permiso runtime | R04 | util + receiver | `NotificadorRecordatorio`, `RecordatorioReceiver`, `POST_NOTIFICATIONS` | Prueba manual | Implementado (S9, v0.6.0) |
| REQ-020 | Seguridad: token cifrado en Android Keystore, mínimo privilegio, sin secretos en repo | R01 | data/security | `TokenStore` (AES/GCM), `buildConfigField`, `.gitignore` | Revisión de seguridad | Implementado (S9, v0.6.0) |
| REQ-021 | CI con GitHub Actions: build, unit tests y lint | R01 | workflow | `.github/workflows/android-ci.yml` | Ver ejecución en PR | Implementado (S9, v0.6.0) |

> **Nota:** Las pruebas (unitarias, de integración y UI) las ejecuta el equipo del proyecto; los resultados PASS/FAIL se registran en `docs/bitacora-pruebas.md` y en los PR correspondientes, sin reportar resultados inventados.