# Registro de Riesgos — PréstamoLab CTMA

| ID   | Riesgo                                                              | Probabilidad | Impacto | Nivel | Tratamiento                                                        |
|------|----------------------------------------------------------------------|-------------:|--------:|-------|---------------------------------------------------------------------|
| R-01 | Doble pulsación en "Enviar Solicitud" crea dos solicitudes duplicadas | Media         | Alta    | Alto  | Validación RN-05 en el repositorio (bloqueo por solicitud activa)   |
| R-02 | Solicitar un equipo que ya está RESERVADO/PRESTADO                   | Alta          | Media   | Alto  | Validación de disponibilidad antes de crear la solicitud (RN-01)   |
| R-03 | Propósito demasiado corto o vacío no explica el uso del equipo       | Media         | Baja    | Medio | Validación de longitud 10–180 caracteres (RN-03)                   |
| R-04 | Duración de préstamo fuera de rango operativo (0 horas o excesiva)   | Media         | Media   | Medio | Validación de rango 1–8 horas (RN-04)                               |
| R-05 | Pérdida de datos al cerrar o reiniciar la app                       | Alta          | Alta    | Alto  | Persistencia con Room (SQLite), no en memoria                       |
| R-06 | Bloqueo de la interfaz por operaciones de base de datos lentas      | Media         | Media   | Medio | Corrutinas + Dispatchers.IO para toda escritura/lectura en Room     |
| R-07 | Nadie puede verificar quién solicitó realmente un equipo             | Media         | Alta    | Alto  | Autenticación biométrica/PIN obligatoria al confirmar la solicitud  |
| R-08 | Cancelación o devolución sobre una solicitud inexistente             | Baja          | Baja    | Bajo  | Validación de existencia antes de cambiar estado (mensaje de error) |
| R-09 | Evidencia fotográfica faltante o corrupta al devolver un equipo     | Baja          | Media   | Medio | Campo `fotoDevolucionUri` opcional, capturado con CameraX/Photo Picker |

## Riesgos por historia de usuario

**HU — Solicitar préstamo**
- R-01, R-02, R-03, R-04, R-07

**HU — Mis solicitudes / Cancelar solicitud**
- R-08

**HU — Devolver equipo**
- R-08, R-09

**HU — Catálogo de equipos**
- R-05, R-06