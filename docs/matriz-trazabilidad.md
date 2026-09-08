# Matriz de Trazabilidad

| Requisito | Historia | Diseño | Código | Prueba | Estado |
|-----------|----------|--------|--------|--------|--------|
| REQ-001: Ver catálogo | P01 | HomeScreen.kt | `EquipoList()` | TC-001 | Pass |
| REQ-002: Solicitar préstamo | P02 | SolicitarScreen.kt | `onGuardar()` | TC-002 | Pass |
| REQ-003: Validar campos | P02 | SolicitarScreen.kt | Validaciones | TC-003 | Pass |
| REQ-004: Estado de carga | P02 | PrestamoUiState | `state.guardando` | TC-004 | Pass |
| REQ-005: Navegación | P01/P02 | NavHost | `NavController` | TC-005 | Pass |