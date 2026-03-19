/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public abstract class ElementoSistema {
    protected String nombre;
    protected String dueno;
    protected Directorio padre;

    public ElementoSistema(String nombre, String dueno, Directorio padre) {
        this.nombre = nombre;
        this.dueno = dueno;
        this.padre = padre;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public void establecerNombre(String nombre) {
        this.nombre = nombre;
    }

    public String obtenerDueno() {
        return dueno;
    }

    public Directorio obtenerPadre() {
        return padre;
    }

    public void establecerPadre(Directorio padre) {
        this.padre = padre;
    }

    public abstract int obtenerTamano();
    
    @Override
    public String toString() {
        return nombre;
    }
}
    