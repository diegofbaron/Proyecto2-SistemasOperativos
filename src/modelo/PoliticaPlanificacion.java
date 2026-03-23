package modelo;

public enum PoliticaPlanificacion {
    FIFO("FIFO"),
    SSTF("SSTF"),
    SCAN("SCAN"),
    C_SCAN("C-SCAN");

    private final String etiqueta;

    PoliticaPlanificacion(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
