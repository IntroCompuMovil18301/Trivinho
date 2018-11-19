package javeriana.compumovil.tcp.trivinho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class HistorialReservasUsuarioListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context contexto;
    private String[][] datos;

    public HistorialReservasUsuarioListAdapter(Context contexto, String[][] datos) {
        this.contexto = contexto;
        this.datos = datos;
        inflater = (LayoutInflater)contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final View v = inflater.inflate(R.layout.item_comentarios,null);

        TextView nombre = (TextView) v.findViewById(R.id.historialPropietarioUsuario);
        TextView fechaInicial = (TextView) v.findViewById(R.id.historialFechaInicial);
        TextView fechaFinal = (TextView) v.findViewById(R.id.historialFechaFinal);
        TextView precio = (TextView) v.findViewById(R.id.historialPrecioUsuario);
        ImageView imagen = (ImageView) v.findViewById(R.id.historialImagenUsuario);

        nombre.setText(datos[i][0]);
        fechaInicial.setText(datos[i][1]);
        fechaFinal.setText(datos[i][2]);
        precio.setText(datos[i][3]);

        imagen.setTag(1);
        return v;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

}
