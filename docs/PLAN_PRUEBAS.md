# Plan de Pruebas — PréstamoLab CTMA

Suite completa. Los casos con automatización (✅) están cubiertos por `PrestamoViewModelTest.kt` y `FakePrestamoRepositoryTest.kt` — 35 pruebas pasando en CI (GitHub Actions).

| ID    | HU / RN         | Técnica         | Precondición                          | Pasos                                              | Esperado                                                        | Automatizado |
|-------|-----------------|-----------------|----------------------------------------|-----------------------------------------------------|-------------------------------------------------------------------|:---:|
| TC-01 | Catálogo        | Caso de uso     | Base de datos inicializada             | Abrir catálogo                                     | Se listan los 6 equipos con su estado actual                     | ✅ |
| TC-02 | RN-01           | Negativa        | Equipo en RESERVADO/PRESTADO           | Intentar solicitarlo                               | Solicitud rechazada: "El equipo no está disponible."             | ✅ |
| TC-03 | Solicitar       | Caso de uso     | Equipo DISPONIBLE, datos válidos       | Completar formulario y enviar                      | Solicitud creada, equipo pasa a PRESTADO                          | ✅ |
| TC-04 | Equipo inexistente | Negativa    | ID de equipo no existe                 | Solicitar equipo con id inválido                   | Rechazado: "El equipo solicitado no existe."                      | ✅ |
| TC-05 | RN-02 (destino) | Equivalencia    | Formulario abierto                     | Dejar "Ambiente/destino" vacío o solo espacios      | Rechazado: "El ambiente o destino es obligatorio."                | ✅ |
| TC-06 | RN-03 (propósito) | Límites       | Formulario abierto                     | Propósito de 5 y de 200 caracteres                 | Rechazado: "El propósito debe tener entre 10 y 180 caracteres."   | ✅ |
| TC-07 | RN-04 (duración) | Límites        | Formulario abierto                     | Duración 0 y duración 9                            | Rechazado: "La duración debe estar entre 1 y 8 horas."            | ✅ |
| TC-08 | RN-05 (doble clic) | Error guessing | Formulario válido                    | Pulsar "Enviar Solicitud" dos veces rápido          | Solo se crea una solicitud                                        | ✅ |
| TC-09 | Cancelar        | Caso de uso     | Existe una solicitud SOLICITADA        | Cancelar la solicitud                              | Estado pasa a CANCELADA, equipo vuelve a DISPONIBLE               | ✅ |
| TC-10 | Cancelar inexistente | Negativa   | ID de solicitud no existe              | Cancelar con id inválido                           | Rechazado: "La solicitud no existe."                              | ✅ |
| TC-11 | Devolver        | Caso de uso     | Existe una solicitud activa            | Devolver el equipo con foto de evidencia           | Estado DEVUELTA, equipo vuelve a DISPONIBLE, foto guardada        | ✅ |
| TC-12 | Persistencia    | Caso de uso     | App con datos guardados                | Cerrar la app por completo (Force Stop) y reabrir  | Los equipos y solicitudes siguen exactamente igual                | Manual |
| TC-13 | Cámara          | Caso de uso     | Formulario de solicitud abierto        | Capturar foto con CameraCaptureSection             | La URI de la foto queda asociada a la solicitud                  | Manual |
| TC-14 | Autenticación   | Caso de uso     | Formulario válido, listo para enviar   | Pulsar "Enviar Solicitud"                          | Se pide huella o PIN; solo si se autentica se crea la solicitud   | Manual |
| TC-15 | Autenticación cancelada | Negativa | Prompt biométrico visible           | Cancelar el diálogo de huella/PIN                  | La solicitud NO se crea, se muestra mensaje de error              | Manual |

## Trazabilidad rápida

```
HU Solicitar préstamo
│
├── CA: destino obligatorio        → TC-05
├── CA: propósito 10–180           → TC-06
├── CA: duración 1–8               → TC-07
├── CA: no duplicar solicitud      → TC-08
├── CA: requiere autenticación     → TC-14, TC-15
│
├── Riesgos: R-01, R-02, R-03, R-04, R-07
└── Casos: TC-02 a TC-08, TC-14, TC-15
```