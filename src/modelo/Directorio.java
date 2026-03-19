/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import estructuras.Lista;

public class Directorio extends ElementoSistema {
    private Lista<ElementoSistema> hijos;

    public Directorio(String nombre, String dueno, Directorio padre) {
        super(nombre, dueno, padre);
        this.hijos = new Lista<>();
    }

    @Override
    public int obtenerTamano() {
        return 0;
    }

    public void agregarHijo(ElementoSistema hijo) {
        hijo.establecerPadre(this);
        hijos.agregar(hijo);
    }

    public void eliminarHijo(ElementoSistema hijo) {
        hijos.eliminar(hijo);
    }

    public Lista<ElementoSistema> obtenerHijos() {
        return hijos;
    }
}
