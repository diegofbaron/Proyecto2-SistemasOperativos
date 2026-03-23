# Paso 3 - Motor de procesos (cola y despachador)

Fecha: 23-03-2026

## Objetivo del paso
Separar el registro de solicitudes de su ejecución real, modelando mejor la cola de E/S y el ciclo de estados de procesos.

## Cambios implementados

### 1) Separación explícita entre solicitar y ejecutar
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se mantuvo la solicitud con encolado:
- `solicitarCreacionArchivo(...)`

Y se agregaron métodos del despachador:
- `despacharSiguienteProceso()`
- `despacharTodosLosProcesosPendientes()`
- `obtenerCantidadProcesosPendientes()`

### 2) Ciclo de estados de proceso
Archivo: `src/modelo/GestorSistemaArchivos.java`

Flujo aplicado en creación:
- `NUEVO` (constructor de `Proceso`)
- `LISTO` (al encolar)
- `EJECUTANDO` (al despachar)
- `TERMINADO` o `BLOQUEADO` (resultado de ejecución)

### 3) Historial de procesos ejecutados/registrados
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se agregó:
- `Lista<Proceso> historialProcesos`
- `obtenerHistorialProcesos()`

Con esto queda trazabilidad de solicitudes para mostrar en UI en pasos siguientes.

### 4) Metadatos de proceso para trazabilidad
Archivo: `src/modelo/Proceso.java`

Se agregaron campos:
- `detalleOperacion`
- `mensajeResultado`

Y getters/setter para exponer estado y resultado del despacho.

## Impacto funcional
- El sistema ya no depende de ejecutar directamente dentro de la solicitud.
- Existe un núcleo de despacho preparado para integrar planificador (FIFO/SSTF/SCAN/C-SCAN) en el paso siguiente.
- La creación actual desde UI sigue funcionando, pero internamente respeta el flujo solicitar → despachar.

## Requisitos del enunciado impactados
- Cola de procesos con estados.
- Base para gestión de solicitudes de E/S por política.

## Criterio de cierre del Paso 3
Paso 3 se considera completado porque:
1. Solicitud y ejecución quedaron desacopladas.
2. Se implementó el despachador de procesos.
3. Se formalizó el ciclo de estados y trazabilidad de resultado.
