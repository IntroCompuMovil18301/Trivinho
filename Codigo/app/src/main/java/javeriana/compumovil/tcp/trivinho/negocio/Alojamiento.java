package javeriana.compumovil.tcp.trivinho.negocio;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Alojamiento implements Serializable {
    private String tipo;
    private Double valorPorNoche;
    private String tipoMoneda;
    private String descripcion;

    private double latitud;
    private double longitud;




    public Alojamiento() {
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValorPorNoche() {
        return valorPorNoche;
    }

    public void setValorPorNoche(Double valorPorNoche) {
        this.valorPorNoche = valorPorNoche;
    }

    public String getTipoMoneda() {
        return tipoMoneda;
    }

    public void setTipoMoneda(String tipoMoneda) {
        this.tipoMoneda = tipoMoneda;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

}
