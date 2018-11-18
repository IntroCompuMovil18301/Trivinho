package javeriana.compumovil.tcp.trivinho.negocio;

import java.util.ArrayList;
import java.util.List;

public class Huesped {
    private int numeroReservas;
    private List<Reserva> reservas;

    public Huesped() {
        this.reservas = new ArrayList<Reserva>();
    }

    public int getNumeroReservas() {
        return numeroReservas;
    }

    public void setNumeroReservas(int numeroReservas) {
        this.numeroReservas = numeroReservas;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
}
