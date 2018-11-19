package javeriana.compumovil.tcp.trivinho.negocio;

import java.io.Serializable;

public class Calificacion implements Serializable {
    private float calificacion;
    private String comentario;

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }
}
