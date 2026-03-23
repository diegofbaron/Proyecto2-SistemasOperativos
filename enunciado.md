# [cite_start]Universidad Metropolitana [cite: 1]
## [cite_start]Departamento de Gestión de Proyectos y Sistemas [cite: 2]
### [cite_start]Sistemas Operativos [cite: 3]
[cite_start]**Trimestre:** 2526-2 [cite: 4]  
[cite_start]**Preparadores:** Sofía León y Marielena Ginez [cite: 5]  
[cite_start]**Proyecto 2:** Simulador Virtual de Sistema de Archivos Concurrente con Gestión de Permisos, Asignación de Bloques y Recuperación ante Fallos [cite: 6, 7]

---

## [cite_start]Planteamiento del Problema [cite: 8]

[cite_start]El objetivo de este proyecto es que los estudiantes desarrollen un simulador avanzado de sistema de archivos en el que puedan comprender y aplicar conceptos fundamentales como la gestión de archivos y directorios, la asignación encadenada de bloques de almacenamiento, la administración de permisos, la fragmentación del espacio en disco, el manejo de operaciones de entrada/salida mediante procesos de usuario, y el control de concurrencia en el acceso a archivos compartidos[cite: 9].

[cite_start]Para ello, los estudiantes deberán implementar un sistema de archivos simulado en Java utilizando NetBeans, con una interfaz gráfica que represente visualmente la estructura jerárquica de directorios y archivos mediante un **JTree**, así como la distribución de bloques en una Simulación de un Disco (SD), una tabla de asignación de archivos, y un sistema de gestión de procesos que realicen operaciones de $E/S$[cite: 10].

[cite_start]El sistema debe operar en dos modos de usuario: **modo administrador** y **modo usuario**[cite: 11].

* [cite_start]En el **modo administrador** se permite realizar todas las operaciones, incluyendo crear, modificar y eliminar archivos y directorios, gestionar los procesos del sistema, cambiar las políticas de planificación del disco, y visualizar información completa del SD[cite: 12].
* [cite_start]Por otro lado, el **modo usuario** restringe las acciones a solo lectura de archivos propios o públicos y la creación de procesos para realizar operaciones de E/S sobre sus propios archivos, impidiendo modificar archivos del sistema o acceder a información de otros usuarios[cite: 13].

[cite_start]Los archivos creados deberán tener un tamaño en bloques, los cuales serán asignados utilizando el método de **asignación encadenada**, en donde cada archivo se representa como una lista enlazada de bloques en el SD[cite: 14]. [cite_start]Sin embargo, la asignación de estos bloques no se realizará de manera directa, sino que será gestionada por procesos de usuario que soliciten operaciones de E/S al sistema[cite: 15]. [cite_start]Cada vez que un proceso necesite crear, leer, actualizar o eliminar un archivo, generará una solicitud de E/S que será procesada por el sistema de archivos según la política de planificación de disco activa[cite: 16].

[cite_start]El sistema deberá mantener una cola de procesos, donde cada proceso tendrá un estado (nuevo, listo, ejecutando, bloqueado o terminado) y estará asociado a una operación específica sobre el sistema de archivos[cite: 17]. [cite_start]Cuando un proceso realiza una solicitud CRUD, esta solicitud entra en la cola de E/S del disco, donde el planificador determinará el orden en que serán atendidas las solicitudes según la política configurada (**FIFO, SSTF, SCAN, C-SCAN**, entre otras)[cite: 18].

[cite_start]Adicionalmente, el sistema deberá implementar control de concurrencia sobre archivos compartidos mediante **locks**, con el fin de simular un acceso realista multi-proceso al sistema de archivos[cite: 19, 20]. [cite_start]Se deberá permitir el uso de lock compartido para lectura y lock exclusivo para escritura, bloqueando automáticamente procesos que intenten acceder a un recurso ocupado y liberando locks al finalizar cada operación[cite: 20].

[cite_start]De forma adicional, el sistema puede implementar un mecanismo de **journaling** inspirado en sistemas reales como NTFS, registrando operaciones críticas sobre la estructura del sistema antes de ejecutarlas, permitiendo simular recuperación ante fallos y garantizando la consistencia del sistema[cite: 21].

[cite_start]La interfaz gráfica debe mostrar en tiempo real el estado del sistema[cite: 22]:
* [cite_start]La estructura de directorios y archivos en el **JTree**[cite: 22].
* [cite_start]La visualización del disco con los bloques ocupados y libres (indicando qué proceso o archivo ocupa cada bloque)[cite: 22].
* [cite_start]La tabla de asignación de archivos[cite: 22].
* [cite_start]Una vista de la cola de procesos con sus estados actuales y las operaciones que están solicitando[cite: 22].
* [cite_start]Visualización clara de los locks activos por archivo[cite: 23].
* [cite_start]El estado del buffer (si se implementa)[cite: 23].
* [cite_start]El estado del journal y un log de eventos del sistema[cite: 23].

[cite_start]El sistema deberá actualizarse en tiempo real cada vez que se realice una operación CRUD (Crear, Leer, Actualizar, Eliminar), reflejando los cambios en toda la estructura y métricas del sistema[cite: 24].

---

## [cite_start]Requerimientos Funcionales [cite: 25]

### [cite_start]1. Visualización de la estructura del sistema de archivos [cite: 26]
* [cite_start]Implementar un **JTree** para representar la estructura jerárquica de directorios y archivos[cite: 27].
* [cite_start]Mostrar información del archivo o directorio seleccionado (nombre, tamaño en bloques y dueño)[cite: 28].
* Referencia de aprendizaje: Curso Java Anexo II. JTree. [cite_start]Vídeo 266[cite: 29, 30].

### [cite_start]2. Simulación del SD y asignación de bloques [cite: 31]
* [cite_start]Representar visualmente el SD como un conjunto de bloques, indicando cuáles están ocupados y cuáles están libres[cite: 32].
* [cite_start]Se deben diferenciar los archivos almacenados mediante el uso de distintos colores[cite: 33].
* [cite_start]Simular la **asignación encadenada**, donde cada archivo se almacena como una lista enlazada de bloques[cite: 34].
* [cite_start]Manejar la liberación de bloques cuando se eliminan archivos[cite: 35].
* [cite_start]Definir un tamaño limitado de almacenamiento, evitando la creación de archivos si no hay espacio disponible[cite: 36].

### [cite_start]3. Gestión de archivos y directorios (CRUD) [cite: 37]
* [cite_start]**Crear:** Los administradores podrán crear archivos y directorios especificando el tamaño del archivo en bloques[cite: 38, 39, 41].
* [cite_start]**Leer:** Todos los usuarios podrán visualizar la estructura del sistema y sus propiedades[cite: 42, 43].
* [cite_start]**Actualizar:** Solo los administradores podrán modificar el nombre[cite: 44, 45].
* **Eliminar:** Al borrar un archivo, se liberarán los bloques asignados. [cite_start]Al eliminar un directorio, se deben eliminar todos sus archivos y subdirectorios[cite: 46, 48, 49].

### [cite_start]4. Planificación de disco [cite: 50]
* [cite_start]El planificador debe determinar el orden de atención de solicitudes en la cola de $E/S$ según la política seleccionada: **FIFO, SSTF, SCAN, C-SCAN**, entre otras[cite: 51].
* [cite_start]Se deben configurar al menos **cuatro (4) políticas**[cite: 52].
* [cite_start]La interfaz debe mostrar la posición actual del cabezal y su desplazamiento en tiempo real[cite: 53].
* [cite_start]Permitir definir la ubicación inicial del cabezal de manera arbitraria al inicio de la simulación[cite: 54].

### [cite_start]5. Modo Administrador vs. Modo Usuario [cite: 55]
* [cite_start]**Administrador:** Permite realizar todas las operaciones[cite: 57].
* [cite_start]**Usuario:** Restringido a solo lectura[cite: 58].
* [cite_start]El modo se seleccionará mediante la interfaz[cite: 59].

### [cite_start]6. Tabla de asignación de archivos [cite: 60]
* [cite_start]Implementar un **JTable** que muestre: nombre del archivo, cantidad de bloques asignados y dirección del primer bloque[cite: 61, 63, 64, 65].
* [cite_start]Si se usan colores para los archivos, incluirlos en la tabla[cite: 66].
* [cite_start]La tabla debe actualizarse en tiempo real con cada operación CRUD[cite: 67].

### [cite_start]7. Almacenar el estado de los archivos en el sistema [cite: 68]
* [cite_start]Los estudiantes podrán elegir almacenar la información en un archivo **JSON** para cargar los datos en futuras ejecuciones[cite: 69, 70].

### [cite_start]8. Recuperación ante fallos con Journaling [cite: 71]
* [cite_start]Implementar un Journal (log de transacciones) para operaciones críticas (crear, eliminar)[cite: 72].
* [cite_start]**Flujo:** Registrar operación como PENDIENTE $\rightarrow$ Ejecutar en el sistema $\rightarrow$ Marcar como CONFIRMADA (commit)[cite: 73, 76, 77, 78].
* [cite_start]Incluir un mecanismo de **"Simular fallo"** que interrumpa el sistema antes del commit[cite: 79].
* [cite_start]Al reiniciar: Revisar el Journal y deshacer (undo) operaciones pendientes para restaurar consistencia[cite: 80, 81, 82].
* [cite_start]Mostrar el Journal en la interfaz[cite: 83].

---

## [cite_start]Casos de Prueba Recomendados [cite: 84]

### [cite_start]Políticas de Planificación [cite: 85]
[cite_start]Se deben cargar previamente los *System Files* y dejar el cabezal en la posición indicada[cite: 88].

#### [cite_start]1. Caso P1 - FIFO [cite: 91]
| Elemento | Valor |
| :--- | :--- |
| Cabezal inicial | [cite_start]50 [cite: 92] |
| Solicitudes | [cite_start]95, 180, 34, 119, 11, 123, 62, 64 [cite: 92] |
| Política | [cite_start]FIFO [cite: 92] |
| Orden esperado | [cite_start]$95\rightarrow180\rightarrow34\rightarrow119\rightarrow11\rightarrow123\rightarrow62\rightarrow64$ [cite: 92] |

#### [cite_start]2. Caso P2 - SSTF [cite: 93]
| Elemento | Valor |
| :--- | :--- |
| Cabezal inicial | [cite_start]50 [cite: 94] |
| Solicitudes | [cite_start]95, 180, 34, 119, 11, 123, 62, 64 [cite: 94] |
| Política | [cite_start]SSTF [cite: 94] |
| Orden esperado | [cite_start]$62\rightarrow64\rightarrow34\rightarrow11\rightarrow95\rightarrow119\rightarrow123\rightarrow180$ [cite: 94] |

#### [cite_start]3. Caso P3 - SCAN [cite: 95]
| Elemento | Valor |
| :--- | :--- |
| Cabezal inicial | [cite_start]50 [cite: 98] |
| Solicitudes | [cite_start]95, 180, 34, 119, 11, 123, 62, 64 [cite: 98] |
| Dirección | [cite_start]↑ [cite: 98] |
| Política | [cite_start]SCAN [cite: 98] |
| Orden esperado | [cite_start]$62\rightarrow64\rightarrow95\rightarrow119\rightarrow123\rightarrow180\rightarrow34\rightarrow11$ [cite: 98] |

#### [cite_start]4. Caso P4 - C-SCAN [cite: 99]
| Elemento | Valor |
| :--- | :--- |
| Cabezal inicial | [cite_start]50 [cite: 100] |
| Dirección | [cite_start]↑ [cite: 100] |
| Política | [cite_start]C-SCAN [cite: 100] |
| Orden esperado | [cite_start]$62\rightarrow64\rightarrow95\rightarrow119\rightarrow123\rightarrow180\rightarrow11\rightarrow34$ [cite: 100] |

---

### [cite_start]Journaling [cite: 101]

#### [cite_start]1. Caso J1 - Crash en CREATE [cite: 102]
| Elemento | Valor |
| :--- | :--- |
| Operación | [cite_start]CREATE A.txt (4 bloques) [cite: 103] |
| Punto de fallo | [cite_start]Después de asignar bloques, antes del commit [cite: 103] |
| Resultado esperado | [cite_start]Archivo no aparece y bloques se liberan tras reinicio [cite: 103] |
| Evidencia | [cite_start]Journal PENDIENTE UNDO aplicado [cite: 103] |

---

## [cite_start]Ejemplo de Estructura JSON [cite: 104, 106]

```json
{
  "test_id": "P1",
  "initial_head": 50,
  "requests": [
    {"pos": 11, "op": "READ"},
    {"pos": 34, "op": "READ"},
    {"pos": 62, "op": "UPDATE"},
    {"pos": 70, "op": "READ"},
    {"pos": 95, "op": "UPDATE"},
    {"pos": 119, "op": "DELETE"},
    {"pos": 131, "op": "UPDATE"},
    {"pos": 180, "op": "READ"}
  ],
  "system_files": {
    "11": {"name": "boot_sect.bin", "blocks": 2},
    "34": {"name": "readme.txt", "blocks": 1},
    "62": {"name": "script.py", "blocks": 8},
    "70": {"name": "style.css", "blocks": 6},
    "95": {"name": "config.sys", "blocks": 4},
    "119": {"name": "image_01.png", "blocks": 12},
    "131": {"name": "data_log.csv", "blocks": 28},
    "180": {"name": "video_clip.mp4", "blocks": 52}
  }
}
```
[cite_start]*Nota: Tienen libertad en cómo aplicar las operaciones update en este tipo de casos automatizados[cite: 155].*

---

## [cite_start]Consideraciones Técnicas y Reglas de Entrega [cite: 156]

* **Conformación de Equipos:** Parejas (máximo 2 personas). Excepcionalmente grupos de 3 si alguien queda solo. [cite_start]No se permiten equipos de diferentes secciones[cite: 158, 159, 160].
* [cite_start]**Tecnología y Entorno:** * Lenguaje: Java (Versión 21 o superior)[cite: 162].
    * IDE: **Estrictamente en NetBeans**. [cite_start]Programas que no se ejecuten adecuadamente en este entorno serán calificados con 0[cite: 163].
    * [cite_start]**Restricción de Librerías:** Solo permitidas para visualización de gráficas (ej. JFreeChart), manejo de JSON, Hilos y Semáforos[cite: 164].
    * [cite_start]**Estructuras de Datos:** Queda **terminantemente prohibido** el uso de `java.util.ArrayList`, `Queue`, `Stack`, `Vector` o cualquier colección del framework de Java[cite: 165]. [cite_start]Los estudiantes deben programar sus propias estructuras (listas enlazadas, colas, etc.)[cite: 166].
* **Estándares de Desarrollo en GitHub (Obligatorio):**
    * Uso obligatorio de repositorio. [cite_start]Sin repositorio la nota es 0[cite: 167, 168].
    * Uso de ramas: No trabajar solo en `main`. [cite_start]Se deben usar ramas por funcionalidad (ej: `feat/scheduler`, `feat/gui`) y contar con una rama `develop`[cite: 168, 169].
    * [cite_start]Gestión de Tareas (Issues)[cite: 170].
* [cite_start]**Interfaz:** Debe ser intuitiva y mostrar cambios de estado en tiempo real[cite: 171]. [cite_start]Es obligatorio implementar validaciones de tipo de dato y rango[cite: 172, 173].
* **Entrega y Evaluación:**
    * [cite_start]Fecha límite: Lunes de Semana 12 antes de las 7:00 AM[cite: 174].
    * [cite_start]Canal: Enviar informe PDF y link del repositorio a `sleon@correo.unimet.edu.ve` y `mginez@correo.unimet.edu.ve`[cite: 175].
    * **Defensa Presencial:** Lunes de Semana 12. Individual y obligatoria. [cite_start]Inasistencia conlleva nota 0. Reprobar la defensa limita la nota máxima a 10 puntos[cite: 176, 177, 178].
    * [cite_start]Es imperativo que ambos miembros dominen el funcionamiento total de la solución[cite: 179].
