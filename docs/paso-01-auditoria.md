# Paso 1 - Auditoría técnica contra enunciado

Fecha: 23-03-2026

## Alcance revisado
Se auditó el código en `src/` y se contrastó con `enunciado.md`.

## Matriz de cumplimiento (estado actual)

### 1) Visualización de estructura (JTree)
**Estado:** Parcialmente cumplido
- Evidencia: `vista.VentanaPrincipal` usa `JTree`, crea nodos recursivos y actualiza vista.
- Falta: panel de detalles del elemento seleccionado (nombre, tamaño, dueño) explícito como vista dedicada.

### 2) Simulación del SD y asignación encadenada
**Estado:** Parcialmente cumplido
- Evidencia: `modelo.DiscoVirtual`, `modelo.Bloque`, `GestorSistemaArchivos.asignarBloquesAArchivo`.
- Evidencia de cadena: cada bloque mantiene `siguienteBloque`.
- Falta: no hay visualización del proceso/solicitud ocupando bloque, ni métricas de fragmentación.

### 3) CRUD archivos/directorios
**Estado:** Parcialmente cumplido
- Evidencia: crear archivo/directorio y eliminar (incluye eliminación recursiva de directorio).
- Falta: operación de actualización/renombrado no está en UI; lectura está limitada a visualización general.

### 4) Planificación de disco (FIFO, SSTF, SCAN, C-SCAN)
**Estado:** No cumplido
- Evidencia: no existe planificador ni estrategias; la cola se atiende de forma directa (`ejecutarProceso`).

### 5) Modo Administrador vs Usuario
**Estado:** Parcialmente cumplido
- Evidencia: `comboModoUsuario` restringe crear/eliminar en UI.
- Falta: permisos por dueño/archivo y reglas detalladas para modo usuario (lectura propia/pública).

### 6) Tabla de asignación de archivos (JTable)
**Estado:** Cumplido básico
- Evidencia: `tablaAsignacion` con nombre, bloques, primer bloque, color.
- Falta: acoplar con todos los cambios de procesos/planificador en tiempo real cuando exista.

### 7) Almacenamiento del estado (JSON)
**Estado:** No cumplido
- Evidencia: no hay módulo de persistencia/carga.

### 8) Concurrencia y locks (lectura/escritura)
**Estado:** No cumplido
- Evidencia: `Archivo` tiene `bloqueado:boolean` simple, pero no hay lock compartido/exclusivo ni cola de espera por recurso.

### 9) Cola de procesos y estados
**Estado:** Parcialmente cumplido
- Evidencia: `Proceso`, `EstadoProceso`, `Cola<Proceso>`.
- Falta: transición completa de estados (NUEVO→LISTO→EJECUTANDO...), bloqueo por recurso y reintentos.

### 10) Journal y recuperación ante fallos
**Estado:** No cumplido
- Evidencia: no existen clases/flujo de journal (PENDIENTE/COMMIT/RECOVERY).

### 11) Restricción de estructuras propias (sin colecciones Java para estructura del sistema)
**Estado:** Cumplido en lo revisado
- Evidencia: uso de `estructuras.Lista`, `estructuras.Cola`, `estructuras.Nodo`.

---

## Hallazgos técnicos relevantes
- El proyecto ya tiene base funcional de UI + modelo para CRUD inicial.
- Existe asignación encadenada válida para creación y liberación al eliminar.
- Faltan casi todos los requisitos avanzados (planificador real, locks, journal, persistencia JSON).
- La clase principal está correctamente definida para Ant como `main.class=vista.VentanaPrincipal`.

## Roadmap de implementación (paso a paso)

### Paso 2: Endurecer núcleo CRUD
- Agregar renombrado de archivo/directorio.
- Validar nombres duplicados en un mismo directorio.
- Validar límites de tamaño y errores de espacio.

### Paso 3: Motor de procesos
- Introducir ciclo de vida completo y despachador.
- Separar `solicitar*` de `ejecutar*`.

### Paso 4: Planificador de disco (4 políticas)
- Implementar estrategia: FIFO, SSTF, SCAN, C-SCAN.
- Configurar posición inicial del cabezal y desplazamiento acumulado.

### Paso 5: Concurrencia real por archivo
- Lock compartido para lectura.
- Lock exclusivo para escritura.
- Cola de espera por recurso + desbloqueo automático.

### Paso 6: Modo admin/usuario con permisos
- Enforzar permisos por dueño y visibilidad.
- Restringir operaciones por rol a nivel de gestor (no solo UI).

### Paso 7: Persistencia JSON
- Guardar/cargar estructura de directorios, archivos, bloques y metadatos.

### Paso 8: Journaling + recuperación
- Registrar transacciones críticas como PENDIENTE.
- Confirmar COMMIT al finalizar.
- Recuperar PENDIENTES al iniciar.

### Paso 9: UI de operación en tiempo real
- Vista de cola de procesos y estados.
- Vista de locks activos.
- Log de eventos/journal.

### Paso 10: Cierre y validación de enunciado
- Checklist final de requisitos.
- Escenarios de prueba de demostración.

---

## Criterio de cierre del Paso 1
Paso 1 se considera completado porque existe:
1. Diagnóstico de cumplimiento requisito por requisito.
2. Evidencia técnica por clase/método.
3. Backlog ordenado para completar el 100% del enunciado.
