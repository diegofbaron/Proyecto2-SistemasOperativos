/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class DiscoVirtual {
    private Bloque[] bloques;
    private int cantidadBloques;

    public DiscoVirtual(int cantidadBloques) {
        this.cantidadBloques = cantidadBloques;
        this.bloques = new Bloque[cantidadBloques];
        for (int i = 0; i < cantidadBloques; i++) {
            this.bloques[i] = new Bloque(i);
        }
    }

    public int buscarBloqueLibre() {
        for (int i = 0; i < cantidadBloques; i++) {
            if (!bloques[i].estaOcupado()) {
                return i;
            }
        }
        return -1;
    }

    public void ocuparBloque(int indice, String nombreArchivo, int siguiente) {
        bloques[indice].establecerOcupado(true);
        bloques[indice].establecerNombreArchivo(nombreArchivo);
        bloques[indice].establecerSiguienteBloque(siguiente);
    }

    public void liberarBloque(int indice) {
        bloques[indice].establecerOcupado(false);
        bloques[indice].establecerNombreArchivo("");
        bloques[indice].establecerSiguienteBloque(-1);
    }

    public Bloque obtenerBloque(int indice) {
        return bloques[indice];
    }

    public int obtenerCantidadBloques() {
        return cantidadBloques;
    }
}
