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
    private String detalleOperacion;
    private String mensajeResultado;

    public Proceso(int id, TipoOperacion operacion, Archivo archivoDestino, int tamanoRequerido) {
        this.id = id;
        this.estado = EstadoProceso.NUEVO;
        this.operacion = operacion;
        this.archivoDestino = archivoDestino;
        this.tamanoRequerido = tamanoRequerido;
        this.detalleOperacion = construirDetalleOperacion(operacion, archivoDestino);
        this.mensajeResultado = "Pendiente";
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

    public String obtenerDetalleOperacion() {
        return detalleOperacion;
    }

    public String obtenerMensajeResultado() {
        return mensajeResultado;
    }

    public void establecerMensajeResultado(String mensajeResultado) {
        this.mensajeResultado = mensajeResultado;
    }

    private String construirDetalleOperacion(TipoOperacion operacion, Archivo archivoDestino) {
        String nombreArchivo = archivoDestino != null ? archivoDestino.obtenerNombre() : "(sin archivo)";
        return operacion.name() + " " + nombreArchivo;
    }
}