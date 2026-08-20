# Registro de Defectos

| ID | Fecha | Severidad | Módulo | Descripción | Pasos para Reproducir | Estado | Responsable |
|----|-------|-----------|--------|-------------|----------------------|--------|-------------|
| DEF-001 | 20/08/2026 | **Crítica** | Compilación | Error de compatibilidad JVM: Java 1.8 vs Kotlin 21 | 1. Clonar repo<br>2. `./gradlew assembleDebug` | **Cerrado** | Dev |
| DEF-002 | 20/08/2026 | **Mayor** | SolicitarScreen | `rememberSaveable` no resuelto, 13 errores de compilación | 1. Abrir SolicitarScreen.kt | **Cerrado** | Dev |
| DEF-003 | 20/08/2026 | **Menor** | UI | Contador de caracteres dice "/180" pero el límite es 200 | 1. Ir a SolicitarScreen<br>2. Ver supportingText | **Abierto** | Dev |

## Confirmación / Regresión

### DEF-001
- **Fix:** Configurar `compileOptions` a Java 17 + `compilerOptions.jvmTarget = JVM_17`
- **Verificación:** `./gradlew assembleDebug` → BUILD SUCCESSFUL
- **Evidencia:** Build log adjunto

### DEF-002
- **Fix:** Agregar `import androidx.compose.runtime.saveable.rememberSaveable`
- **Verificación:** Compilación sin errores, 0 issues en SolicitarScreen.kt
- **Evidencia:** Screenshot build successful