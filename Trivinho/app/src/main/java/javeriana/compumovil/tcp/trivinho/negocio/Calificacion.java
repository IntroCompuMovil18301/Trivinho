package javeriana.compumovil.tcp.trivinho.negocio;

import java.io.Serializable;

public class Calificacion implements Serializable {
    private Double calificacion;
    private String comentario;

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }
}
