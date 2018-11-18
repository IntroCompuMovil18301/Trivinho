package javeriana.compumovil.tcp.trivinho.negocio;

import java.io.Serializable;

public class FotoAlojamiento implements Serializable {
    private String rutaFoto;

    public String getRutaFoto() {
        return rutaFoto;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

}
