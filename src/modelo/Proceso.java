/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Proceso {
    private int id;
    private EstadoProceso estado;
    private TipoOperacion operacion;
    private Archivo archivoDestino;
    private int tamanoRequerido;

    public Proceso(int id, TipoOperacion operacion, Archivo archivoDestino, int tamanoRequerido) {
        this.id = id;
        this.estado = EstadoProceso.NUEVO;
        this.operacion = operacion;
        this.archivoDestino = archivoDestino;
        this.tamanoRequerido = tamanoRequerido;
    }

    public int obtenerId() {
        return id;
    }

    public EstadoProceso obtenerEstado() {
        return estado;
    }

    public void establecerEstado(EstadoProceso estado) {
        this.estado = estado;
    }

    public TipoOperacion obtenerOperacion() {
        return operacion;
    }

    public Archivo obtenerArchivoDestino() {
        return archivoDestino;
    }

    public int obtenerTamanoRequerido() {
        return tamanoRequerido;
    }
}