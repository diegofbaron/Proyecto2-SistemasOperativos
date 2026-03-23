/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package modelo;

import estructuras.Cola;
import estructuras.Lista;

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

    public String crearArchivo(String nombre, String dueno, Directorio padre, int tamano) {
        String errorValidacion = validarCreacion(nombre, padre, tamano);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        Proceso p = solicitarCreacionArchivo(nombre.trim(), dueno, padre, tamano);
        despacharSiguienteProceso();

        if (p.obtenerEstado() == EstadoProceso.BLOQUEADO) {
            return p.obtenerMensajeResultado();
        }

        return null;
    }

    public String crearDirectorio(String nombre, String dueno, Directorio padre) {
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

        Directorio nuevoDir = new Directorio(nombreNormalizado, dueno, padre);
        padre.agregarHijo(nuevoDir);
        return null;
    }

    public String renombrarElemento(ElementoSistema elemento, String nuevoNombre) {
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
        int procesosEjecutados = 0;
        while (!colaProcesos.estaVacia()) {
            Proceso ejecutado = despacharSiguienteProceso();
            if (ejecutado != null) {
                procesosEjecutados++;
            }
        }
        return procesosEjecutados;
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
            boolean exito = asignarBloquesAArchivo(p.obtenerArchivoDestino());
            if (exito) {
                p.establecerEstado(EstadoProceso.TERMINADO);
                p.establecerMensajeResultado("Completado");
            } else {
                p.establecerEstado(EstadoProceso.BLOQUEADO);
                p.establecerMensajeResultado("No hay suficientes bloques disponibles para crear el archivo.");
            }
            return;
        }

        p.establecerEstado(EstadoProceso.BLOQUEADO);
        p.establecerMensajeResultado("Operación no soportada por el despachador actual.");
    }

    private boolean asignarBloquesAArchivo(Archivo archivo) {
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
                disco.ocuparBloque(i, archivo.obtenerNombre(), -1);
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
}