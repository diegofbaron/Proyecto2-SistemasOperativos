/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package estructuras;

/**
 *
 * @author diego
 */
public class Lista<T> {
    private Nodo<T> cabeza;
    private Nodo<T> colaNodo;
    private int tamano;

    public Lista() {
        this.cabeza = null;
        this.colaNodo = null;
        this.tamano = 0;
    }

    public void agregar(T dato) {
        Nodo<T> nuevoNodo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevoNodo;
            colaNodo = nuevoNodo;
        } else {
            colaNodo.siguiente = nuevoNodo;
            colaNodo = nuevoNodo;
        }
        tamano++;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException();
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }

    public int obtenerTamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public void eliminar(T dato) {
        if (cabeza == null) return;
        
        if (cabeza.dato.equals(dato)) {
            cabeza = cabeza.siguiente;
            if (cabeza == null) colaNodo = null;
            tamano--;
            return;
        }
        
        Nodo<T> actual = cabeza;
        while (actual.siguiente != null && !actual.siguiente.dato.equals(dato)) {
            actual = actual.siguiente;
        }
        
        if (actual.siguiente != null) {
            if (actual.siguiente == colaNodo) {
                colaNodo = actual;
            }
            actual.siguiente = actual.siguiente.siguiente;
            tamano--;
        }
    }
}
