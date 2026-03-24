/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import estructuras.Cola;
import estructuras.Lista;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GestorSistemaArchivos {
    private DiscoVirtual disco;
    private Directorio raiz;
    private Cola<Proceso> colaProcesos;
    private Lista<Proceso> historialProcesos;
    private Lista<Archivo> todosLosArchivos;
    private int contadorProcesos;
    private PoliticaPlanificacion politicaActiva;
    private int posicionCabezal;
    private int desplazamientoCabezal;
    private boolean direccionAscendente;
    private int maximoIndiceDisco;
    private String usuarioActual;
    private boolean modoAdministrador;
    private Lista<EntradaJournal> journal;
    private boolean simularFalloAntesCommit;
    private int contadorTransacciones;

    public GestorSistemaArchivos(int cantidadBloquesDisco) {
        this.disco = new DiscoVirtual(cantidadBloquesDisco);
        this.raiz = new Directorio("Raiz", "admin", null);
        this.colaProcesos = new Cola<>();
        this.historialProcesos = new Lista<>();
        this.todosLosArchivos = new Lista<>();
        this.contadorProcesos = 1;
        this.politicaActiva = PoliticaPlanificacion.FIFO;
        this.posicionCabezal = 0;
        this.desplazamientoCabezal = 0;
        this.direccionAscendente = true;
        this.maximoIndiceDisco = Math.max(0, cantidadBloquesDisco - 1);
        this.usuarioActual = "admin";
        this.modoAdministrador = true;
        this.journal = new Lista<>();
        this.simularFalloAntesCommit = false;
        this.contadorTransacciones = 1;
    }

    public Directorio obtenerRaiz() {
        return raiz;
    }

    public DiscoVirtual obtenerDisco() {
        return disco;
    }

    public Cola<Proceso> obtenerColaProcesos() {
        return colaProcesos;
    }

    public Lista<Proceso> obtenerHistorialProcesos() {
        return historialProcesos;
    }

    public Lista<Archivo> obtenerTodosLosArchivos() {
        return todosLosArchivos;
    }

    public PoliticaPlanificacion obtenerPoliticaActiva() {
        return politicaActiva;
    }

    public int obtenerPosicionCabezal() {
        return posicionCabezal;
    }

    public int obtenerDesplazamientoCabezal() {
        return desplazamientoCabezal;
    }

    public boolean esDireccionAscendente() {
        return direccionAscendente;
    }

    public void configurarPlanificador(PoliticaPlanificacion politica, int posicionInicialCabezal, boolean direccionAscendente) {
        if (politica != null) {
            this.politicaActiva = politica;
        }
        this.posicionCabezal = normalizarPosicion(posicionInicialCabezal);
        this.direccionAscendente = direccionAscendente;
        this.desplazamientoCabezal = 0;
    }

    public void configurarSesion(String usuario, boolean modoAdministrador) {
        String usuarioNormalizado = usuario == null ? "" : usuario.trim();
        if (usuarioNormalizado.isEmpty()) {
            usuarioNormalizado = modoAdministrador ? "admin" : "usuario";
        }
        this.usuarioActual = usuarioNormalizado;
        this.modoAdministrador = modoAdministrador;
    }

    public String obtenerUsuarioActual() {
        return usuarioActual;
    }

    public boolean esModoAdministrador() {
        return modoAdministrador;
    }

    public boolean estaSimulacionFalloActiva() {
        return simularFalloAntesCommit;
    }

    public void configurarSimulacionFallo(boolean activar) {
        this.simularFalloAntesCommit = activar;
    }

    public Lista<String> obtenerResumenJournal() {
        Lista<String> resumen = new Lista<>();
        for (int i = 0; i < journal.obtenerTamano(); i++) {
            EntradaJournal e = journal.obtener(i);
            resumen.agregar("TX-" + e.id + " " + e.operacion + " " + e.estado + " " + e.rutaArchivo);
        }
        return resumen;
    }

    public Lista<String> obtenerResumenColaProcesos() {
        Lista<String> resumen = new Lista<>();
        for (int i = 0; i < colaProcesos.obtenerTamano(); i++) {
            Proceso proceso = colaProcesos.obtener(i);
            resumen.agregar(formatearProceso(proceso));
        }
        return resumen;
    }

    public Lista<String> obtenerResumenHistorialProcesos(int limite) {
        Lista<String> resumen = new Lista<>();
        int total = historialProcesos.obtenerTamano();
        int inicio = Math.max(0, total - Math.max(0, limite));
        for (int i = inicio; i < total; i++) {
            Proceso proceso = historialProcesos.obtener(i);
            resumen.agregar(formatearProceso(proceso));
        }
        return resumen;
    }

    public Lista<String> obtenerResumenLocksActivos() {
        Lista<String> resumen = new Lista<>();
        for (int i = 0; i < todosLosArchivos.obtenerTamano(); i++) {
            Archivo archivo = todosLosArchivos.obtener(i);
            int enEspera = archivo.obtenerColaEspera().obtenerTamano();
            if (archivo.obtenerLectoresActivos() == 0 && !archivo.tieneLockEscrituraActivo() && enEspera == 0) {
                continue;
            }
            String escritor = archivo.tieneLockEscrituraActivo() ? String.valueOf(archivo.obtenerProcesoEscritor()) : "-";
            resumen.agregar(archivo.obtenerNombre() + " | R=" + archivo.obtenerLectoresActivos() + " W=" + escritor + " Q=" + enEspera);
        }
        return resumen;
    }

    public Lista<String> generarReporteValidacionSistema() {
        Lista<String> reporte = new Lista<>();
        int totalArchivos = todosLosArchivos.obtenerTamano();
        int totalBloques = disco.obtenerCantidadBloques();
        int libres = contarBloquesLibres();
        int ocupados = totalBloques - libres;

        reporte.agregar("=== Validación Integral del Sistema ===");
        reporte.agregar("Sesión activa: " + usuarioActual + " (" + (modoAdministrador ? "ADMIN" : "USUARIO") + ")");
        reporte.agregar("Política activa: " + politicaActiva + " | Cabezal=" + posicionCabezal + " | Desplazamiento=" + desplazamientoCabezal);
        reporte.agregar("Archivos totales: " + totalArchivos);
        reporte.agregar("Bloques ocupados/libres: " + ocupados + "/" + libres + " de " + totalBloques);
        reporte.agregar("Procesos en cola: " + colaProcesos.obtenerTamano());
        reporte.agregar("Procesos en historial: " + historialProcesos.obtenerTamano());
        reporte.agregar("Locks activos: " + obtenerResumenLocksActivos().obtenerTamano());
        reporte.agregar("Entradas journal: " + journal.obtenerTamano());

        int pendientesJournal = 0;
        for (int i = 0; i < journal.obtenerTamano(); i++) {
            EntradaJournal entrada = journal.obtener(i);
            if ("PENDIENTE".equals(entrada.estado)) {
                pendientesJournal++;
            }
        }
        reporte.agregar("Transacciones journal pendientes: " + pendientesJournal);

        reporte.agregar("Checks:");
        reporte.agregar((raiz != null ? "[OK] " : "[X] ") + "Raíz del árbol disponible");
        reporte.agregar((totalBloques > 0 ? "[OK] " : "[X] ") + "Disco virtual inicializado");
        reporte.agregar((posicionCabezal >= 0 && posicionCabezal <= maximoIndiceDisco ? "[OK] " : "[X] ") + "Posición de cabezal válida");
        reporte.agregar((pendientesJournal == 0 ? "[OK] " : "[WARN] ") + "Journal sin pendientes críticas");
        return reporte;
    }

    public Lista<String> generarReportePruebasRecomendadas() {
        Lista<String> reporte = new Lista<>();
        reporte.agregar("=== Paso 11 - Pruebas recomendadas ===");
        reporte.agregar("Cabezal inicial: 50");
        reporte.agregar("Solicitudes: 95, 180, 34, 119, 11, 123, 62, 64");

        Lista<Integer> solicitudes = construirSolicitudesPrueba();
        reporte.agregar(validarPoliticaPrueba(PoliticaPlanificacion.FIFO, solicitudes, new int[]{95, 180, 34, 119, 11, 123, 62, 64}));
        reporte.agregar(validarPoliticaPrueba(PoliticaPlanificacion.SSTF, solicitudes, new int[]{62, 64, 34, 11, 95, 119, 123, 180}));
        reporte.agregar(validarPoliticaPrueba(PoliticaPlanificacion.SCAN, solicitudes, new int[]{62, 64, 95, 119, 123, 180, 34, 11}));
        reporte.agregar(validarPoliticaPrueba(PoliticaPlanificacion.C_SCAN, solicitudes, new int[]{62, 64, 95, 119, 123, 180, 11, 34}));

        reporte.agregar(validarCasoJournalJ1());
        return reporte;
    }

    private Lista<Integer> construirSolicitudesPrueba() {
        Lista<Integer> solicitudes = new Lista<>();
        solicitudes.agregar(95);
        solicitudes.agregar(180);
        solicitudes.agregar(34);
        solicitudes.agregar(119);
        solicitudes.agregar(11);
        solicitudes.agregar(123);
        solicitudes.agregar(62);
        solicitudes.agregar(64);
        return solicitudes;
    }

    private String validarPoliticaPrueba(PoliticaPlanificacion politica, Lista<Integer> solicitudesOriginales, int[] esperado) {
        Lista<Integer> obtenido = calcularOrdenPolitica(politica, solicitudesOriginales, 50, true);
        boolean coincide = coincideOrden(obtenido, esperado);
        return (coincide ? "[OK] " : "[X] ")
                + politica + " esperado=" + formatearArreglo(esperado)
                + " obtenido=" + formatearLista(obtenido);
    }

    private Lista<Integer> calcularOrdenPolitica(PoliticaPlanificacion politica, Lista<Integer> solicitudesOriginales, int cabezalInicial, boolean direccionAsc) {
        Lista<Integer> pendientes = copiarListaEnteros(solicitudesOriginales);
        Lista<Integer> orden = new Lista<>();
        int cabezal = cabezalInicial;
        boolean asc = direccionAsc;

        while (pendientes.obtenerTamano() > 0) {
            int seleccion;
            switch (politica) {
                case SSTF:
                    seleccion = seleccionarSstfEnteros(pendientes, cabezal);
                    break;
                case SCAN:
                    seleccion = seleccionarScanEnteros(pendientes, cabezal, asc);
                    if (seleccion == -1) {
                        asc = !asc;
                        seleccion = seleccionarScanEnteros(pendientes, cabezal, asc);
                    }
                    break;
                case C_SCAN:
                    seleccion = seleccionarScanEnteros(pendientes, cabezal, asc);
                    if (seleccion == -1) {
                        seleccion = seleccionarExtremoEnteros(pendientes, asc);
                    }
                    break;
                case FIFO:
                default:
                    seleccion = 0;
                    break;
            }

            int siguiente = pendientes.obtener(seleccion);
            orden.agregar(siguiente);
            pendientes.eliminar(siguiente);
            cabezal = siguiente;
        }

        return orden;
    }

    private int seleccionarSstfEnteros(Lista<Integer> pendientes, int cabezal) {
        int indice = 0;
        int mejorDistancia = Integer.MAX_VALUE;
        int mejorValor = Integer.MAX_VALUE;
        for (int i = 0; i < pendientes.obtenerTamano(); i++) {
            int valor = pendientes.obtener(i);
            int distancia = Math.abs(valor - cabezal);
            if (distancia < mejorDistancia || (distancia == mejorDistancia && valor < mejorValor)) {
                indice = i;
                mejorDistancia = distancia;
                mejorValor = valor;
            }
        }
        return indice;
    }

    private int seleccionarScanEnteros(Lista<Integer> pendientes, int cabezal, boolean asc) {
        int indice = -1;
        int mejorDistancia = Integer.MAX_VALUE;
        for (int i = 0; i < pendientes.obtenerTamano(); i++) {
            int valor = pendientes.obtener(i);
            int delta = valor - cabezal;
            if (asc && delta < 0) {
                continue;
            }
            if (!asc && delta > 0) {
                continue;
            }
            int distancia = Math.abs(delta);
            if (distancia < mejorDistancia) {
                mejorDistancia = distancia;
                indice = i;
            }
        }
        return indice;
    }

    private int seleccionarExtremoEnteros(Lista<Integer> pendientes, boolean asc) {
        int indice = 0;
        int extremo = pendientes.obtener(0);
        for (int i = 1; i < pendientes.obtenerTamano(); i++) {
            int valor = pendientes.obtener(i);
            if (asc) {
                if (valor < extremo) {
                    extremo = valor;
                    indice = i;
                }
            } else {
                if (valor > extremo) {
                    extremo = valor;
                    indice = i;
                }
            }
        }
        return indice;
    }

    private Lista<Integer> copiarListaEnteros(Lista<Integer> original) {
        Lista<Integer> copia = new Lista<>();
        for (int i = 0; i < original.obtenerTamano(); i++) {
            copia.agregar(original.obtener(i));
        }
        return copia;
    }

    private boolean coincideOrden(Lista<Integer> obtenido, int[] esperado) {
        if (obtenido.obtenerTamano() != esperado.length) {
            return false;
        }
        for (int i = 0; i < esperado.length; i++) {
            if (obtenido.obtener(i) != esperado[i]) {
                return false;
            }
        }
        return true;
    }

    private String formatearArreglo(int[] arreglo) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arreglo.length; i++) {
            sb.append(arreglo[i]);
            if (i < arreglo.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String formatearLista(Lista<Integer> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.obtenerTamano(); i++) {
            sb.append(lista.obtener(i));
            if (i < lista.obtenerTamano() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String validarCasoJournalJ1() {
        GestorSistemaArchivos prueba = new GestorSistemaArchivos(20);
        prueba.configurarSesion("admin", true);
        prueba.configurarSimulacionFallo(true);

        String resultadoCrear = prueba.crearArchivo("J1.txt", "admin", prueba.obtenerRaiz(), 4, false);
        int recuperadas = prueba.ejecutarRecuperacionJournalPendientes();
        Archivo restante = prueba.buscarArchivoPorNombre("J1.txt");

        boolean ok = resultadoCrear != null && recuperadas > 0 && restante == null;
        return (ok ? "[OK] " : "[X] ")
                + "J1 Crash en CREATE -> recovery "
                + "(mensajeFallo=" + (resultadoCrear != null)
                + ", recuperadas=" + recuperadas
                + ", archivoRestante=" + (restante != null) + ")";
    }

    public int ejecutarRecuperacionJournalPendientes() {
        int recuperadas = 0;
        // Recorremos el journal buscando lo que quedó a medias
        for (int i = 0; i < journal.obtenerTamano(); i++) {
            EntradaJournal entrada = journal.obtener(i);
            
            // Accedemos a la variable pública 'estado' directamente
            if (entrada.estado != null && (entrada.estado.equalsIgnoreCase("PENDIENTE") || 
                entrada.estado.equalsIgnoreCase("FALLO") || entrada.estado.equalsIgnoreCase("ABORTADA"))) {
                
                // Cambiamos el estado a RECUPERADO
                entrada.estado = "RECUPERADO";
                recuperadas++;
            }
        }
        return recuperadas;
    }

    private String formatearProceso(Proceso proceso) {
        if (proceso == null) {
            return "(proceso nulo)";
        }
        String nombreArchivo = proceso.obtenerArchivoDestino() != null
                ? proceso.obtenerArchivoDestino().obtenerNombre()
                : "(sin archivo)";
        return "P" + proceso.obtenerId() + " " + proceso.obtenerOperacion()
                + " " + nombreArchivo
                + " | " + proceso.obtenerEstado()
                + " | " + proceso.obtenerMensajeResultado();
    }

    public String guardarEstadoEnJson(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            return "Ruta de archivo inválida.";
        }

        try {
            String json = construirJsonEstado();
            Files.writeString(Path.of(rutaArchivo), json, StandardCharsets.UTF_8);
            return null;
        } catch (IOException ex) {
            return "No se pudo guardar el estado: " + ex.getMessage();
        }
    }

    public String cargarEstadoDesdeJson(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) {
            return "Ruta de archivo inválida.";
        }

        try {
            String json = Files.readString(Path.of(rutaArchivo), StandardCharsets.UTF_8);
            restaurarDesdeJson(json);
            return null;
        } catch (IOException ex) {
            return "No se pudo leer el archivo JSON: " + ex.getMessage();
        } catch (RuntimeException ex) {
            return "Formato JSON inválido o incompatible: " + ex.getMessage();
        }
    }

    public String crearArchivo(String nombre, String dueno, Directorio padre, int tamano) {
        if (!modoAdministrador) {
            return "Solo el administrador puede crear archivos.";
        }

        String errorValidacion = validarCreacion(nombre, padre, tamano);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        String propietario = normalizarNombre(dueno);
        if (propietario == null) {
            propietario = usuarioActual;
        }

        Proceso p = solicitarCreacionArchivo(nombre.trim(), propietario, padre, tamano);
        despacharSiguienteProceso();

        if (p.obtenerEstado() == EstadoProceso.BLOQUEADO) {
            return p.obtenerMensajeResultado();
        }

        return null;
    }

    public String crearArchivo(String nombre, String dueno, Directorio padre, int tamano, boolean publico) {
        String error = crearArchivo(nombre, dueno, padre, tamano);
        if (error != null) {
            return error;
        }

        Archivo creado = obtenerUltimoArchivoCreado();
        if (creado != null) {
            creado.establecerPublico(publico);
        }
        return null;
    }

    public String crearDirectorio(String nombre, String dueno, Directorio padre) {
        if (!modoAdministrador) {
            return "Solo el administrador puede crear directorios.";
        }

        String nombreNormalizado = normalizarNombre(nombre);
        if (nombreNormalizado == null) {
            return "El nombre no puede estar vacío.";
        }
        if (padre == null) {
            return "Debes seleccionar un directorio padre válido.";
        }
        if (existeNombreEnDirectorio(padre, nombreNormalizado, null)) {
            return "Ya existe un elemento con ese nombre en el directorio seleccionado.";
        }

        String propietario = normalizarNombre(dueno);
        if (propietario == null) {
            propietario = usuarioActual;
        }

        Directorio nuevoDir = new Directorio(nombreNormalizado, propietario, padre);
        padre.agregarHijo(nuevoDir);
        return null;
    }

    public String solicitarOperacionArchivo(String nombreArchivo, TipoOperacion operacion) {
        if (operacion == null || operacion == TipoOperacion.CREAR) {
            return "La operación solicitada no es válida para este método.";
        }

        Archivo archivo = buscarArchivoPorNombre(nombreArchivo);
        if (archivo == null) {
            return "No existe un archivo con ese nombre.";
        }

        String errorPermiso = validarPermisoOperacionArchivo(archivo, operacion);
        if (errorPermiso != null) {
            return errorPermiso;
        }

        int posicionSolicitud = archivo.obtenerBloqueInicial() >= 0 ? archivo.obtenerBloqueInicial() : estimarPosicionSolicitudCreacion();
        Proceso p = new Proceso(contadorProcesos++, operacion, archivo, archivo.obtenerTamano(), posicionSolicitud);
        p.establecerEstado(EstadoProceso.LISTO);
        colaProcesos.encolar(p);
        historialProcesos.agregar(p);

        // Ya NO llamamos a despacharSiguienteProceso(); aquí.
        // Dejamos que el proceso espere en la COLA hasta aplicar el planificador.
        
        return null;
    }

    public String renombrarElemento(ElementoSistema elemento, String nuevoNombre) {
        if (!modoAdministrador) {
            return "Solo el administrador puede renombrar elementos.";
        }

        if (elemento == null) {
            return "Debes seleccionar un elemento para renombrar.";
        }
        if (elemento.obtenerPadre() == null) {
            return "No se puede renombrar el directorio raíz del sistema.";
        }

        String nombreNormalizado = normalizarNombre(nuevoNombre);
        if (nombreNormalizado == null) {
            return "El nuevo nombre no puede estar vacío.";
        }

        Directorio padre = elemento.obtenerPadre();
        if (existeNombreEnDirectorio(padre, nombreNormalizado, elemento)) {
            return "Ya existe un elemento con ese nombre en el directorio seleccionado.";
        }

        elemento.establecerNombre(nombreNormalizado);
        return null;
    }

    public Proceso solicitarCreacionArchivo(String nombre, String dueno, Directorio padre, int tamano) {
        Archivo nuevoArchivo = new Archivo(nombre, dueno, padre, tamano);
        int posicionSolicitud = estimarPosicionSolicitudCreacion();
        Proceso p = new Proceso(contadorProcesos++, TipoOperacion.CREAR, nuevoArchivo, tamano, posicionSolicitud);
        p.establecerEstado(EstadoProceso.LISTO);
        colaProcesos.encolar(p);
        historialProcesos.agregar(p);
        return p;
    }

    public Proceso despacharSiguienteProceso() {
        Proceso siguiente = seleccionarSiguienteProcesoSegunPolitica();
        if (siguiente == null) {
            return null;
        }

        moverCabezalHasta(siguiente.obtenerPosicionSolicitudDisco());
        ejecutarProceso(siguiente);
        return siguiente;
    }

    public int despacharTodosLosProcesosPendientes() {
        int procesosDespachados = 0;
        
        // Recorremos la cola hasta que quede vacía
        while (!colaProcesos.estaVacia()) {
            Proceso p = colaProcesos.desencolar();
            if (p != null) {
                // 1. Simular el movimiento del cabezal hacia donde está el archivo
                int distancia = Math.abs(posicionCabezal - p.obtenerPosicionSolicitudDisco());
                desplazamientoCabezal += distancia;
                posicionCabezal = p.obtenerPosicionSolicitudDisco();

                // 2. Ejecutar la operación (Leer, Actualizar, Eliminar)
                ejecutarProceso(p);
                procesosDespachados++;
            }
        }
        return procesosDespachados;
    }

    public int obtenerCantidadProcesosPendientes() {
        return colaProcesos.obtenerTamano();
    }

    public void ejecutarProceso(Proceso p) {
        if (p == null) {
            return;
        }

        p.establecerEstado(EstadoProceso.EJECUTANDO);
        p.establecerMensajeResultado("En ejecución en bloque " + p.obtenerPosicionSolicitudDisco());
        
        if (p.obtenerOperacion() == TipoOperacion.CREAR) {
            Archivo archivoCreado = p.obtenerArchivoDestino();
            EntradaJournal entrada = registrarEntradaPendiente(TipoOperacion.CREAR, archivoCreado, null);

            boolean exito = asignarBloquesAArchivo(archivoCreado, p.obtenerId());
            if (exito) {
                if (simularFalloAntesCommit) {
                    p.establecerEstado(EstadoProceso.BLOQUEADO);
                    p.establecerMensajeResultado("Fallo simulado antes del commit. Operación quedó PENDIENTE en journal.");
                    entrada.detalle = "Fallo simulado antes de commit";
                    return;
                }

                confirmarEntradaJournal(entrada, "Commit exitoso CREATE");
                p.establecerEstado(EstadoProceso.TERMINADO);
                p.establecerMensajeResultado("Creación completada y bloques asignados.");
            } else {
                p.establecerEstado(EstadoProceso.BLOQUEADO);
                p.establecerMensajeResultado("Fallo al asignar bloques (espacio insuficiente).");
                entrada.estado = "ABORTADA";
                entrada.detalle = "Fallo por falta de espacio";
            }
        } else {
            Archivo archivoDestino = p.obtenerArchivoDestino();
            if (!intentarAdquirirLock(archivoDestino, p)) {
                p.establecerEstado(EstadoProceso.BLOQUEADO);
                p.establecerMensajeResultado("Archivo bloqueado. Proceso en espera de lock.");
                archivoDestino.obtenerColaEspera().encolar(p);
                return;
            }

            marcarProcesoEnCadenaBloques(archivoDestino, p.obtenerId());
            p.establecerEstado(EstadoProceso.TERMINADO);
            p.establecerMensajeResultado("Completado con lock " + tipoLockDeOperacion(p.obtenerOperacion()));

            if (p.obtenerOperacion() == TipoOperacion.ELIMINAR) {
                SnapshotArchivoEliminado snapshot = crearSnapshotArchivo(archivoDestino);
                EntradaJournal entrada = registrarEntradaPendiente(TipoOperacion.ELIMINAR, archivoDestino, snapshot);

                eliminarElemento(archivoDestino);

                if (simularFalloAntesCommit) {
                    p.establecerEstado(EstadoProceso.BLOQUEADO);
                    p.establecerMensajeResultado("Fallo simulado antes del commit. Eliminación quedó PENDIENTE en journal.");
                    entrada.detalle = "Fallo simulado antes de commit";
                    liberarLock(archivoDestino, p);
                    desbloquearProcesosEnEspera(archivoDestino);
                    return;
                }
                confirmarEntradaJournal(entrada, "Commit exitoso DELETE");
                
            } else if (p.obtenerOperacion() == TipoOperacion.ACTUALIZAR) {
                // Registro visual en Journal para Actualizar
                EntradaJournal entrada = registrarEntradaPendiente(TipoOperacion.ACTUALIZAR, archivoDestino, null);
                if (simularFalloAntesCommit) {
                    p.establecerEstado(EstadoProceso.BLOQUEADO);
                    p.establecerMensajeResultado("Fallo simulado. UPDATE quedó PENDIENTE.");
                    entrada.detalle = "Fallo simulado antes de commit";
                } else {
                    confirmarEntradaJournal(entrada, "Commit exitoso UPDATE");
                }
                
            } else if (p.obtenerOperacion() == TipoOperacion.LEER) {
                // Registro visual en Journal para Lectura (Para que aparezca en el Log)
                EntradaJournal entrada = registrarEntradaPendiente(TipoOperacion.LEER, archivoDestino, null);
                confirmarEntradaJournal(entrada, "Commit exitoso READ");
            }

            liberarLock(archivoDestino, p);
            desbloquearProcesosEnEspera(archivoDestino);
            return;
        }
    }

    private boolean asignarBloquesAArchivo(Archivo archivo, int procesoId) {
        int bloquesNecesarios = archivo.obtenerTamano();
        int bloquesAsignados = 0;
        int bloqueAnterior = -1;
        int primerBloque = -1;

        Lista<Integer> bloquesTemporales = new Lista<>();

        for (int i = 0; i < disco.obtenerCantidadBloques() && bloquesAsignados < bloquesNecesarios; i++) {
            if (!disco.obtenerBloque(i).estaOcupado()) {
                if (bloqueAnterior == -1) {
                    primerBloque = i;
                } else {
                    disco.obtenerBloque(bloqueAnterior).establecerSiguienteBloque(i);
                }
                disco.ocuparBloque(i, archivo.obtenerNombre(), -1, procesoId);
                bloquesTemporales.agregar(i);
                bloqueAnterior = i;
                bloquesAsignados++;
            }
        }

        if (bloquesAsignados == bloquesNecesarios) {
            archivo.establecerBloqueInicial(primerBloque);
            archivo.obtenerPadre().agregarHijo(archivo);
            todosLosArchivos.agregar(archivo);
            return true;
        } else {
            for (int i = 0; i < bloquesTemporales.obtenerTamano(); i++) {
                disco.liberarBloque(bloquesTemporales.obtener(i));
            }
            return false;
        }
    }

    public Archivo buscarArchivoPorNombre(String nombre) {
        for (int i = 0; i < todosLosArchivos.obtenerTamano(); i++) {
            Archivo a = todosLosArchivos.obtener(i);
            if (a.obtenerNombre().equals(nombre)) {
                return a;
            }
        }
        return null;
    }

    private void marcarProcesoEnCadenaBloques(Archivo archivo, int procesoId) {
        if (archivo == null) {
            return;
        }
        int bloqueActual = archivo.obtenerBloqueInicial();
        while (bloqueActual != -1 && bloqueActual < disco.obtenerCantidadBloques()) {
            Bloque bloque = disco.obtenerBloque(bloqueActual);
            bloque.establecerProcesoOcupante(procesoId);
            bloqueActual = bloque.obtenerSiguienteBloque();
        }
    }

    public String obtenerResumenLockArchivo(String nombreArchivo) {
        Archivo archivo = buscarArchivoPorNombre(nombreArchivo);
        if (archivo == null) {
            return "Archivo no encontrado";
        }

        int enEspera = archivo.obtenerColaEspera().obtenerTamano();
        String escritor = archivo.tieneLockEscrituraActivo() ? String.valueOf(archivo.obtenerProcesoEscritor()) : "-";
        return "Lectores=" + archivo.obtenerLectoresActivos() + ", Escritor=" + escritor + ", Espera=" + enEspera;
    }

    public String cambiarVisibilidadArchivo(String nombreArchivo, boolean publico) {
        if (!modoAdministrador) {
            return "Solo el administrador puede cambiar visibilidad de archivos.";
        }

        Archivo archivo = buscarArchivoPorNombre(nombreArchivo);
        if (archivo == null) {
            return "Archivo no encontrado";
        }

        archivo.establecerPublico(publico);
        return null;
    }

    public String eliminarElementoSeguro(ElementoSistema elemento) {
        if (!modoAdministrador) {
            return "Solo el administrador puede eliminar elementos.";
        }
        if (elemento == null) {
            return "Debes seleccionar un elemento para eliminar.";
        }
        eliminarElemento(elemento);
        return null;
    }

    public void eliminarElemento(ElementoSistema elemento) {
        if (elemento instanceof Archivo) {
            Archivo a = (Archivo) elemento;
            int bloqueActual = a.obtenerBloqueInicial();
            while (bloqueActual != -1) {
                int siguiente = disco.obtenerBloque(bloqueActual).obtenerSiguienteBloque();
                disco.liberarBloque(bloqueActual);
                bloqueActual = siguiente;
            }
            todosLosArchivos.eliminar(a);
        } else if (elemento instanceof Directorio) {
            Directorio dir = (Directorio) elemento;
            for (int i = dir.obtenerHijos().obtenerTamano() - 1; i >= 0; i--) {
                eliminarElemento(dir.obtenerHijos().obtener(i));
            }
        }
        if (elemento.obtenerPadre() != null) {
            elemento.obtenerPadre().eliminarHijo(elemento);
        }
    }

    private String validarCreacion(String nombre, Directorio padre, int tamano) {
        String nombreNormalizado = normalizarNombre(nombre);
        if (nombreNormalizado == null) {
            return "El nombre no puede estar vacío.";
        }
        if (padre == null) {
            return "Debes seleccionar un directorio padre válido.";
        }
        if (tamano <= 0) {
            return "El tamaño debe ser un entero positivo.";
        }
        if (existeNombreEnDirectorio(padre, nombreNormalizado, null)) {
            return "Ya existe un elemento con ese nombre en el directorio seleccionado.";
        }
        if (!hayEspacioDisponible(tamano)) {
            return "Espacio insuficiente en el disco para crear el archivo.";
        }
        return null;
    }

    private String validarPermisoOperacionArchivo(Archivo archivo, TipoOperacion operacion) {
        if (modoAdministrador) {
            return null;
        }

        boolean esDueno = archivo.obtenerDueno().equalsIgnoreCase(usuarioActual);
        if (operacion == TipoOperacion.LEER) {
            if (esDueno || archivo.esPublico()) {
                return null;
            }
            return "En modo usuario solo puedes leer archivos propios o públicos.";
        }

        if (operacion == TipoOperacion.ACTUALIZAR || operacion == TipoOperacion.ELIMINAR) {
            return "En modo usuario solo se permite lectura.";
        }

        return "Operación no permitida para el modo actual.";
    }

    private String normalizarNombre(String nombre) {
        if (nombre == null) {
            return null;
        }
        String limpio = nombre.trim();
        if (limpio.isEmpty()) {
            return null;
        }
        return limpio;
    }

    private boolean existeNombreEnDirectorio(Directorio directorio, String nombre, ElementoSistema excluido) {
        for (int i = 0; i < directorio.obtenerHijos().obtenerTamano(); i++) {
            ElementoSistema hijo = directorio.obtenerHijos().obtener(i);
            if (hijo == excluido) {
                continue;
            }
            if (hijo.obtenerNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    private boolean hayEspacioDisponible(int bloquesNecesarios) {
        return contarBloquesLibres() >= bloquesNecesarios;
    }

    private int estimarPosicionSolicitudCreacion() {
        int bloqueLibre = disco.buscarBloqueLibre();
        if (bloqueLibre == -1) {
            return posicionCabezal;
        }
        return bloqueLibre;
    }

    private Proceso seleccionarSiguienteProcesoSegunPolitica() {
        Lista<Proceso> pendientes = extraerPendientesALista();
        int totalPendientes = pendientes.obtenerTamano();
        if (totalPendientes == 0) {
            return null;
        }

        int indiceSeleccionado;
        switch (politicaActiva) {
            case SSTF:
                indiceSeleccionado = seleccionarIndiceSstf(pendientes);
                break;
            case SCAN:
                indiceSeleccionado = seleccionarIndiceScan(pendientes);
                break;
            case C_SCAN:
                indiceSeleccionado = seleccionarIndiceCscan(pendientes);
                break;
            case FIFO:
            default:
                indiceSeleccionado = 0;
                break;
        }

        Proceso seleccionado = pendientes.obtener(indiceSeleccionado);
        reencolarTodosExceptoIndice(pendientes, indiceSeleccionado);
        return seleccionado;
    }

    private Lista<Proceso> extraerPendientesALista() {
        Lista<Proceso> pendientes = new Lista<>();
        while (!colaProcesos.estaVacia()) {
            Proceso proceso = colaProcesos.desencolar();
            if (proceso != null) {
                pendientes.agregar(proceso);
            }
        }
        return pendientes;
    }

    private void reencolarTodosExceptoIndice(Lista<Proceso> pendientes, int indiceOmitido) {
        for (int i = 0; i < pendientes.obtenerTamano(); i++) {
            if (i == indiceOmitido) {
                continue;
            }
            colaProcesos.encolar(pendientes.obtener(i));
        }
    }

    private int seleccionarIndiceSstf(Lista<Proceso> pendientes) {
        int indiceSeleccionado = 0;
        int mejorDistancia = Integer.MAX_VALUE;
        int mejorPosicion = Integer.MAX_VALUE;

        for (int i = 0; i < pendientes.obtenerTamano(); i++) {
            int posicion = normalizarPosicion(pendientes.obtener(i).obtenerPosicionSolicitudDisco());
            int distancia = Math.abs(posicion - posicionCabezal);
            if (distancia < mejorDistancia || (distancia == mejorDistancia && posicion < mejorPosicion)) {
                mejorDistancia = distancia;
                mejorPosicion = posicion;
                indiceSeleccionado = i;
            }
        }

        return indiceSeleccionado;
    }

    private int seleccionarIndiceScan(Lista<Proceso> pendientes) {
        int indiceEnDireccion = buscarIndiceMasCercanoEnDireccion(pendientes, direccionAscendente);
        if (indiceEnDireccion != -1) {
            return indiceEnDireccion;
        }

        direccionAscendente = !direccionAscendente;
        return buscarIndiceMasCercanoEnDireccion(pendientes, direccionAscendente);
    }

    private int seleccionarIndiceCscan(Lista<Proceso> pendientes) {
        int indiceEnDireccion = buscarIndiceMasCercanoEnDireccion(pendientes, direccionAscendente);
        if (indiceEnDireccion != -1) {
            return indiceEnDireccion;
        }

        if (direccionAscendente) {
            desplazamientoCabezal += (maximoIndiceDisco - posicionCabezal);
            desplazamientoCabezal += maximoIndiceDisco;
            posicionCabezal = 0;
        } else {
            desplazamientoCabezal += posicionCabezal;
            desplazamientoCabezal += maximoIndiceDisco;
            posicionCabezal = maximoIndiceDisco;
        }

        return buscarIndiceMasCercanoEnDireccion(pendientes, direccionAscendente);
    }

    private int buscarIndiceMasCercanoEnDireccion(Lista<Proceso> pendientes, boolean direccionAscendente) {
        int indiceSeleccionado = -1;
        int mejorDistancia = Integer.MAX_VALUE;

        for (int i = 0; i < pendientes.obtenerTamano(); i++) {
            int posicion = normalizarPosicion(pendientes.obtener(i).obtenerPosicionSolicitudDisco());
            int delta = posicion - posicionCabezal;

            if (direccionAscendente && delta < 0) {
                continue;
            }
            if (!direccionAscendente && delta > 0) {
                continue;
            }

            int distancia = Math.abs(delta);
            if (distancia < mejorDistancia) {
                mejorDistancia = distancia;
                indiceSeleccionado = i;
            }
        }

        return indiceSeleccionado;
    }

    private void moverCabezalHasta(int nuevaPosicion) {
        int destino = normalizarPosicion(nuevaPosicion);
        desplazamientoCabezal += Math.abs(destino - posicionCabezal);
        posicionCabezal = destino;
    }

    private int normalizarPosicion(int posicion) {
        if (posicion < 0) {
            return 0;
        }
        if (posicion > maximoIndiceDisco) {
            return maximoIndiceDisco;
        }
        return posicion;
    }

    private int contarBloquesLibres() {
        int libres = 0;
        for (int i = 0; i < disco.obtenerCantidadBloques(); i++) {
            if (!disco.obtenerBloque(i).estaOcupado()) {
                libres++;
            }
        }
        return libres;
    }

    private Archivo obtenerUltimoArchivoCreado() {
        for (int i = historialProcesos.obtenerTamano() - 1; i >= 0; i--) {
            Proceso proceso = historialProcesos.obtener(i);
            if (proceso.obtenerOperacion() == TipoOperacion.CREAR) {
                return proceso.obtenerArchivoDestino();
            }
        }
        return null;
    }

    private String construirJsonEstado() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"version\":1,\n");
        sb.append("  \"cantidadBloques\":").append(disco.obtenerCantidadBloques()).append(",\n");
        sb.append("  \"sesion\":{\"usuario\":\"").append(escaparJson(usuarioActual)).append("\",\"admin\":").append(modoAdministrador).append("},\n");
        sb.append("  \"planificador\":{\"politica\":\"").append(politicaActiva.name()).append("\",\"cabezal\":").append(posicionCabezal)
            .append(",\"desplazamiento\":").append(desplazamientoCabezal).append(",\"asc\":").append(direccionAscendente).append("},\n");

        Lista<Directorio> directorios = new Lista<>();
        recolectarDirectorios(raiz, directorios);
        sb.append("  \"directorios\":[\n");
        for (int i = 0; i < directorios.obtenerTamano(); i++) {
            Directorio dir = directorios.obtener(i);
            String ruta = construirRutaElemento(dir);
            String rutaPadre = dir.obtenerPadre() == null ? "null" : "\"" + escaparJson(construirRutaElemento(dir.obtenerPadre())) + "\"";
            sb.append("    {\"ruta\":\"").append(escaparJson(ruta)).append("\",\"nombre\":\"").append(escaparJson(dir.obtenerNombre()))
                .append("\",\"dueno\":\"").append(escaparJson(dir.obtenerDueno())).append("\",\"publico\":").append(dir.esPublico())
                .append(",\"padre\":").append(rutaPadre).append("}");
            if (i < directorios.obtenerTamano() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ],\n");

        sb.append("  \"archivos\":[\n");
        for (int i = 0; i < todosLosArchivos.obtenerTamano(); i++) {
            Archivo archivo = todosLosArchivos.obtener(i);
            String ruta = construirRutaElemento(archivo);
            String rutaPadre = archivo.obtenerPadre() == null ? "null" : "\"" + escaparJson(construirRutaElemento(archivo.obtenerPadre())) + "\"";
            sb.append("    {\"ruta\":\"").append(escaparJson(ruta)).append("\",\"nombre\":\"").append(escaparJson(archivo.obtenerNombre()))
                .append("\",\"dueno\":\"").append(escaparJson(archivo.obtenerDueno())).append("\",\"publico\":").append(archivo.esPublico())
                .append(",\"tamano\":").append(archivo.obtenerTamano()).append(",\"bloqueInicial\":").append(archivo.obtenerBloqueInicial())
                .append(",\"padre\":").append(rutaPadre).append("}");
            if (i < todosLosArchivos.obtenerTamano() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ],\n");

        sb.append("  \"bloques\":[\n");
        for (int i = 0; i < disco.obtenerCantidadBloques(); i++) {
            Bloque b = disco.obtenerBloque(i);
            sb.append("    {\"id\":").append(b.obtenerId()).append(",\"ocupado\":").append(b.estaOcupado())
                .append(",\"archivo\":\"").append(escaparJson(b.obtenerNombreArchivo())).append("\",\"siguiente\":").append(b.obtenerSiguienteBloque())
                .append(",\"proceso\":").append(b.obtenerProcesoOcupante()).append("}");
            if (i < disco.obtenerCantidadBloques() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ],\n");

        sb.append("  \"journal\":[\n");
        for (int i = 0; i < journal.obtenerTamano(); i++) {
            EntradaJournal e = journal.obtener(i);
            sb.append("    {\"id\":").append(e.id)
                .append(",\"operacion\":\"").append(escaparJson(e.operacion)).append("\"")
                .append(",\"estado\":\"").append(escaparJson(e.estado)).append("\"")
                .append(",\"ruta\":\"").append(escaparJson(e.rutaArchivo)).append("\"")
                .append(",\"padre\":").append(e.padreRuta == null ? "null" : "\"" + escaparJson(e.padreRuta) + "\"")
                .append(",\"nombre\":\"").append(escaparJson(e.nombreArchivo)).append("\"")
                .append(",\"dueno\":\"").append(escaparJson(e.duenoArchivo)).append("\"")
                .append(",\"publico\":").append(e.publico)
                .append(",\"tamano\":").append(e.tamano)
                .append(",\"bloqueInicial\":").append(e.bloqueInicial)
                .append(",\"bloques\":\"").append(escaparJson(e.cadenaBloques)).append("\"")
                .append(",\"detalle\":\"").append(escaparJson(e.detalle)).append("\"}");
            if (i < journal.obtenerTamano() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private void restaurarDesdeJson(String json) {
        int cantidadBloques = extraerEntero(json, "cantidadBloques", disco.obtenerCantidadBloques());
        String usuario = extraerCadena(json, "usuario", "admin");
        boolean admin = extraerBooleano(json, "admin", true);
        String politicaTexto = extraerCadena(json, "politica", PoliticaPlanificacion.FIFO.name());
        int cabezal = extraerEntero(json, "cabezal", 0);
        int desplazamiento = extraerEntero(json, "desplazamiento", 0);
        boolean asc = extraerBooleano(json, "asc", true);

        reinicializarEstado(cantidadBloques);

        Lista<RegistroDirectorio> directorios = parsearDirectorios(extraerSeccionArreglo(json, "directorios"));
        Lista<RutaDirectorio> rutas = construirDirectoriosDesdeRegistros(directorios);

        Lista<RegistroArchivo> archivos = parsearArchivos(extraerSeccionArreglo(json, "archivos"));
        construirArchivosDesdeRegistros(archivos, rutas);

        Lista<RegistroBloque> bloques = parsearBloques(extraerSeccionArreglo(json, "bloques"));
        aplicarBloques(bloques);

        this.journal = parsearJournal(extraerSeccionArreglo(json, "journal"));
        this.contadorTransacciones = calcularSiguienteTransaccion();

        configurarSesion(usuario, admin);
        PoliticaPlanificacion politica = parsearPolitica(politicaTexto);
        configurarPlanificador(politica, cabezal, asc);
        this.desplazamientoCabezal = desplazamiento;
    }

    private void reinicializarEstado(int cantidadBloques) {
        this.disco = new DiscoVirtual(cantidadBloques);
        this.raiz = new Directorio("Raiz", "admin", null);
        this.colaProcesos = new Cola<>();
        this.historialProcesos = new Lista<>();
        this.todosLosArchivos = new Lista<>();
        this.contadorProcesos = 1;
        this.maximoIndiceDisco = Math.max(0, cantidadBloques - 1);
    }

    private Lista<RegistroDirectorio> parsearDirectorios(String arregloJson) {
        Lista<RegistroDirectorio> resultado = new Lista<>();
        Lista<String> objetos = dividirObjetos(arregloJson);
        for (int i = 0; i < objetos.obtenerTamano(); i++) {
            String obj = objetos.obtener(i);
            RegistroDirectorio reg = new RegistroDirectorio();
            reg.ruta = extraerCadena(obj, "ruta", "/Raiz");
            reg.nombre = extraerCadena(obj, "nombre", "Raiz");
            reg.dueno = extraerCadena(obj, "dueno", "admin");
            reg.publico = extraerBooleano(obj, "publico", false);
            reg.padre = extraerCadenaNullable(obj, "padre");
            resultado.agregar(reg);
        }
        return resultado;
    }

    private Lista<RegistroArchivo> parsearArchivos(String arregloJson) {
        Lista<RegistroArchivo> resultado = new Lista<>();
        Lista<String> objetos = dividirObjetos(arregloJson);
        for (int i = 0; i < objetos.obtenerTamano(); i++) {
            String obj = objetos.obtener(i);
            RegistroArchivo reg = new RegistroArchivo();
            reg.ruta = extraerCadena(obj, "ruta", "");
            reg.nombre = extraerCadena(obj, "nombre", "");
            reg.dueno = extraerCadena(obj, "dueno", "admin");
            reg.publico = extraerBooleano(obj, "publico", false);
            reg.tamano = extraerEntero(obj, "tamano", 1);
            reg.bloqueInicial = extraerEntero(obj, "bloqueInicial", -1);
            reg.padre = extraerCadenaNullable(obj, "padre");
            resultado.agregar(reg);
        }
        return resultado;
    }

    private Lista<RegistroBloque> parsearBloques(String arregloJson) {
        Lista<RegistroBloque> resultado = new Lista<>();
        Lista<String> objetos = dividirObjetos(arregloJson);
        for (int i = 0; i < objetos.obtenerTamano(); i++) {
            String obj = objetos.obtener(i);
            RegistroBloque reg = new RegistroBloque();
            reg.id = extraerEntero(obj, "id", i);
            reg.ocupado = extraerBooleano(obj, "ocupado", false);
            reg.archivo = extraerCadena(obj, "archivo", "");
            reg.siguiente = extraerEntero(obj, "siguiente", -1);
            reg.proceso = extraerEntero(obj, "proceso", -1);
            resultado.agregar(reg);
        }
        return resultado;
    }

    private Lista<EntradaJournal> parsearJournal(String arregloJson) {
        Lista<EntradaJournal> resultado = new Lista<>();
        Lista<String> objetos = dividirObjetos(arregloJson);
        for (int i = 0; i < objetos.obtenerTamano(); i++) {
            String obj = objetos.obtener(i);
            EntradaJournal e = new EntradaJournal();
            e.id = extraerEntero(obj, "id", i + 1);
            e.operacion = extraerCadena(obj, "operacion", TipoOperacion.CREAR.name());
            e.estado = extraerCadena(obj, "estado", "CONFIRMADA");
            e.rutaArchivo = extraerCadena(obj, "ruta", "");
            e.padreRuta = extraerCadenaNullable(obj, "padre");
            e.nombreArchivo = extraerCadena(obj, "nombre", "");
            e.duenoArchivo = extraerCadena(obj, "dueno", "admin");
            e.publico = extraerBooleano(obj, "publico", false);
            e.tamano = extraerEntero(obj, "tamano", 0);
            e.bloqueInicial = extraerEntero(obj, "bloqueInicial", -1);
            e.cadenaBloques = extraerCadena(obj, "bloques", "");
            e.detalle = extraerCadena(obj, "detalle", "");
            resultado.agregar(e);
        }
        return resultado;
    }

    private Lista<RutaDirectorio> construirDirectoriosDesdeRegistros(Lista<RegistroDirectorio> registros) {
        Lista<RutaDirectorio> rutas = new Lista<>();

        RegistroDirectorio rootReg = encontrarRegistroRaiz(registros);
        this.raiz = new Directorio(rootReg.nombre, rootReg.dueno, null);
        this.raiz.establecerPublico(rootReg.publico);

        RutaDirectorio raizRuta = new RutaDirectorio();
        raizRuta.ruta = rootReg.ruta;
        raizRuta.directorio = this.raiz;
        rutas.agregar(raizRuta);

        boolean progreso = true;
        while (progreso) {
            progreso = false;
            for (int i = 0; i < registros.obtenerTamano(); i++) {
                RegistroDirectorio reg = registros.obtener(i);
                if (reg.padre == null) {
                    continue;
                }
                if (buscarDirectorioPorRuta(rutas, reg.ruta) != null) {
                    continue;
                }

                Directorio padre = buscarDirectorioPorRuta(rutas, reg.padre);
                if (padre == null) {
                    continue;
                }

                Directorio nuevo = new Directorio(reg.nombre, reg.dueno, padre);
                nuevo.establecerPublico(reg.publico);
                padre.agregarHijo(nuevo);

                RutaDirectorio map = new RutaDirectorio();
                map.ruta = reg.ruta;
                map.directorio = nuevo;
                rutas.agregar(map);
                progreso = true;
            }
        }

        return rutas;
    }

    private void construirArchivosDesdeRegistros(Lista<RegistroArchivo> registros, Lista<RutaDirectorio> rutas) {
        for (int i = 0; i < registros.obtenerTamano(); i++) {
            RegistroArchivo reg = registros.obtener(i);
            Directorio padre = reg.padre == null ? raiz : buscarDirectorioPorRuta(rutas, reg.padre);
            if (padre == null) {
                padre = raiz;
            }

            Archivo archivo = new Archivo(reg.nombre, reg.dueno, padre, reg.tamano);
            archivo.establecerPublico(reg.publico);
            archivo.establecerBloqueInicial(reg.bloqueInicial);
            padre.agregarHijo(archivo);
            todosLosArchivos.agregar(archivo);
        }
    }

    private void aplicarBloques(Lista<RegistroBloque> bloques) {
        for (int i = 0; i < disco.obtenerCantidadBloques(); i++) {
            disco.liberarBloque(i);
        }

        for (int i = 0; i < bloques.obtenerTamano(); i++) {
            RegistroBloque reg = bloques.obtener(i);
            if (reg.id < 0 || reg.id >= disco.obtenerCantidadBloques()) {
                continue;
            }
            if (reg.ocupado) {
                disco.ocuparBloque(reg.id, reg.archivo, reg.siguiente, reg.proceso);
            } else {
                disco.liberarBloque(reg.id);
            }
        }
    }

    private RegistroDirectorio encontrarRegistroRaiz(Lista<RegistroDirectorio> registros) {
        for (int i = 0; i < registros.obtenerTamano(); i++) {
            RegistroDirectorio reg = registros.obtener(i);
            if (reg.padre == null) {
                return reg;
            }
        }
        RegistroDirectorio fallback = new RegistroDirectorio();
        fallback.ruta = "/Raiz";
        fallback.nombre = "Raiz";
        fallback.dueno = "admin";
        fallback.publico = false;
        fallback.padre = null;
        return fallback;
    }

    private Directorio buscarDirectorioPorRuta(Lista<RutaDirectorio> rutas, String ruta) {
        for (int i = 0; i < rutas.obtenerTamano(); i++) {
            RutaDirectorio item = rutas.obtener(i);
            if (item.ruta.equals(ruta)) {
                return item.directorio;
            }
        }
        return null;
    }

    private void recolectarDirectorios(Directorio actual, Lista<Directorio> acumulado) {
        acumulado.agregar(actual);
        for (int i = 0; i < actual.obtenerHijos().obtenerTamano(); i++) {
            ElementoSistema hijo = actual.obtenerHijos().obtener(i);
            if (hijo instanceof Directorio) {
                recolectarDirectorios((Directorio) hijo, acumulado);
            }
        }
    }

    private String construirRutaElemento(ElementoSistema elemento) {
        if (elemento == null) {
            return "";
        }
        if (elemento.obtenerPadre() == null) {
            return "/" + elemento.obtenerNombre();
        }
        return construirRutaElemento(elemento.obtenerPadre()) + "/" + elemento.obtenerNombre();
    }

    private String escaparJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String desescaparJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private String extraerSeccionArreglo(String json, String clave) {
        int idx = json.indexOf("\"" + clave + "\"");
        if (idx == -1) {
            return "[]";
        }
        int inicio = json.indexOf('[', idx);
        if (inicio == -1) {
            return "[]";
        }
        int fin = buscarCierre(json, inicio, '[', ']');
        if (fin == -1) {
            return "[]";
        }
        return json.substring(inicio, fin + 1);
    }

    private int buscarCierre(String texto, int inicio, char abre, char cierra) {
        int nivel = 0;
        boolean enCadena = false;
        for (int i = inicio; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '"' && (i == 0 || texto.charAt(i - 1) != '\\')) {
                enCadena = !enCadena;
                continue;
            }
            if (enCadena) {
                continue;
            }
            if (c == abre) {
                nivel++;
            } else if (c == cierra) {
                nivel--;
                if (nivel == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Lista<String> dividirObjetos(String arregloJson) {
        Lista<String> objetos = new Lista<>();
        if (arregloJson == null || arregloJson.length() < 2) {
            return objetos;
        }

        boolean enCadena = false;
        int nivel = 0;
        int inicioObj = -1;
        for (int i = 0; i < arregloJson.length(); i++) {
            char c = arregloJson.charAt(i);
            if (c == '"' && (i == 0 || arregloJson.charAt(i - 1) != '\\')) {
                enCadena = !enCadena;
                continue;
            }
            if (enCadena) {
                continue;
            }
            if (c == '{') {
                if (nivel == 0) {
                    inicioObj = i;
                }
                nivel++;
            } else if (c == '}') {
                nivel--;
                if (nivel == 0 && inicioObj != -1) {
                    objetos.agregar(arregloJson.substring(inicioObj, i + 1));
                    inicioObj = -1;
                }
            }
        }

        return objetos;
    }

    private String extraerCadena(String json, String clave, String valorDefecto) {
        Pattern p = Pattern.compile("\\\"" + Pattern.quote(clave) + "\\\"\\s*:\\s*\\\"(.*?)\\\"");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return desescaparJson(m.group(1));
        }
        return valorDefecto;
    }

    private String extraerCadenaNullable(String json, String clave) {
        Pattern nullPattern = Pattern.compile("\\\"" + Pattern.quote(clave) + "\\\"\\s*:\\s*null");
        Matcher nullMatcher = nullPattern.matcher(json);
        if (nullMatcher.find()) {
            return null;
        }
        return extraerCadena(json, clave, null);
    }

    private int extraerEntero(String json, String clave, int valorDefecto) {
        Pattern p = Pattern.compile("\\\"" + Pattern.quote(clave) + "\\\"\\s*:\\s*(-?\\d+)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return valorDefecto;
    }

    private boolean extraerBooleano(String json, String clave, boolean valorDefecto) {
        Pattern p = Pattern.compile("\\\"" + Pattern.quote(clave) + "\\\"\\s*:\\s*(true|false)");
        Matcher m = p.matcher(json);
        if (m.find()) {
            return Boolean.parseBoolean(m.group(1));
        }
        return valorDefecto;
    }

    private PoliticaPlanificacion parsearPolitica(String politicaTexto) {
        for (PoliticaPlanificacion p : PoliticaPlanificacion.values()) {
            if (p.name().equalsIgnoreCase(politicaTexto)) {
                return p;
            }
        }
        return PoliticaPlanificacion.FIFO;
    }

    private static class RegistroDirectorio {
        String ruta;
        String nombre;
        String dueno;
        boolean publico;
        String padre;
    }

    private static class RegistroArchivo {
        String ruta;
        String nombre;
        String dueno;
        boolean publico;
        int tamano;
        int bloqueInicial;
        String padre;
    }

    private static class RegistroBloque {
        int id;
        boolean ocupado;
        String archivo;
        int siguiente;
        int proceso;
    }

    private static class RutaDirectorio {
        String ruta;
        Directorio directorio;
    }

    private static class EntradaJournal {
        int id;
        String operacion;
        String estado;
        String rutaArchivo;
        String padreRuta;
        String nombreArchivo;
        String duenoArchivo;
        boolean publico;
        int tamano;
        int bloqueInicial;
        String cadenaBloques;
        String detalle;
    }

    private static class SnapshotArchivoEliminado {
        String rutaArchivo;
        String padreRuta;
        String nombre;
        String dueno;
        boolean publico;
        int tamano;
        int bloqueInicial;
        String cadenaBloques;
    }

    private EntradaJournal registrarEntradaPendiente(TipoOperacion operacion, Archivo archivo, SnapshotArchivoEliminado snapshot) {
        EntradaJournal entrada = new EntradaJournal();
        entrada.id = contadorTransacciones++;
        entrada.operacion = operacion.name();
        entrada.estado = "PENDIENTE";

        if (archivo != null) {
            entrada.rutaArchivo = construirRutaElemento(archivo);
            entrada.padreRuta = archivo.obtenerPadre() == null ? null : construirRutaElemento(archivo.obtenerPadre());
            entrada.nombreArchivo = archivo.obtenerNombre();
            entrada.duenoArchivo = archivo.obtenerDueno();
            entrada.publico = archivo.esPublico();
            entrada.tamano = archivo.obtenerTamano();
            entrada.bloqueInicial = archivo.obtenerBloqueInicial();
        }

        if (snapshot != null) {
            entrada.rutaArchivo = snapshot.rutaArchivo;
            entrada.padreRuta = snapshot.padreRuta;
            entrada.nombreArchivo = snapshot.nombre;
            entrada.duenoArchivo = snapshot.dueno;
            entrada.publico = snapshot.publico;
            entrada.tamano = snapshot.tamano;
            entrada.bloqueInicial = snapshot.bloqueInicial;
            entrada.cadenaBloques = snapshot.cadenaBloques;
        }

        if (entrada.cadenaBloques == null) {
            entrada.cadenaBloques = "";
        }
        entrada.detalle = "Registrada como pendiente";
        journal.agregar(entrada);
        return entrada;
    }

    private void confirmarEntradaJournal(EntradaJournal entrada, String detalle) {
        if (entrada == null) {
            return;
        }
        entrada.estado = "CONFIRMADA";
        entrada.detalle = detalle;
    }

    private void abortarEntradaJournal(EntradaJournal entrada, String detalle) {
        if (entrada == null) {
            return;
        }
        entrada.estado = "ABORTADA";
        entrada.detalle = detalle;
    }

    private int calcularSiguienteTransaccion() {
        int max = 0;
        for (int i = 0; i < journal.obtenerTamano(); i++) {
            EntradaJournal e = journal.obtener(i);
            if (e.id > max) {
                max = e.id;
            }
        }
        return max + 1;
    }

    private SnapshotArchivoEliminado crearSnapshotArchivo(Archivo archivo) {
        SnapshotArchivoEliminado snapshot = new SnapshotArchivoEliminado();
        snapshot.rutaArchivo = construirRutaElemento(archivo);
        snapshot.padreRuta = archivo.obtenerPadre() == null ? null : construirRutaElemento(archivo.obtenerPadre());
        snapshot.nombre = archivo.obtenerNombre();
        snapshot.dueno = archivo.obtenerDueno();
        snapshot.publico = archivo.esPublico();
        snapshot.tamano = archivo.obtenerTamano();
        snapshot.bloqueInicial = archivo.obtenerBloqueInicial();
        snapshot.cadenaBloques = serializarCadenaBloques(archivo.obtenerBloqueInicial());
        return snapshot;
    }

    private String serializarCadenaBloques(int bloqueInicial) {
        if (bloqueInicial < 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int actual = bloqueInicial;
        while (actual != -1 && actual < disco.obtenerCantidadBloques()) {
            Bloque b = disco.obtenerBloque(actual);
            sb.append(actual).append(":").append(b.obtenerSiguienteBloque());
            actual = b.obtenerSiguienteBloque();
            if (actual != -1) {
                sb.append(";");
            }
        }
        return sb.toString();
    }

    private void restaurarCadenaBloques(String cadena, String nombreArchivo) {
        if (cadena == null || cadena.isEmpty()) {
            return;
        }

        String[] partes = cadena.split(";");
        for (String parte : partes) {
            String[] tokens = parte.split(":");
            if (tokens.length != 2) {
                continue;
            }
            int id = Integer.parseInt(tokens[0]);
            int sig = Integer.parseInt(tokens[1]);
            if (id >= 0 && id < disco.obtenerCantidadBloques()) {
                disco.ocuparBloque(id, nombreArchivo, sig);
            }
        }
    }

    private void deshacerCreatePendiente(EntradaJournal entrada) {
        ElementoSistema elemento = buscarElementoPorRuta(entrada.rutaArchivo);
        if (elemento instanceof Archivo) {
            eliminarElemento(elemento);
        }
    }

    private void deshacerDeletePendiente(EntradaJournal entrada) {
        if (buscarElementoPorRuta(entrada.rutaArchivo) != null) {
            return;
        }

        Directorio padre = entrada.padreRuta == null ? raiz : buscarDirectorioPorRutaEnArbol(entrada.padreRuta);
        if (padre == null) {
            padre = raiz;
        }

        Archivo archivo = new Archivo(entrada.nombreArchivo, entrada.duenoArchivo, padre, Math.max(1, entrada.tamano));
        archivo.establecerPublico(entrada.publico);
        archivo.establecerBloqueInicial(entrada.bloqueInicial);
        padre.agregarHijo(archivo);
        todosLosArchivos.agregar(archivo);

        restaurarCadenaBloques(entrada.cadenaBloques, entrada.nombreArchivo);
    }

    private ElementoSistema buscarElementoPorRuta(String ruta) {
        if (ruta == null || ruta.isEmpty()) {
            return null;
        }
        if (ruta.equals("/" + raiz.obtenerNombre())) {
            return raiz;
        }

        String[] partes = ruta.split("/");
        Directorio actual = raiz;
        for (int i = 2; i < partes.length; i++) {
            String segmento = partes[i];
            ElementoSistema encontrado = null;
            for (int j = 0; j < actual.obtenerHijos().obtenerTamano(); j++) {
                ElementoSistema hijo = actual.obtenerHijos().obtener(j);
                if (hijo.obtenerNombre().equals(segmento)) {
                    encontrado = hijo;
                    break;
                }
            }
            if (encontrado == null) {
                return null;
            }
            if (i == partes.length - 1) {
                return encontrado;
            }
            if (!(encontrado instanceof Directorio)) {
                return null;
            }
            actual = (Directorio) encontrado;
        }
        return null;
    }

    private Directorio buscarDirectorioPorRutaEnArbol(String ruta) {
        ElementoSistema elemento = buscarElementoPorRuta(ruta);
        if (elemento instanceof Directorio) {
            return (Directorio) elemento;
        }
        return null;
    }

    private boolean intentarAdquirirLock(Archivo archivo, Proceso proceso) {
        TipoOperacion operacion = proceso.obtenerOperacion();

        if (operacion == TipoOperacion.LEER) {
            if (archivo.tieneConflictoLectura()) {
                return false;
            }
            archivo.tomarLockLectura();
            return true;
        }

        if (operacion == TipoOperacion.ACTUALIZAR || operacion == TipoOperacion.ELIMINAR) {
            if (!archivo.puedeTomarLockEscritura()) {
                return false;
            }
            archivo.tomarLockEscritura(proceso.obtenerId());
            return true;
        }

        return false;
    }

    private void liberarLock(Archivo archivo, Proceso proceso) {
        TipoOperacion operacion = proceso.obtenerOperacion();
        if (operacion == TipoOperacion.LEER) {
            archivo.liberarLockLectura();
            return;
        }

        if (operacion == TipoOperacion.ACTUALIZAR || operacion == TipoOperacion.ELIMINAR) {
            archivo.liberarLockEscritura(proceso.obtenerId());
        }
    }

    private void desbloquearProcesosEnEspera(Archivo archivo) {
        Cola<Proceso> espera = archivo.obtenerColaEspera();
        while (!espera.estaVacia()) {
            Proceso procesoEnEspera = espera.desencolar();
            if (procesoEnEspera == null) {
                continue;
            }
            procesoEnEspera.establecerEstado(EstadoProceso.LISTO);
            procesoEnEspera.establecerMensajeResultado("Reintentando operación tras liberar lock.");
            colaProcesos.encolar(procesoEnEspera);
        }
    }

    private String tipoLockDeOperacion(TipoOperacion operacion) {
        if (operacion == TipoOperacion.LEER) {
            return "compartido";
        }
        if (operacion == TipoOperacion.ACTUALIZAR || operacion == TipoOperacion.ELIMINAR) {
            return "exclusivo";
        }
        return "N/A";
    }
}