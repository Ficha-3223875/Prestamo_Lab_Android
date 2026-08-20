# Suite de Casos de Prueba y Datos Sintéticos

## Casos de Prueba

| ID | Escenario | Precondición | Pasos | Datos de Entrada | Resultado Esperado |
|----|-----------|-------------|-------|-----------------|-------------------|
| TC-001 | Listar equipos | App instalada | 1. Abrir app | N/A | Se muestra lista de equipos |
| TC-002 | Solicitud exitosa | Equipo seleccionado | 1. Tap en equipo<br>2. Llenar formulario<br>3. Tap Guardar | Destino: "Lab 3"<br>Propósito: "Práctica redes"<br>Horas: "4" | Mensaje de éxito, estado guardando = false |
| TC-003 | Validar campos vacíos | En SolicitarScreen | 1. Dejar campos vacíos<br>2. Tap Guardar | Destino: ""<br>Propósito: ""<br>Horas: "" | Botón deshabilitado o mensaje de error |
| TC-004 | Límite de caracteres propósito | En SolicitarScreen | 1. Ingresar 201 caracteres | Texto de 201 chars | Solo acepta 200, contador se detiene |
| TC-005 | Horas solo numéricas | En SolicitarScreen | 1. Ingresar "abc" | "abc" | Campo filtra solo dígitos |
| TC-006 | Estado de carga | Enviando solicitud | 1. Tap Guardar con datos válidos | Datos válidos | Botón muestra "Guardando..." y se deshabilita |

## Datos Sintéticos (JSON)

```json
[
  {
    "id": "EQ-001",
    "nombre": "Osciloscopio Digital",
    "descripcion": "Osciloscopio 100MHz, 2 canales",
    "disponible": true,
    "ubicacion": "Lab Electrónica"
  },
  {
    "id": "EQ-002",
    "nombre": "Multímetro Fluke 87V",
    "descripcion": "Multímetro industrial True RMS",
    "disponible": true,
    "ubicacion": "Lab Instrumentación"
  },
  {
    "id": "EQ-003",
    "nombre": "Fuente de Poder DC",
    "descripcion": "0-30V, 0-5A triple salida",
    "disponible": false,
    "ubicacion": "Lab Electrónica"
  }
]