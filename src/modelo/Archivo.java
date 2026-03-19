/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

public class Archivo extends ElementoSistema {
    private int tamanoBloques;
    private int bloqueInicial;
    private boolean bloqueado;

    public Archivo(String nombre, String dueno, Directorio padre, int tamanoBloques) {
        super(nombre, dueno, padre);
        this.tamanoBloques = tamanoBloques;
        this.bloqueInicial = -1;
        this.bloqueado = false;
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

    public boolean estaBloqueado() {
        return bloqueado;
    }

    public void bloquear() {
        this.bloqueado = true;
    }

    public void desbloquear() {
        this.bloqueado = false;
    }
}