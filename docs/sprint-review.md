# Sprint Review y Retrospective

## Sprint Review — Sprint 1
**Fecha:** 20/08/2026  
**Asistentes:** Dev, QA, Product Owner

| Qué se mostró | Feedback del PO | Próximos pasos |
|--------------|-----------------|----------------|
| Flujo completo de solicitud de préstamo | "El formulario es claro, pero falta indicar campos obligatorios con asterisco rojo" | Implementar indicadores visuales de obligatoriedad |
| Lista de equipos con imágenes | "Las imágenes tardan en cargar, considerar placeholders" | Agregar shimmer/placeholder |
| Estado de carga y errores | "Buen manejo de estados, agregar snackbar para errores" | Migrar mensajes de error a Snackbar |

---

## Retrospective

| ¿Qué funcionó? | ¿Qué mejorar? | Acción concreta |
|---------------|---------------|----------------|
| MVVM + Compose facilitó el desarrollo | Gradle 9.0-milestone-1 causó problemas de compatibilidad | **Acción:** Downgrade a Gradle 8.10.2 estable al inicio del Sprint 2 |
| rememberSaveable salvó el estado en rotación | Falta de pruebas automatizadas desde el día 1 | **Acción:** Configurar CI/CD con GitHub Actions para ejecutar tests en cada PR |
| Comunicación fluida Dev-QA | Documentación técnica desactualizada | **Acción:** Actualizar README automáticamente como criterio de Done |