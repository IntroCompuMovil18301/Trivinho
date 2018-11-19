package javeriana.compumovil.tcp.trivinho.negocio;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Reserva implements Serializable {

    private int diaInicio;
    private int mesInicio;
    private int anioInicio;

    private int diaFinal;
    private int mesFinal;
    private int anioFinal;

    private String alojamiento;
    private String huesped;

    private Bitmap foto;

    private Alojamiento alojamientoO;
    private Usuario anfitrionO;

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public int getDiaInicio() {
        return diaInicio;
    }

    public void setDiaInicio(int diaInicio) {
        this.diaInicio = diaInicio;
    }

    public int getMesInicio() {
        return mesInicio;
    }

    public void setMesInicio(int mesInicio) {
        this.mesInicio = mesInicio;
    }

    public int getAnioInicio() {
        return anioInicio;
    }

    public void setAnioInicio(int anioInicio) {
        this.anioInicio = anioInicio;
    }

    public int getDiaFinal() {
        return diaFinal;
    }

    public void setDiaFinal(int diaFinal) {
        this.diaFinal = diaFinal;
    }

    public int getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }

    public int getAnioFinal() {
        return anioFinal;
    }

    public void setAnioFinal(int anioFinal) {
        this.anioFinal = anioFinal;
    }

    public String getAlojamiento() {
        return alojamiento;
    }

    public void setAlojamiento(String alojamiento) {
        this.alojamiento = alojamiento;
    }

    public String getHuesped() {
        return huesped;
    }

    public void setHuesped(String huesped) {
        this.huesped = huesped;
    }

    public Alojamiento getAlojamientoO() {
        return alojamientoO;
    }

    public void setAlojamientoO(Alojamiento alojamientoO) {
        this.alojamientoO = alojamientoO;
    }

    public Usuario getAnfitrionO() {
        return anfitrionO;
    }

    public void setAnfitrionO(Usuario anfitrionO) {
        this.anfitrionO = anfitrionO;
    }
}
