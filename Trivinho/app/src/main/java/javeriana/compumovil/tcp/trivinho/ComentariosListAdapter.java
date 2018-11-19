package javeriana.compumovil.tcp.trivinho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import javeriana.compumovil.tcp.trivinho.negocio.Calificacion;

public class ComentariosListAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Context contexto;
    private List<Calificacion> comentarios;

    public ComentariosListAdapter(Context contexto, List<Calificacion> comentarios) {
        this.contexto = contexto;
        this.comentarios = comentarios;
        inflater = (LayoutInflater)contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final View v = inflater.inflate(R.layout.item_comentarios,null);

        TextView nombre = (TextView) v.findViewById(R.id.comenNombre);
        TextView comentario = (TextView) v.findViewById(R.id.comenComentario);
        RatingBar rating = (RatingBar) v.findViewById(R.id.comenRating);

        nombre.setText(comentarios.get(i).getUsuario());
        comentario.setText(comentarios.get(i).getComentario());
        rating.setRating(comentarios.get(i).getCalificacion());

        rating.setTag(1);
        return v;
    }

    @Override
    public int getCount() {
        return comentarios.size();
    }

    @Override
    public Object getItem(int i) {
        return comentarios.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

}
