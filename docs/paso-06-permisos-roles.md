# Paso 6 - Permisos y modo Admin/Usuario en backend

Fecha: 23-03-2026

## Objetivo del paso
Aplicar reglas de autorización en la capa de negocio (no solo en la interfaz) para distinguir de forma real entre modo administrador y modo usuario.

## Cambios implementados

### 1) Sesión activa en el gestor
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se agregaron campos y métodos:
- `usuarioActual`
- `modoAdministrador`
- `configurarSesion(usuario, modoAdministrador)`
- `obtenerUsuarioActual()`
- `esModoAdministrador()`

Con esto, el backend decide permisos según sesión activa.

### 2) Visibilidad pública en elementos
Archivo: `src/modelo/ElementoSistema.java`

Se agregó propiedad:
- `publico`

Y métodos:
- `esPublico()`
- `establecerPublico(boolean)`

Esto permite soportar lectura de archivos propios o públicos en modo usuario.

### 3) Permisos backend en operaciones de estructura
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se reforzó autorización para que solo admin pueda:
- Crear archivos
- Crear directorios
- Renombrar elementos
- Eliminar elementos por UI (`eliminarElementoSeguro`)

### 4) Permisos backend en operaciones de archivo
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se agregó validación por operación en `solicitarOperacionArchivo(...)`:
- `LEER`: permitido en modo usuario solo si el archivo es propio o público.
- `ACTUALIZAR` / `ELIMINAR`: permitido en modo usuario solo sobre archivos propios.

### 5) Soporte de archivo público al crear
Archivo: `src/modelo/GestorSistemaArchivos.java`

Se agregó sobrecarga de creación:
- `crearArchivo(..., boolean publico)`

### 6) UI conectada a sesión
Archivo: `src/vista/VentanaPrincipal.java`

Se agregaron controles:
- Campo de usuario
- Botón "Aplicar Sesión"

Y sincronización automática al cambiar de modo.

Además, crear archivo ahora permite elegir visibilidad pública.

## Resultado funcional
- El modo usuario ya no depende solo de validaciones visuales.
- El backend impone permisos por rol y por dueño/visibilidad.
- El sistema ya contempla archivos públicos para lectura en modo usuario.

## Criterio de cierre del Paso 6
Paso 6 se considera completado porque:
1. El rol activo se gestiona en backend.
2. Se aplican restricciones reales por operación y propietario.
3. Se soporta acceso de lectura a archivos públicos en modo usuario.
