# 🔬 PréstamoLab CTMA

### Aplicación móvil Android para la gestión de préstamos de equipos de laboratorio

---

## 🏷️ Badges

![API](https://img.shields.io/badge/API-Android%2026%2B-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-2.x-purple?logo=kotlin)
![Licencia](https://img.shields.io/badge/Licencia-Académica-blue)
![Build](https://img.shields.io/badge/Build-Sin%20CI%20configurado-lightgrey)

> **Nota:** Los badges de versión de API, Kotlin y licencia son referenciales y pueden ajustarse a la configuración real del proyecto. El proyecto no cuenta con un flujo de integración continua configurado.

---

## 📱 Descripción

**PréstamoLab CTMA** es una aplicación móvil Android desarrollada con Kotlin y Jetpack Compose que permite gestionar el catálogo de equipos de laboratorio y las solicitudes de préstamo. La aplicación ofrece una interfaz moderna basada en Material 3, navegación mediante una barra inferior y una arquitectura que utiliza ViewModel, StateFlow y un repositorio en memoria para gestionar la información durante la ejecución.

El proyecto tiene un enfoque académico y está orientado a la práctica de desarrollo de aplicaciones móviles, gestión de estados, navegación y pruebas unitarias.

---

## ✨ Funcionalidades principales

* 📦 Visualización del catálogo de equipos de laboratorio.
* 📝 Creación de solicitudes de préstamo.
* 📋 Consulta de las solicitudes realizadas en la sección **Mis solicitudes**.
* ❌ Cancelación de solicitudes de préstamo.
* 🧭 Navegación mediante una barra inferior con dos pestañas:

  * **Equipos:** catálogo de equipos disponibles.
  * **Solicitudes:** consulta de mis solicitudes.
* ✅ Validación de los datos de las solicitudes.
* ⚠️ Gestión de mensajes de error y confirmación mediante el estado de la interfaz.
* 🧠 Gestión del estado de la aplicación con ViewModel y StateFlow.
* 🧪 Pruebas unitarias de las validaciones, el repositorio y el ViewModel.

---

## 🛠️ Tecnologías utilizadas

<p align="left">
  <img src="https://img.shields.io/badge/Kotlin-2.x-purple?logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Material%203-Design-757575?logo=materialdesign&logoColor=white" alt="Material 3" />
  <img src="https://img.shields.io/badge/Navigation%20Compose-Navigation-3DDC84?logo=android&logoColor=white" alt="Navigation Compose" />
  <img src="https://img.shields.io/badge/ViewModel%20%2B%20StateFlow-State%20Management-FF6F00?logo=kotlin&logoColor=white" alt="ViewModel y StateFlow" />
  <img src="https://img.shields.io/badge/InMemoryRepository-Data%20Layer-607D8B" alt="InMemoryRepository" />
  <img src="https://img.shields.io/badge/JUnit%204-Unit%20Testing-25A162?logo=junit5&logoColor=white" alt="JUnit 4" />
</p>

| Tecnología             | Uso en el proyecto                                           |
| ---------------------- | ------------------------------------------------------------ |
| **Kotlin**             | Lenguaje principal de desarrollo.                            |
| **Jetpack Compose**    | Construcción de la interfaz de usuario declarativa.          |
| **Material 3**         | Componentes visuales y diseño de la aplicación.              |
| **Navigation Compose** | Navegación entre las pantallas de Equipos y Solicitudes.     |
| **ViewModel**          | Gestión de la lógica y el estado de la interfaz.             |
| **StateFlow**          | Exposición reactiva del estado de la aplicación.             |
| **InMemoryRepository** | Almacenamiento temporal de equipos y solicitudes en memoria. |
| **JUnit 4**            | Ejecución de pruebas unitarias.                              |

---

## 🧩 Modelo de datos

El modelo representa la información necesaria para gestionar los préstamos de equipos. Los nombres y tipos exactos deben coincidir con las clases implementadas en el proyecto.

| Campo           | Tipo     | Descripción                                                           |
| --------------- | -------- | --------------------------------------------------------------------- |
| `id`            | `String` | Identificador del equipo o de la solicitud, según el modelo.          |
| `nombre`        | `String` | Nombre o título del elemento representado.                            |
| `destino`       | `String` | Destino indicado en la solicitud de préstamo.                         |
| `proposito`     | `String` | Propósito o motivo del préstamo.                                      |
| `duracionHoras` | `Int`    | Duración solicitada, expresada en horas.                              |
| `estado`        | `String` | Estado de la solicitud, según los estados definidos en la aplicación. |

> **Importante:** Esta tabla documenta los campos funcionales conocidos del proyecto. Si los modelos Kotlin utilizan nombres diferentes, tipos específicos o campos adicionales, deben reflejarse en esta sección para mantener la documentación sincronizada con el código.

---

## 📋 Reglas de negocio

1. El campo **destino** es obligatorio y no puede estar vacío.
2. El campo **propósito** debe contener entre **10 y 180 caracteres**.
3. La duración del préstamo debe estar entre **1 y 8 horas**, incluyendo ambos límites.
4. Una solicitud no debe guardarse dos veces durante una misma operación de guardado.
5. No se deben realizar operaciones sobre equipos o solicitudes cuyos identificadores no existan.
6. El repositorio debe gestionar los flujos de creación y cancelación de solicitudes.
7. El ViewModel debe mostrar mensajes de error cuando una operación no sea válida.
8. Los mensajes de estado deben poder limpiarse después de mostrarse.
9. El estado inicial de la interfaz debe representar correctamente la información disponible al iniciar la aplicación.

---

## 📸 Capturas de pantalla

<!--
Añadir aquí las capturas reales de la aplicación.

Ejemplo de organización:

![Catálogo de equipos](docs/screenshots/equipos.png)
![Mis solicitudes](docs/screenshots/solicitudes.png)
![Formulario de préstamo](docs/screenshots/formulario.png)

No se incluyen imágenes hasta disponer de las capturas reales del proyecto.
-->

---

## 📁 Estructura del proyecto

La siguiente estructura es una guía de organización basada en los componentes conocidos del proyecto. Debe ajustarse a las carpetas y archivos que existan realmente en el repositorio.

```text
PrestamoLab-CTMA/
│
├── app/
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   └── java/
│       │       └── <paquete-del-proyecto>/
│       │           ├── MainActivity.kt
│       │           ├── data/
│       │           │   └── InMemoryPrestamoRepository.kt
│       │           ├── model/
│       │           │   └── <modelos-del-proyecto>.kt
│       │           ├── ui/
│       │           │   ├── screens/
│       │           │   │   ├── <pantalla-equipos>.kt
│       │           │   │   ├── <pantalla-solicitudes>.kt
│       │           │   │   └── <pantalla-formulario>.kt
│       │           │   ├── navigation/
│       │           │   │   └── <navegacion>.kt
│       │           │   └── theme/
│       │           │       └── <archivos-de-tema>.kt
│       │           └── viewmodel/
│       │               └── PrestamoViewModel.kt
│       │
│       └── test/
│           └── java/
│               └── <paquete-del-proyecto>/
│                   ├── ValidacionesTest.kt
│                   ├── InMemoryPrestamoRepositoryTest.kt
│                   └── PrestamoViewModelTest.kt
│
├── docs/
│   ├── <documentacion-del-proyecto>
│   └── <otros-documentos>
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

> Los elementos entre `< >` son marcadores de posición. Reemplázalos por los nombres reales de los paquetes, archivos y carpetas del repositorio. No representan archivos confirmados del proyecto.

---

## ⚙️ Requisitos previos

Para ejecutar PréstamoLab CTMA se necesita:

* Android Studio instalado.
* JDK compatible con la versión de Gradle y Android Gradle Plugin del proyecto.
* Android SDK configurado.
* Un emulador Android o un dispositivo físico con depuración USB habilitada.
* Gradle Wrapper incluido en el repositorio (`gradlew` y `gradlew.bat`).
* Conexión a internet para descargar las dependencias durante la primera compilación.

> La versión exacta de Android, Kotlin, Gradle y el SDK debe verificarse en los archivos de configuración del proyecto.

---

## 🚀 Cómo compilar y ejecutar la aplicación

1. Clonar el repositorio:

   ```powershell
   git clone <URL_DEL_REPOSITORIO>
   ```

2. Entrar en la carpeta del proyecto:

   ```powershell
   cd PrestamoLab-CTMA
   ```

3. Abrir el proyecto en Android Studio.

4. Esperar a que Gradle sincronice las dependencias.

5. Conectar un dispositivo Android o iniciar un emulador.

6. Ejecutar la aplicación desde Android Studio utilizando el botón **Run ▶**.

7. También es posible compilar el proyecto desde PowerShell:

   ```powershell
   .\gradlew.bat assembleDebug
   ```

---

## 🧪 Cómo ejecutar las pruebas unitarias

Desde PowerShell, ubicado en la raíz del proyecto, ejecuta:

### Ejecutar todas las pruebas unitarias

```powershell
.\gradlew.bat test
```

### Ejecutar las pruebas de validaciones

```powershell
.\gradlew.bat test --tests "*ValidacionesTest"
```

### Ejecutar las pruebas del repositorio en memoria

```powershell
.\gradlew.bat test --tests "*InMemoryPrestamoRepositoryTest"
```

### Ejecutar las pruebas del ViewModel

```powershell
.\gradlew.bat test --tests "*PrestamoViewModelTest"
```

### Ejecutar una prueba específica

```powershell
.\gradlew.bat test --tests "*ValidacionesTest.nombreDelTest"
```

> Reemplaza `nombreDelTest` por el nombre real del método de prueba.

---

## 📊 Cómo consultar los reportes de pruebas

Después de ejecutar las pruebas, el reporte HTML se encuentra normalmente en:

```text
app/build/reports/tests/testDebugUnitTest/index.html
```

Para abrirlo desde PowerShell:

```powershell
Start-Process ".\app\build\reports\tests\testDebugUnitTest\index.html"
```

> La ruta puede variar según la configuración de Gradle y la variante de compilación utilizada.

---

## 📚 Documentación

La carpeta `docs/` está destinada a contener la documentación complementaria del proyecto.

Entre los documentos que pueden formar parte de esta carpeta se encuentran:

* Documentación técnica de la aplicación.
* Guías o instrucciones de uso.
* Documentos académicos del proyecto.
* Evidencias y material de apoyo.
* Capturas de pantalla, si se incorporan al repositorio.

> Esta lista es orientativa. Solo deben incluirse en el README los documentos y archivos que existan realmente dentro de `docs/`.

---

## 📝 Notas

* Los datos utilizados por la aplicación son **sintéticos** y se emplean únicamente con fines académicos y de prueba.
* El proyecto utiliza un repositorio **en memoria (`InMemoryRepository`)**.
* La información no se persiste de forma permanente y puede perderse al reiniciar la aplicación.
* El proyecto **no utiliza una base de datos**.
* El proyecto **no cuenta con una API REST**.
* El proyecto **no tiene un flujo de integración continua (CI)** configurado.
* Las pruebas unitarias se ejecutan localmente mediante JUnit 4.
* La aplicación está orientada al aprendizaje de Kotlin, Jetpack Compose, navegación, gestión de estados y pruebas unitarias.

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
