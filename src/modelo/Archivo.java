/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import estructuras.Cola;
import java.awt.Color;

public class Archivo extends ElementoSistema {
    private int tamanoBloques;
    private int bloqueInicial;
    private int lectoresActivos;
    private boolean lockEscrituraActivo;
    private int procesoEscritor;
    private Cola<Proceso> colaEspera;
    private Color color;

    public Archivo(String nombre, String dueno, Directorio padre, int tamanoBloques) {
        super(nombre, dueno, padre);
        this.tamanoBloques = tamanoBloques;
        this.bloqueInicial = -1;
        this.lectoresActivos = 0;
        this.lockEscrituraActivo = false;
        this.procesoEscritor = -1;
        this.colaEspera = new Cola<>();
        this.color = new Color((int)(Math.random() * 200), (int)(Math.random() * 200), (int)(Math.random() * 200));
    }

    @Override
    public int obtenerTamano() {
        return tamanoBloques;
    }

    public int obtenerBloqueInicial() {
        return bloqueInicial;
    }

    public void establecerBloqueInicial(int bloqueInicial) {
        this.bloqueInicial = bloqueInicial;
    }

    public boolean tieneConflictoLectura() {
        return lockEscrituraActivo;
    }

    public boolean puedeTomarLockEscritura() {
        return !lockEscrituraActivo && lectoresActivos == 0;
    }

    public void tomarLockLectura() {
        lectoresActivos++;
    }

    public void liberarLockLectura() {
        if (lectoresActivos > 0) {
            lectoresActivos--;
        }
    }

    public void tomarLockEscritura(int procesoId) {
        lockEscrituraActivo = true;
        procesoEscritor = procesoId;
    }

    public void liberarLockEscritura(int procesoId) {
        if (lockEscrituraActivo && procesoEscritor == procesoId) {
            lockEscrituraActivo = false;
            procesoEscritor = -1;
        }
    }

    public int obtenerLectoresActivos() {
        return lectoresActivos;
    }

    public boolean tieneLockEscrituraActivo() {
        return lockEscrituraActivo;
    }

    public int obtenerProcesoEscritor() {
        return procesoEscritor;
    }

    public Cola<Proceso> obtenerColaEspera() {
        return colaEspera;
    }

    public Color obtenerColor() {
        return color;
    }

    public void establecerColor(Color color) {
        this.color = color;
    }
}