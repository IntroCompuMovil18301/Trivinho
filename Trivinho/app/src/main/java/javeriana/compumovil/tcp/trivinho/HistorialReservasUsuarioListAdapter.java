package javeriana.compumovil.tcp.trivinho;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javeriana.compumovil.tcp.trivinho.negocio.Reserva;

public class HistorialReservasUsuarioListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context contexto;
    private List<Reserva> reservas;

    public HistorialReservasUsuarioListAdapter(Context contexto, List<Reserva> reservas) {
        this.contexto = contexto;
        this.reservas = reservas;
        inflater = (LayoutInflater)contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final View v = inflater.inflate(R.layout.item_comentarios,null);
        String fechaI,fechaF;

        TextView nombre = (TextView) v.findViewById(R.id.historialPropietarioUsuario);
        TextView fechaInicial = (TextView) v.findViewById(R.id.historialFechaInicial);
        TextView fechaFinal = (TextView) v.findViewById(R.id.historialFechaFinal);
        TextView precio = (TextView) v.findViewById(R.id.historialPrecioUsuario);
        ImageView imagen = (ImageView) v.findViewById(R.id.historialImagenUsuario);

        fechaI=Integer.toString(reservas.get(i).getDiaInicio())+"/"+Integer.toString(reservas.get(i).getMesInicio())+"/"+Integer.toString(reservas.get(i).getAnioInicio());
        fechaF=Integer.toString(reservas.get(i).getDiaFinal())+"/"+Integer.toString(reservas.get(i).getMesFinal())+"/"+Integer.toString(reservas.get(i).getAnioFinal());

        nombre.setText(reservas.get(i).getAnfitrionO().getNombres()+" "+reservas.get(i).getAnfitrionO().getApellidos());
        fechaInicial.setText(fechaI);
        fechaFinal.setText(fechaF);
        precio.setText(Double.toString(reservas.get(i).getAlojamientoO().getValorPorNoche()));
        imagen.setImageBitmap(reservas.get(i).getFoto());

        imagen.setTag(i);
        return v;
    }

    @Override
    public int getCount() {
        return reservas.size();
    }

    @Override
    public Object getItem(int i) {
        return reservas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

}
