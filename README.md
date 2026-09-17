# PréstamoLab CTMA

Aplicación Android para la gestión de préstamos de equipos de laboratorio, desarrollada como proyecto integrador del programa ADSO (SENA) — combina Scrum, desarrollo Android y pruebas de software.

## Descripción

Permite a los aprendices consultar el catálogo de equipos disponibles, solicitar un préstamo indicando destino/propósito/duración, hacer seguimiento a sus solicitudes y cancelarlas mientras estén en estado "Solicitada".

## Arquitectura

com.example.prestamolab/
├── model/              → Equipo, SolicitudPrestamo, enumeraciones (Estados.kt)
├── data/repository/     → Validaciones.kt (RN-02 a RN-04), PrestamoRepository (interfaz),
│                          InMemoryPrestamoRepository (implementación con reglas de negocio)
├── viewmodel/           → PrestamoViewModel (StateFlow), PrestamoViewModelFactory
├── ui/
│   ├── screens/         → CatalogoScreen, EquipoDetalleScreen, SolicitarScreen,
│   │                      MisSolicitudesScreen, SolicitudDetalleScreen, IconosUi
│   ├── theme/           → Tema Material 3
│   └── PrestamoLabApp.kt → NavHost y navegación (bottom bar)
└── MainActivity.kt

Patrón: MVVM con Repository como fuente única de verdad, StateFlow para exponer el estado a Compose, y Result<Unit> para propagar éxito/fallo con mensajes descriptivos.

## Reglas de negocio implementadas

| Regla | Descripción |
|---|---|
| RN-01 | Solo se puede solicitar un equipo en estado DISPONIBLE |
| RN-02 | El ambiente/destino es obligatorio |
| RN-03 | El propósito debe tener entre 10 y 180 caracteres |
| RN-04 | La duración debe estar entre 1 y 8 horas |
| RN-05 | No se permite doble solicitud (doble pulsación o mismo equipo activo) |
| RN-06 | Al crear una solicitud, el equipo pasa a RESERVADO |
| RN-07 | Solo se puede cancelar una solicitud en estado SOLICITADA; al cancelar, el equipo vuelve a DISPONIBLE |
| RN-08 | IDs inexistentes no provocan cierre abrupto de la app (se maneja con null/ErrorState) |

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

Desde Android Studio: clic derecho sobre com.example.prestamolab (test) → Run 'Tests in ...'

O por línea de comandos:
./gradlew testDebugUnitTest

Suite actual: 25 pruebas automatizadas (12 en ValidacionesTest, 13 en InMemoryPrestamoRepositoryTest), 100% en PASS.

## Alcance del MVP

Incluido: catálogo, detalle de equipo, solicitud de préstamo, mis solicitudes, detalle de solicitud, cancelación.

Fuera de alcance (backlog futuro): aprobación/rechazo de solicitudes por parte de instructores, notificaciones de cambio de estado.

## Autor

Steven — Aprendiz ADSO, SENA (Ficha 3223875)
