# Matriz de Riesgos

| ID | Riesgo | Probabilidad | Impacto | Estrategia | Mitigación |
|----|--------|-------------|---------|------------|------------|
| R01 | Incompatibilidad de versiones Gradle/Kotlin/AGP | Alta | Alto | Mitigar | Fijar versiones en `gradle/libs.versions.toml` |
| R02 | Falta de tiempo para integrar backend real | Alta | Medio | Aceptar | Usar repositorio mock para MVP |
| R03 | Cambios de configuración destruyen estado del formulario | Media | Medio | Mitigar | Usar `rememberSaveable` |
| R04 | Dispositivos con Android < API 24 no soportados | Baja | Bajo | Aceptar | `minSdk = 24` |
| R05 | Rendimiento lento en listas grandes de equipos | Media | Medio | Mitigar | Implementar lazy loading |