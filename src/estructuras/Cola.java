/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author diego
 */
public class Cola<T> {
    private Lista<T> lista;

    public Cola() {
        this.lista = new Lista<>();
    }

    public void encolar(T dato) {
        lista.agregar(dato);
    }

    public T desencolar() {
        if (lista.estaVacia()) return null;
        T dato = lista.obtener(0);
        lista.eliminar(dato);
        return dato;
    }

    public T verFrente() {
        if (lista.estaVacia()) return null;
        return lista.obtener(0);
    }

    public boolean estaVacia() {
        return lista.estaVacia();
    }

    public int obtenerTamano() {
        return lista.obtenerTamano();
    }

    public T obtener(int indice) {
        return lista.obtener(indice);
    }
}