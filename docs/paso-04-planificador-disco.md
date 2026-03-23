# Paso 4 - Planificador de disco (FIFO, SSTF, SCAN, C-SCAN)

Fecha: 23-03-2026

## Objetivo del paso
Implementar la selección de solicitudes por política de planificación de disco, con posición inicial configurable del cabezal y cálculo de desplazamiento acumulado.

## Cambios implementados

### 1) Políticas de planificación
Archivos:
- `src/modelo/PoliticaPlanificacion.java`
- `src/modelo/GestorSistemaArchivos.java`

Se agregaron las políticas:
- FIFO
- SSTF
- SCAN
- C-SCAN

### 2) Estado del cabezal y métricas
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se agregaron campos y API para:
- Política activa.
- Posición actual del cabezal.
- Dirección de barrido (ascendente/descendente).
- Desplazamiento acumulado del cabezal.
- Configuración inicial de planificador (`configurarPlanificador`).

### 3) Selección del siguiente proceso por política
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se reemplazó el despacho FIFO fijo por una selección según política:
- `seleccionarSiguienteProcesoSegunPolitica()`
- Helpers de selección para SSTF / SCAN / C-SCAN.
- Movimiento del cabezal antes de ejecutar cada proceso (`moverCabezalHasta`).

### 4) Posición solicitada por proceso
Archivo: `src/modelo/Proceso.java`

Se agregó `posicionSolicitudDisco` para que cada proceso tenga una referencia en disco usada por el planificador.

### 5) UI para configurar y visualizar planificador
Archivo: `src/vista/VentanaPrincipal.java`

Se agregaron controles:
- Selector de política.
- Cabezal inicial.
- Dirección (checkbox).
- Botón para aplicar configuración.
- Etiquetas de posición actual y desplazamiento acumulado.

También se actualiza el estado visible del planificador tras operaciones CRUD.

## Resultado funcional
- El sistema permite elegir entre FIFO, SSTF, SCAN y C-SCAN.
- Se puede definir ubicación inicial del cabezal de forma arbitraria.
- Se muestra desplazamiento acumulado y posición actual del cabezal en tiempo real.
- El despacho de solicitudes ya no es estrictamente FIFO; depende de la política activa.

## Criterio de cierre del Paso 4
Paso 4 se considera completado porque:
1. Existen 4 políticas funcionales de planificación.
2. El cabezal tiene posición inicial configurable.
3. Se visualizan posición y desplazamiento del cabezal durante la simulación.
