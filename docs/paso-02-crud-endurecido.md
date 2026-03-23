# Paso 2 - Endurecimiento del CRUD

Fecha: 23-03-2026

## Objetivo del paso
Fortalecer el CRUD básico para evitar inconsistencias y preparar el proyecto para el motor de procesos y planificación del disco.

## Cambios implementados

### 1) Validación de creación en el gestor (lógica de negocio)
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se agregaron métodos para centralizar reglas en backend (no solo en la UI):
- `crearArchivo(...)`
- `crearDirectorio(...)`
- `renombrarElemento(...)`

Validaciones agregadas:
- Nombre no nulo / no vacío.
- Directorio padre válido.
- Tamaño de archivo mayor a 0.
- No permitir nombres duplicados dentro del mismo directorio (comparación case-insensitive).
- Verificación de bloques libres antes de crear archivo.

### 2) Renombrado de elementos (Update del CRUD)
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se implementó `renombrarElemento(...)` con reglas:
- No renombrar la raíz.
- No aceptar nombre vacío.
- No permitir colisión de nombre en el mismo directorio padre.

### 3) UI conectada al gestor (validaciones reales)
Archivo: `src/vista/VentanaPrincipal.java`

Se reemplazó la creación directa desde vista por métodos del gestor:
- Crear archivo ahora usa `gestor.crearArchivo(...)`.
- Crear directorio ahora usa `gestor.crearDirectorio(...)`.

También se agregó:
- Botón `Renombrar`.
- Acción `accionRenombrarElemento()` para actualizar nombre desde la interfaz.
- Mensajes de error consistentes cuando falla una validación.

## Resultado esperado visible en ejecución
- No se pueden crear elementos con nombre vacío.
- No se pueden crear dos elementos con el mismo nombre en el mismo directorio.
- Si no hay espacio suficiente, la creación de archivo se rechaza con mensaje claro.
- Se puede renombrar archivo/directorio desde la UI.
- No se puede renombrar el directorio raíz.

## Requisitos del enunciado impactados
- CRUD (Create/Update) más robusto.
- Validaciones de tipo/rango en interfaz.
- Base preparada para siguientes pasos (procesos, planificador, concurrencia).

## Criterio de cierre del Paso 2
Paso 2 se considera completado porque:
1. El `Create` quedó validado por backend.
2. Se implementó `Update` (renombrado) con restricciones.
3. Se eliminaron puntos de creación sin validación en la interfaz.
