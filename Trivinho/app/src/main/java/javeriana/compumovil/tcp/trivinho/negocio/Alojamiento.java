package javeriana.compumovil.tcp.trivinho.negocio;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Alojamiento implements Serializable {
    private String id;
    private String tipo;
    private Double valorPorNoche;
    private String tipoMoneda;
    private String descripcion;
    private float  puntaje;
    private String anfitrion;

    private double latitud;
    private double longitud;

    private List<FechaDisponible> fechasDisponibles;
    private List<FotoAlojamiento> fotos;
    private List<SitioDeInteres> sitiosDeInteres;
    private List<Reserva> reservas;
    private List<Calificacion> calificaciones;

    private int numeroReservas;
    private int numeroCalificaciones;



    public Alojamiento() {
        fechasDisponibles = new ArrayList<FechaDisponible>();
        fotos = new ArrayList<FotoAlojamiento>();
        reservas = new ArrayList<Reserva>();
        sitiosDeInteres = new ArrayList<SitioDeInteres>();
        calificaciones = new ArrayList<Calificacion>();
        puntaje=0;
        numeroReservas = 0;
        numeroCalificaciones = 0;
    }

    public float getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(float puntaje) {
        this.puntaje = puntaje;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FechaDisponible> getFechasDisponibles() {
        return fechasDisponibles;
    }

    public void setFechasDisponibles(List<FechaDisponible> fechasDisponibles) {
        this.fechasDisponibles = fechasDisponibles;
    }

    public List<FotoAlojamiento> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoAlojamiento> fotos) {
        this.fotos = fotos;
    }

    public int getNumeroReservas() {
        return numeroReservas;
    }

    public void setNumeroReservas(int numeroReservas) {
        this.numeroReservas = numeroReservas;
    }

    public List<SitioDeInteres> getSitiosDeInteres() {
        return sitiosDeInteres;
    }

    public void setSitiosDeInteres(List<SitioDeInteres> sitiosDeInteres) {
        this.sitiosDeInteres = sitiosDeInteres;
    }


    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }

    public String getAnfitrion() {
        return anfitrion;
    }

    public void setAnfitrion(String anfitrion) {
        this.anfitrion = anfitrion;
    }


    public List<Calificacion> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(List<Calificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }

    public int getNumeroCalificaciones() {
        return numeroCalificaciones;
    }

    public void setNumeroCalificaciones(int numeroCalificaciones) {
        this.numeroCalificaciones = numeroCalificaciones;
    }
}
