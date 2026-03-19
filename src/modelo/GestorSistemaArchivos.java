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
    private Lista<Archivo> todosLosArchivos;
    private int contadorProcesos;

    public GestorSistemaArchivos(int cantidadBloquesDisco) {
        this.disco = new DiscoVirtual(cantidadBloquesDisco);
        this.raiz = new Directorio("Raiz", "admin", null);
        this.colaProcesos = new Cola<>();
        this.todosLosArchivos = new Lista<>();
        this.contadorProcesos = 1;
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

    public Lista<Archivo> obtenerTodosLosArchivos() {
        return todosLosArchivos;
    }

    public Proceso solicitarCreacionArchivo(String nombre, String dueno, Directorio padre, int tamano) {
        Archivo nuevoArchivo = new Archivo(nombre, dueno, padre, tamano);
        Proceso p = new Proceso(contadorProcesos++, TipoOperacion.CREAR, nuevoArchivo, tamano);
        colaProcesos.encolar(p);
        return p;
    }

    public void ejecutarProceso(Proceso p) {
        p.establecerEstado(EstadoProceso.EJECUTANDO);
        
        if (p.obtenerOperacion() == TipoOperacion.CREAR) {
            boolean exito = asignarBloquesAArchivo(p.obtenerArchivoDestino());
            if (exito) {
                p.establecerEstado(EstadoProceso.TERMINADO);
            } else {
                p.establecerEstado(EstadoProceso.BLOQUEADO);
            }
        }
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
}
