package javeriana.compumovil.tcp.trivinho.negocio;

import java.io.Serializable;

public class Calificacion implements Serializable {
    private float calificacion;
    private String comentario;
    private String usuario;

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
