package javeriana.compumovil.tcp.trivinho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class ComentariosListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context contexto;
    private String[][] datos;
    private int imagenes[];
    private float calificacion[];

    public ComentariosListAdapter(Context contexto, String[][] datos, int imagenes[], float calificacion[]) {
        this.contexto = contexto;
        this.datos = datos;
        this.imagenes = imagenes;
        this.calificacion = calificacion;
        inflater = (LayoutInflater)contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final View v = inflater.inflate(R.layout.item_comentarios,null);

        TextView nombre = (TextView) v.findViewById(R.id.comenNombre);
        TextView fecha = (TextView) v.findViewById(R.id.comenFecha);
        TextView comentario = (TextView) v.findViewById(R.id.comenComentario);
        RatingBar rating = (RatingBar) v.findViewById(R.id.comenRating);
        ImageView imagen = (ImageView) v.findViewById(R.id.comenUsuario);

        nombre.setText(datos[i][0]);
        comentario.setText(datos[i][1]);
        fecha.setText(datos[i][2]);
        rating.setRating(calificacion[i]);

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
