# 🔬 PréstamoLab CTMA

### Aplicación móvil Android para la gestión de préstamos de equipos de laboratorio

---

## 🏷️ Badges

![API](https://img.shields.io/badge/API-Android%2024%2B-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple?logo=kotlin)
![Version](https://img.shields.io/badge/Version-0.6.0-blue)
![Licencia](https://img.shields.io/badge/Licencia-Académica-blue)
![CI](https://img.shields.io/badge/CI-GitHub%20Actions-success)

---

## 📱 Descripción

**PréstamoLab CTMA** es una aplicación móvil Android desarrollada con Kotlin y Jetpack Compose para gestionar el catálogo de equipos de laboratorio y las solicitudes de préstamo, incluyendo el registro de devoluciones con evidencia fotográfica.

El proyecto sigue una arquitectura local-first: **Room** es la fuente canónica local, **DataStore** guarda las preferencias del usuario y **Retrofit/OkHttp** sincronizan con el servicio remoto. Incluye capacidades del dispositivo (Photo Picker, GPS y notificaciones), seguridad (HTTPS y token cifrado en Android Keystore) e integración continua con GitHub Actions.

La aplicación es parte de un proyecto académico para practicar desarrollo móvil Android, Scrum, prueba de software y arquitectura.

---

## ✨ Funcionalidades principales

* 📦 Visualización del catálogo de equipos con **filtro por categoría** persistido en DataStore.
* 🔍 Detalle de equipo e información de disponibilidad.
* 📝 Creación de solicitudes de préstamo (con validaciones y captura opcional de ubicación GPS).
* 📋 Consulta, cancelación y **registro de devolución con evidencia fotográfica** (Photo Picker).
* ☁️ Sincronización local-first con el servicio remoto (Retrofit/OkHttp + MockWebServer en pruebas).
* 🧭 Navegación con barra inferior (Equipos / Solicitudes).
* 💾 Persistencia local con Room (catálogo y solicitudes sobreviven al reinicio).
* 📱 Capacidades del dispositivo: **GPS**, **Photo Picker** y **notificaciones** de recordatorio.
* 🔒 Seguridad: HTTPS obligatorio, token cifrado en Android Keystore y permisos de mínimo privilegio.
* 🧠 Estados reactivos Loading / Content / Empty / Error con ViewModel y StateFlow.
* 🤖 CI con GitHub Actions (build, unit tests, lint).

---

## 🛠️ Tecnologías utilizadas

| Tecnología | Uso en el proyecto |
| ---------- | ------------------ |
| **Kotlin 2.0 / Corrutinas / Flow** | Lenguaje y programación asíncrona reactiva. |
| **Jetpack Compose + Material 3** | Interfaz de usuario declarativa. |
| **Navigation Compose** | Navegación entre pantallas. |
| **ViewModel + StateFlow** | Lógica de presentación y estado observable. |
| **Room 2.6.1 (KSP)** | Persistencia local (catálogo y solicitudes). |
| **DataStore Preferences** | Preferencias del usuario (filtro de categoría). |
| **Retrofit 2.11 + OkHttp 4.12** | Consumo de la API REST. |
| **Android Keystore (AES/GCM)** | Cifrado del token de sesión. |
| **JUnit 4 + coroutines-test + MockWebServer** | Pruebas unitarias e integración HTTP. |
| **GitHub Actions** | Integración continua. |

---

## 🧩 Modelo de datos

### Equipo
| Campo | Tipo | Descripción |
| ----- | ---- | ----------- |
| `id` | `Long` | Identificador del equipo. |
| `nombre` | `String` | Nombre del equipo. |
| `categoria` | `CategoriaEquipo` | COMPUTO, AUDIOVISUAL, REDES, ELECTRONICA u otra. |
| `estado` | `EstadoEquipo` | DISPONIBLE, RESERVADO o PRESTADO. |

### Solicitud de préstamo
| Campo | Tipo | Descripción |
| ----- | ---- | ----------- |
| `id` | `Long` | Identificador de la solicitud. |
| `equipoId` | `Long` | Equipo solicitado. |
| `ambienteDestino` / `proposito` | `String` | Destino y propósito del préstamo. |
| `duracionHoras` | `Int` | Duración solicitada (1–8 h). |
| `estado` | `EstadoSolicitud` | SOLICITADA, ENTREGADA, DEVUELTA o CANCELADA. |
| `fechaSolicitud` / `fechaLimiteDevolucion` | `Long` | Fechas (epoch millis). |
| `evidenciaUri` | `String?` | URI de la evidencia fotográfica. |
| `evidenciaEstado` | `EstadoEvidencia` | LOCAL, SUBIENDO, SINCRONIZADA o FALLIDA. |
| `latitud` / `longitud` | `Double?` | Ubicación GPS registrada al solicitar. |
| `sincronizado` | `Boolean` | Indicador de sincronización remota. |

---

## 📋 Reglas de negocio

1. El campo **destino** es obligatorio y no puede estar vacío.
2. El campo **propósito** debe contener entre **10 y 180 caracteres**.
3. La duración del préstamo debe estar entre **1 y 8 horas**, inclusive.
4. Solo un equipo **DISPONIBLE** puede solicitarse; la solicitud lo marca **RESERVADO**.
5. Un equipo ya solicitado/repetido no puede tener dos préstamos activos.
6. Solo una solicitud **ENTREGADA** puede registrar devolución (con o sin evidencia).
7. La devolución marca la solicitud **DEVUELTA** y el equipo **DISPONIBLE**.
8. La evidencia pasa por estados **LOCAL → SUBIENDO → SINCRONIZADA/FALLIDA**.
9. Los errores de red/HTTP no rompen el estado de la UI y se muestran como mensajes recuperables.

---

## 📁 Estructura del proyecto

```text
PrestamoLab-CTMA/
│
├── .github/workflows/
│   └── android-ci.yml              # CI: assembleDebug, testDebugUnitTest, lintDebug
├── app/src/
│   ├── main/java/com/example/prestamolab/
│   │   ├── MainActivity.kt
│   │   ├── PrestamoLabApplication.kt
│   │   ├── RecordatorioReceiver.kt
│   │   ├── di/AppContainer.kt
│   │   ├── model/                  # Equipo, SolicitudPrestamo, Estados
│   │   ├── data/
│   │   │   ├── local/              # Entities, DAOs, PrestamoDatabase, UserPreferencesStore
│   │   │   ├── remote/             # Dtos, Mappers, PrestamoApiService, NetworkModule
│   │   │   ├── repository/         # PrestamoRepository (+Room / InMemory)
│   │   │   └── security/TokenStore.kt
│   │   ├── ui/                     # PrestamoLabApp, screens/, theme/
│   │   ├── util/                   # UbicacionProvider, NotificadorRecordatorio
│   │   └── viewmodel/              # PrestamoViewModel (+Factory)
│   └── test/java/com/example/prestamolab/
│       └── ValidacionesTest.kt     # Pruebas de validaciones
└── docs/                           # Matrices, informes, backlogs, suites de pruebas
```

---

## ⚙️ Requisitos previos

* Android Studio (con SDK compatible, `compileSdk 35`).
* JDK 21 compatible con Gradle 8.9 y AGP 8.7.
* Dispositivo/emulador Android (API 24+), con GPS activo para probar la ubicación.
* Internet para descargar dependencias en la primera compilación.

---

## 🚀 Cómo compilar y ejecutar

```powershell
cd PrestamoLab-CTMA
.\gradlew.bat assembleDebug
```

Ejecutar desde Android Studio con el botón **Run ▶**. El APK de depuración queda en `app/build/outputs/apk/debug/`.

---

## 🔌 API REST y ambientes

La URL base se configura por ambiente mediante `BuildConfig.API_BASE_URL` (sin exponer secretos):

| BuildType | URL |
|-----------|-----|
| `debug` | `http://10.0.2.2:8080/api/v1/` (emulador → host local) |
| `release` | `https://api.prestamolab.example.com/api/v1/` (placeholder) |

La sincronización manual (botón en el catálogo) actualiza Room desde el servicio. Sin servicio disponible, la aplicación funciona con la semilla local (local-first). Más detalle en `docs/informe-tecnico.md`.

---

## 🧪 Pruebas

La estrategia prevista (los tests los ejecuta el equipo del proyecto):

* **Unitarias**: `.\gradlew.bat testDebugUnitTest`
* **Prueba concreta**: `.\gradlew.bat test --tests "*ValidacionesTest"`
* **Instrumentadas (dispositivo/emulador)**: `.\gradlew.bat connectedAndroidTest` (Room in-memory + recorrido UI Compose).

Reportes HTML: `app/build/reports/tests/testDebugUnitTest/index.html`.

---

## 🤖 Integración continua

`.github/workflows/android-ci.yml` ejecuta en cada push/PR:

1. `assembleDebug`
2. `testDebugUnitTest`
3. `lintDebug`

y publica el artefacto `app-debug.apk`.

---

## 📚 Documentación

La carpeta `docs/` contiene la documentación del proyecto:

* `matriz-trazabilidad.md` — trazabilidad requisitos → diseño → código → verificación.
* `informe-tecnico.md` — informe técnico de los incrementos v0.2.0 a v0.6.0.
* `product-backlog.md`, `sprint-backlog.md`, `sprint-review.md` — gestión de sprints.
* `matriz-riesgos.md`, `registro-defectos.md`, `suite-pruebas.md`, `bitacora-pruebas.md` — riesgos y pruebas.

---

## 📝 Notas

* La información se **persiste localmente** con Room/DataStore.
* La aplicación **sincroniza con una API REST** (Retrofit/OkHttp) de forma **local-first**.
* Solo se usa **HTTPS** en producción; token cifrado en Android Keystore.
* Permisos de **mínimo privilegio** solicitados en tiempo de ejecución (ubicación y notificaciones).
* Los datos son **sintéticos**, con fines académicos y de prueba.
* **Uso de IA**: la herramienta de desarrollo (opencode) asistió en la implementación de la capa de datos, la integración REST, las capacidades del dispositivo y la documentación; todo el código fue compilado, revisado y es modificable por el equipo.

---



## 🎤 13. Preguntas para sustentación

### 40. Abra una HU y muestre un criterio de aceptación; siga la trazabilidad hasta el código y la prueba que lo valida.

Una Historia de Usuario puede relacionarse con la creación de una solicitud de préstamo.

**Criterio de aceptación:**
El usuario debe poder registrar una solicitud únicamente cuando el destino sea válido, el propósito tenga entre 10 y 180 caracteres y la duración esté entre 1 y 8 horas.

**Trazabilidad:**

```text
Historia de Usuario
        ↓
Criterio de aceptación
        ↓
Regla de negocio
        ↓
Validación en el código
        ↓
ViewModel / Repository
        ↓
Prueba unitaria
```

En el proyecto estas reglas están documentadas como reglas de negocio y las pruebas unitarias cubren las validaciones, el repositorio y el ViewModel.

---

### 41. Explique por qué Room se considera fuente local canónica en su solución.

**En el incremento actual no se utiliza Room.**

La solución actual utiliza `InMemoryRepository` como almacenamiento temporal durante la ejecución de la aplicación. Los datos se pierden al reiniciar la aplicación y el README indica que actualmente no existe una base de datos.

Por lo tanto, para este incremento, la fuente local de información es el repositorio en memoria.

Si posteriormente se incorpora Room, este podría utilizarse como fuente local persistente y única de verdad para los datos almacenados en el dispositivo.

---

### 42. ¿Qué diferencia existe entre Flow y StateFlow en el contexto del ViewModel?

`Flow` representa un flujo de datos que puede emitir valores a lo largo del tiempo.

`StateFlow` es un tipo de `Flow` diseñado para representar **estado**, ya que mantiene un valor actual y permite que la interfaz observe los cambios.

En el proyecto, el ViewModel utiliza `StateFlow` para exponer el estado de la aplicación hacia la interfaz.

Por ejemplo:

```kotlin
private val _uiState = MutableStateFlow(...)
val uiState: StateFlow<...> = _uiState.asStateFlow()
```

De esta manera, el ViewModel modifica el estado y la interfaz reacciona a los cambios.

---

### 43. Muestre un caso de error de red y explique cómo se representa en UiState.

**El proyecto actual no utiliza una API REST ni operaciones de red**, por lo que no tiene un error de red implementado.

En la arquitectura actual, los errores corresponden principalmente a validaciones y operaciones inválidas.

Por ejemplo:

```text
Usuario deja vacío el destino
        ↓
Validación
        ↓
Error
        ↓
UiState
        ↓
La interfaz muestra el mensaje
```

Si en una versión futura se incorpora una API, el `UiState` podría representar estados como:

```kotlin
data class UiState(
    val cargando: Boolean = false,
    val error: String? = null
)
```

Un error de red podría establecer:

```kotlin
error = "No se pudo conectar con el servidor"
```

---

### 44. Seleccione un test automatizado y explique Arrange, Act y Assert.

Un ejemplo puede ser una prueba de validación del propósito.

**Arrange:** preparar los datos de prueba.

```kotlin
val proposito = "Préstamo"
```

**Act:** ejecutar la función que valida el propósito.

```kotlin
val resultado = validarProposito(proposito)
```

**Assert:** comprobar que el resultado sea el esperado.

```kotlin
assertFalse(resultado)
```

La estructura es:

```text
Arrange → preparar
Act     → ejecutar
Assert  → comprobar
```

Esto permite que cada prueba tenga un objetivo claro y fácil de entender.

---

### 45. ¿Qué parte del incremento fue desarrollada mediante TDD y qué aprendieron?

La parte que puede relacionarse con TDD es la construcción de las **reglas de validación de las solicitudes**.

El proceso consiste en:

```text
1. Definir el comportamiento esperado
        ↓
2. Crear la prueba
        ↓
3. Implementar la validación
        ↓
4. Ejecutar la prueba
        ↓
5. Corregir y refactorizar
```

Esto permitió comprobar las reglas antes de considerar terminada la funcionalidad.

El principal aprendizaje fue que las pruebas ayudan a detectar errores temprano y permiten modificar el código con mayor seguridad.

---

### 46. Muestre un defecto encontrado, su confirmación y la regresión seleccionada.

Un ejemplo de defecto es permitir registrar una solicitud con un **propósito menor a 10 caracteres**, incumpliendo la regla de negocio.

**Defecto encontrado:**

```text
Propósito: "Préstamo"
```

El texto tiene menos de 10 caracteres.

**Confirmación:**

Se ejecuta la validación y se comprueba que la solicitud no debe ser aceptada.

**Corrección:**

Se aplica la regla:

```text
10 ≤ longitud del propósito ≤ 180
```

**Prueba de regresión:**

Después de corregir el defecto se vuelven a ejecutar las pruebas de validación para comprobar que:

* Un propósito menor de 10 caracteres sea rechazado.
* Un propósito válido sea aceptado.
* Un propósito mayor de 180 caracteres sea rechazado.

---

### 47. ¿Qué permiso del dispositivo solicitaron y por qué cumple mínimo privilegio?

**En el incremento documentado no se especifica ningún permiso del dispositivo solicitado.**

El README únicamente establece que la aplicación puede ejecutarse en un dispositivo físico con depuración USB habilitada para desarrollo y pruebas.

Por lo tanto, no se debe afirmar que se solicitó un permiso específico si este no está realmente implementado en el `AndroidManifest.xml`.

Si posteriormente se requiere un permiso, debe solicitarse únicamente el necesario para la funcionalidad correspondiente, siguiendo el principio de **mínimo privilegio**.

---

### 48. ¿Qué quality gates utiliza su Pull Request?

En el README actual **no hay un flujo de integración continua (CI) configurado**.

Por esta razón, no sería correcto afirmar que existe un quality gate automático en GitHub Actions.

Como controles actuales se pueden considerar:

* Compilación del proyecto.
* Ejecución de las pruebas unitarias.
* Revisión de los cambios antes del merge.
* Verificación de que las funcionalidades cumplan las reglas de negocio.

Las pruebas pueden ejecutarse mediante:

```powershell
.\gradlew.bat test
```

Y la aplicación puede compilarse mediante:

```powershell
.\gradlew.bat assembleDebug
```

Estas verificaciones permiten comprobar que los cambios no rompan las funcionalidades existentes.

---

### 49. ¿Qué riesgo residual permanece en el incremento actual?

El principal riesgo residual es que la información se almacena únicamente en memoria.

Actualmente:

```text
InMemoryRepository
        ↓
Datos temporales
        ↓
Reinicio de la aplicación
        ↓
Los datos se pierden
```

El README confirma que el proyecto no utiliza una base de datos y que la información puede perderse al reiniciar la aplicación.

Por lo tanto, un riesgo pendiente es la **falta de persistencia permanente de los préstamos y solicitudes**.

Otros riesgos que pueden considerarse son:

* No existe una API REST.
* No existe sincronización con un servidor.
* No existe integración continua configurada.
* La información actualmente está limitada al almacenamiento en memoria.

Estos riesgos pueden abordarse en futuros incrementos mediante persistencia local, por ejemplo Room, una API REST y un flujo de CI.

<p align="center">
  <strong>PréstamoLab CTMA</strong><br>
  Proyecto académico de desarrollo de aplicaciones móviles Android.
</p>
