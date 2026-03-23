# Paso 7 - Persistencia JSON del estado del sistema

Fecha: 23-03-2026

## Objetivo del paso
Permitir guardar y restaurar el estado completo del sistema de archivos desde un archivo JSON para reutilizar la simulación en futuras ejecuciones.

## Cambios implementados

### 1) API de persistencia en backend
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se agregaron métodos públicos:
- `guardarEstadoEnJson(String rutaArchivo)`
- `cargarEstadoDesdeJson(String rutaArchivo)`

Estos métodos manejan errores de I/O y de formato con mensajes claros.

### 2) Serialización del estado completo
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se serializa a JSON:
- Configuración general (`version`, `cantidadBloques`).
- Sesión (`usuario`, `admin`).
- Planificador (`politica`, `cabezal`, `desplazamiento`, `asc`).
- Estructura de directorios (en formato de rutas).
- Archivos (dueño, visibilidad, tamaño, bloque inicial, ruta padre).
- Bloques del disco (ocupación, archivo, enlace siguiente).

### 3) Restauración desde JSON
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se implementó carga robusta:
- Reinicializa el estado interno del gestor.
- Reconstruye directorios y archivos por rutas.
- Restaura ocupación y enlaces de bloques del disco.
- Restaura sesión y planificador.

### 4) Utilidades internas de parseo JSON
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se incorporaron utilidades internas sin librerías externas:
- Extracción de arreglos y objetos.
- Parseo de cadenas, enteros y booleanos.
- Escape/desescape básico para strings JSON.

### 5) Integración en la interfaz
Archivo: `src/vista/VentanaPrincipal.java`

Se agregaron botones:
- `Guardar JSON`
- `Cargar JSON`

Flujo UI:
- Uso de `JFileChooser` para seleccionar ruta.
- Actualización automática de árbol, disco, tabla y estado de planificador tras cargar.

## Resultado funcional
- Se puede exportar el estado actual de la simulación a JSON.
- Se puede importar luego ese JSON y continuar desde el mismo estado.
- No se requirieron dependencias externas de JSON.

## Criterio de cierre del Paso 7
Paso 7 se considera completado porque:
1. Existe guardado/carga funcional del estado completo.
2. La interfaz permite ejecutar ambas acciones.
3. El sistema restaura estructura, disco, sesión y planificador desde JSON.
