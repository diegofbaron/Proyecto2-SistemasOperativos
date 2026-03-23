# Paso 5 - Concurrencia por archivo (locks)

Fecha: 23-03-2026

## Objetivo del paso
Implementar control de concurrencia en archivos compartidos con lock compartido (lectura), lock exclusivo (escritura) y cola de espera por recurso.

## Cambios implementados

### 1) Estado de locks en archivo
Archivo: `src/modelo/Archivo.java`

Se reemplazó el booleano simple de bloqueo por un modelo de lock real:
- `lectoresActivos`
- `lockEscrituraActivo`
- `procesoEscritor`
- `Cola<Proceso> colaEspera`

Se agregaron métodos para tomar/liberar locks y consultar estado.

### 2) Solicitudes de operación sobre archivos existentes
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se agregó:
- `solicitarOperacionArchivo(String nombreArchivo, TipoOperacion operacion)`

Este método encola y despacha operaciones de `LEER`, `ACTUALIZAR` y `ELIMINAR` sobre archivos existentes.

### 3) Bloqueo automático y cola de espera por recurso
Archivo: `src/modelo/GestorSistemaArchivos.java`

En `ejecutarProceso(...)` ahora:
- Si no puede adquirir lock, el proceso pasa a `BLOQUEADO` y entra en la cola de espera del archivo.
- Si adquiere lock, la operación termina y el lock se libera automáticamente al finalizar.
- Tras liberar, los procesos en espera del archivo se reencolan como `LISTO` para reintentar.

### 4) Tipos de lock aplicados
Archivo: `src/modelo/GestorSistemaArchivos.java`

Reglas implementadas:
- `LEER` usa lock compartido.
- `ACTUALIZAR` y `ELIMINAR` usan lock exclusivo.
- `ELIMINAR` conserva liberación de bloques mediante el flujo de eliminación existente.

### 5) API de diagnóstico de locks
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se agregó:
- `obtenerResumenLockArchivo(String nombreArchivo)`

Devuelve resumen de lectores activos, escritor y cantidad en espera.

## Resultado funcional
- El sistema bloquea procesos cuando el recurso está ocupado.
- Se liberan locks automáticamente al terminar cada operación.
- Existe una cola de espera por archivo para reintento ordenado.
- El backend queda listo para mostrar locks en la UI del siguiente paso.

## Criterio de cierre del Paso 5
Paso 5 se considera completado porque:
1. Se implementaron lock compartido y exclusivo.
2. Se agregó bloqueo y espera por recurso.
3. Se implementó liberación automática de lock al finalizar operación.
