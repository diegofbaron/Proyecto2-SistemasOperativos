/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Bloque {
    private int id;
    private boolean ocupado;
    private String nombreArchivo;
    private int siguienteBloque;
    private int procesoOcupante;

    public Bloque(int id) {
        this.id = id;
        this.ocupado = false;
        this.nombreArchivo = "";
        this.siguienteBloque = -1;
        this.procesoOcupante = -1;
    }

    public int obtenerId() {
        return id;
    }

    public boolean estaOcupado() {
        return ocupado;
    }

    public void establecerOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public String obtenerNombreArchivo() {
        return nombreArchivo;
    }

    public void establecerNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public int obtenerSiguienteBloque() {
        return siguienteBloque;
    }

    public void establecerSiguienteBloque(int siguienteBloque) {
        this.siguienteBloque = siguienteBloque;
    }

    public int obtenerProcesoOcupante() {
        return procesoOcupante;
    }

    public void establecerProcesoOcupante(int procesoOcupante) {
        this.procesoOcupante = procesoOcupante;
    }
}
