# PréstamoLab CTMA

Aplicación Android educativa basada en la guía integradora de Scrum + Android + Pruebas de Software.

## Funcionalidades

- Catálogo de equipos.
- Detalle mediante `equipoId`.
- Solicitud de préstamo.
- Validación de destino, propósito y duración.
- Prevención de solicitudes sobre equipos no disponibles.
- Protección contra doble guardado.
- Mis solicitudes.
- Detalle de solicitud.
- Cancelación de solicitudes `SOLICITADA`.
- Actualización de disponibilidad.
- Manejo de IDs inexistentes.
- Repository en memoria compartido durante la ejecución.

## Tecnologías

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- ViewModel + StateFlow
- Repository + InMemoryRepository
- JUnit

## Cómo abrir

1. Descomprime el ZIP.
2. Abre la carpeta `PrestamoLabCTMA` en Android Studio.
3. Espera a que Gradle sincronice.
4. Ejecuta en un emulador o dispositivo Android.
5. La aplicación inicia en el catálogo.

## Nota

Los datos son sintéticos y se mantienen únicamente durante la ejecución, tal como plantea el incremento inicial de la guía.
