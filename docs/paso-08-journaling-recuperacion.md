# Paso 8: Journaling y recuperación ante fallos

## Objetivo
Implementar un mecanismo básico de journaling para operaciones críticas y permitir recuperación consistente después de una falla simulada.

## Cambios implementados

### 1) Journal transaccional
- Se agregó un journal en `GestorSistemaArchivos` para registrar transacciones con estado:
  - `PENDIENTE`
  - `CONFIRMADA`
  - `ABORTADA`
- Cada operación crítica crea una entrada de journal con metadatos de proceso, operación y objetivo.

### 2) Simulación de fallo antes de commit
- Se agregó un modo de simulación de fallo (`simularFalloAntesCommit`) para dejar transacciones en estado `PENDIENTE` antes de confirmar.
- Esto permite probar el flujo de recuperación de forma controlada.

### 3) Recuperación (replay/undo básico)
- Se agregó `ejecutarRecuperacionJournalPendientes()` para procesar entradas pendientes:
  - Si la operación pendiente fue creación incompleta, se revierte.
  - Si la operación pendiente fue eliminación incompleta, se restaura desde snapshot.
- Se devuelve la cantidad de entradas recuperadas.

### 4) Persistencia del journal en JSON
- El journal ahora se serializa dentro del estado JSON.
- Al cargar JSON se restaura el journal y se recalibra el contador de transacciones.

### 5) Interfaz (UI)
- Se reorganizaron controles superiores en 2 filas para mejorar visibilidad.
- Se añadieron controles de journaling:
  - Botón para activar/desactivar simulación de fallo.
  - Botón para ejecutar recuperación manual del journal.
  - Etiqueta de política activa del planificador.
- Se actualiza el estado visual del planificador y simulación en tiempo real.

## Resultado
- Se corrige el problema de visibilidad de política en la interfaz.
- El sistema puede simular fallas en commit y recuperarse de transacciones pendientes.
- El estado de journaling queda persistido entre ejecuciones.

## Validación
- Compilación del proyecto con Ant: `BUILD SUCCESSFUL`.
