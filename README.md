# PréstamoLab CTMA

Aplicación Android para la gestión de préstamos de equipos de laboratorio, desarrollada como proyecto integrador del programa ADSO (SENA) — combina Scrum, desarrollo Android y pruebas de software.

## Descripción

Permite a los aprendices consultar el catálogo de equipos disponibles, solicitar un préstamo indicando destino/propósito/duración (con autenticación biométrica/PIN y evidencia fotográfica), hacer seguimiento a sus solicitudes y cancelarlas o devolver el equipo mientras estén en un estado válido.

## Arquitectura
com.example.prestamolab/
├── model/ → Equipo, SolicitudPrestamo, enumeraciones (Estados.kt)
├── data/
│ ├── local/ → Room: EquipoEntity, SolicitudEntity, EquipoDao, SolicitudDao,
│ │ PrestamoDatabase, Converters (LocalDateTime)
│ └── repository/ → Validaciones.kt (RN-02 a RN-04), PrestamoRepository (interfaz),
│ RoomPrestamoRepository (implementación real, con Flow + suspend)
├── viewmodel/ → PrestamoViewModel (StateFlow derivado de Flow del repositorio),
│ PrestamoViewModelFactory
├── ui/
│ ├── screens/ → CatalogoScreen, EquipoDetalleScreen, SolicitarScreen,
│ │ MisSolicitudesScreen, SolicitudDetalleScreen, IconosUi
│ ├── components/ → CameraCaptureSection (evidencia fotográfica),
│ │ BiometricAuthHelper (huella/PIN)
│ ├── theme/ → Tema Material 3
│ └── PrestamoLabApp.kt → NavHost y navegación (bottom bar)
└── MainActivity.kt → FragmentActivity (requerido por BiometricPrompt)


Patrón: MVVM con Repository como fuente única de verdad, Room como persistencia local, Flow/StateFlow para exponer el estado a Compose de forma reactiva, corrutinas para toda operación de E/S, y `Result<Unit>` para propagar éxito/fallo con mensajes descriptivos.

## Reglas de negocio implementadas

| Regla | Descripción |
|---|---|
| RN-01 | Solo se puede solicitar un equipo en estado DISPONIBLE |
| RN-02 | El ambiente/destino es obligatorio |
| RN-03 | El propósito debe tener entre 10 y 180 caracteres |
| RN-04 | La duración debe estar entre 1 y 8 horas |
| RN-05 | No se permite doble solicitud (doble pulsación o mismo equipo activo) |
| RN-06 | Al crear una solicitud, el equipo pasa a PRESTADO (ver riesgo residual en la última sección) |
| RN-07 | Solo se puede cancelar/devolver una solicitud existente; al hacerlo, el equipo vuelve a DISPONIBLE |
| RN-08 | IDs inexistentes no provocan cierre abrupto de la app (se maneja con `Result.failure` y mensaje) |

## Requisitos

- Android Studio (con AGP 8.7.3)
- JDK 21
- Kotlin 2.0.21

## Cómo ejecutar
git clone https://github.com/Ficha-3223875/Prestamo_Lab_Android.git
cd Prestamo_Lab_Android
git checkout steven-prestamolab

Abrir la carpeta con Android Studio (File → Open) y esperar el sync de Gradle. Verificar que el Gradle JDK esté configurado en JDK 21 (Settings → Build Tools → Gradle).

## Cómo correr las pruebas

Desde Android Studio: clic derecho sobre `com.example.prestamolab` (test) → Run 'Tests in ...'

O por línea de comandos (el mismo comando que usa el CI):
./gradlew testDebugUnitTest

Suite actual: 36 pruebas automatizadas (12 en `ValidacionesTest`, 11 en `FakePrestamoRepositoryTest`, 13 en `PrestamoViewModelTest`), 100% en PASS.

## Integración continua (CI)

`.github/workflows/android-ci.yml` corre en cada push/PR: `assembleDebug` (compila), `testDebugUnitTest` (pruebas unitarias) y `lintDebug` (análisis estático). Publica como artefactos el reporte de pruebas, el reporte de Lint y el APK debug generado.

## Alcance del MVP

Incluido: catálogo, detalle de equipo, solicitud de préstamo (con cámara y autenticación biométrica/PIN), mis solicitudes, detalle de solicitud, cancelación, devolución, persistencia local con Room.

Fuera de alcance (backlog futuro): aprobación/rechazo de solicitudes por parte de instructores, notificaciones de cambio de estado, backend remoto.

## Trazabilidad HU → CA → código → prueba (ejemplo)

**HU-03 — Solicitar préstamo**

- **CA-03.1**: "El sistema debe rechazar la solicitud si el equipo no está DISPONIBLE."
- **Código que lo implementa**: `RoomPrestamoRepository.crearSolicitud()`, validación `if (equipoEntity.estado != EstadoEquipo.DISPONIBLE.name) return ... Result.failure(...)`.
- **Prueba que lo valida**: `FakePrestamoRepositoryTest.crearSolicitud_sobre_equipo_no_disponible_falla` (y su equivalente a nivel de ViewModel, `PrestamoViewModelTest.crearSolicitud_sobre_equipo_no_disponible_muestra_error`).

## Preguntas de sustentación

**1. ¿Por qué Room se considera la fuente local canónica en esta solución?**
Porque ninguna otra capa guarda su propia copia de la verdad: la UI y el ViewModel nunca mantienen listas propias de equipos o solicitudes, solo observan lo que Room expone a través de `Flow` en los DAO. Toda escritura (crear, cancelar, devolver) se hace directamente contra Room; el `StateFlow` del ViewModel es solo una proyección reactiva de esos datos, no una copia independiente. Por eso, aunque se cierre el proceso de la app, los datos sobreviven — están en el archivo SQLite, no en memoria.

**2. ¿Qué diferencia existe entre Flow y StateFlow en el contexto del ViewModel?**
`Flow` es frío: no guarda un valor actual, y cada vez que alguien lo colecta se vuelve a ejecutar la fuente (en este caso, la consulta de Room). `StateFlow` es caliente: siempre tiene un valor disponible (`.value`), y todos los que lo colecten reciben el mismo último valor sin recalcular nada. En `PrestamoViewModel`, los `Flow` que vienen de Room (vía `observarEquipos()`/`observarSolicitudes()`) se combinan y se convierten en `StateFlow` con `stateIn(...)`, para que la UI de Compose pueda leer `uiState.value` de forma síncrona y varias pantallas compartan el mismo estado sin volver a consultar la base de datos cada vez.

**3. Muestre un caso de error (de red o local) y explique cómo se representa en UiState.**
Esta app no consume ninguna API de red — toda la persistencia es local con Room — así que no hay un caso de error de red real. El equivalente local es el mismo patrón que usaría un error de red: cuando `crearSolicitud()` falla (por ejemplo, "El equipo no existe." o "El equipo no está disponible para préstamo."), el repositorio devuelve `Result.failure(...)`, y el ViewModel lo transforma así: `_mensaje.value = resultado.fold(onSuccess = {...}, onFailure = { it.message ?: "..." })`. Ese mensaje viaja dentro de `PrestamoUiState.mensaje` y la UI lo muestra como texto de error. Si mañana se agregara una API remota, el mismo mecanismo aplicaría: la excepción de red caería en el `onFailure` y llenaría `mensaje` de la misma forma.

**4. Seleccione un test automatizado y explique Arrange, Act y Assert.**
`FakePrestamoRepositoryTest.cancelarSolicitud_exitosa_cancela_y_libera_equipo`:
- **Arrange**: se crea una solicitud sobre el equipo 3 (que arranca DISPONIBLE en el repositorio de prueba).
- **Act**: se llama a `repo.cancelarSolicitud(1)`.
- **Assert**: se verifica que el resultado sea éxito, que la solicitud quede en estado `CANCELADA`, y que el equipo 3 vuelva a `DISPONIBLE`.

**5. ¿Qué parte del incremento fue desarrollada mediante TDD y qué aprendieron?**
La mayoría de las funcionalidades se desarrollaron con pruebas escritas después del código (test-after), no TDD estricto. Donde sí se siguió un ciclo Red-Green real fue al corregir `PrestamoViewModelTest`: la suite completa falló (12 de 35 en rojo) al ejecutarla con `./gradlew testDebugUnitTest`, se diagnosticó que `Dispatchers.setMain(...)` y `runTest { }` estaban usando relojes de prueba distintos y desincronizados, se corrigió compartiendo un mismo `TestDispatcher`, y se volvió a correr hasta quedar en verde. Aprendizaje concreto: que un test pase en el runner de Android Studio no garantiza que pase con el comando real de Gradle/CI si depende de temporización de corrutinas — desde entonces se valida siempre con `./gradlew testDebugUnitTest` antes de dar algo por confirmado.

**6. Muestre un defecto encontrado, su confirmación y la regresión seleccionada.**
Defecto: al crear una segunda solicitud, la app cerraba con `FATAL EXCEPTION: java.lang.IllegalArgumentException: Key "0" was already used` (visible en Logcat). Confirmación: se rastreó el stack trace hasta `InMemoryPrestamoRepository.crearSolicitud()`, donde la solicitud siempre se guardaba con `id = 0` en vez de generar un id nuevo. Corrección: un contador `siguienteSolicitudId` que se incrementa en cada creación. Prueba de regresión: `FakePrestamoRepositoryTest.crearSolicitud_multiples_veces_asigna_ids_unicos`, que crea varias solicitudes seguidas y confirma que ningún id se repite.

**7. ¿Qué permiso del dispositivo solicitaron y por qué cumple mínimo privilegio?**
`android.permission.CAMERA`, solicitado en tiempo de ejecución (no al instalar) y solo en el momento exacto en que el usuario pulsa "tomar foto" en `CameraCaptureSection`. Cumple mínimo privilegio porque: (1) es el único permiso pedido — no se solicita acceso a la galería completa ni a almacenamiento externo, ya que la foto se guarda con `FileProvider` en un directorio privado de la app; (2) se pide justo antes de usarse, no al abrir la app; y (3) el `<uses-feature android:required="false">` en el manifiesto permite que la app funcione igual en un dispositivo sin cámara.

**8. ¿Qué quality gates utiliza su Pull Request?**
El workflow `Android CI` (GitHub Actions) corre en cada push y PR contra `main`, con tres gates secuenciales: `assembleDebug` (el proyecto debe compilar), `testDebugUnitTest` (las 36 pruebas unitarias deben pasar) y `lintDebug` (Android Lint no debe romper el build). Si cualquiera falla, el PR queda marcado en rojo. Además se publican como artefactos el reporte de pruebas, el reporte de Lint y el APK generado, para revisión manual.

**9. ¿Qué riesgo residual permanece en el incremento actual?**
No existe un estado intermedio real entre "solicitado" y "en manos del aprendiz": al crear una solicitud, el equipo pasa directamente a `PRESTADO` sin un paso de aprobación o entrega por parte de un instructor. Esto significa que cualquier aprendiz autenticado puede dejar un equipo como prestado sin que nadie más confirme la entrega física — un control de custodia real requeriría un rol adicional (instructor) y un estado `RESERVADO` intermedio, que quedó fuera del alcance del MVP actual (ver R-07 en `docs/RIESGOS.md`).