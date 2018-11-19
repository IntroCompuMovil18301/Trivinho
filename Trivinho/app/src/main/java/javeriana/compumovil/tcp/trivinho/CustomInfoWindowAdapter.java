package javeriana.compumovil.tcp.trivinho;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.FotoAlojamiento;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View view;
    private static LayoutInflater inflater = null;
    private Context contexto;
    private StorageReference mStorageRef;
    private ImageView foto;

    public CustomInfoWindowAdapter(Context contexto) {
        this.contexto = contexto;
        inflater = (LayoutInflater)contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.infowindowconstum,null);
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void agregarInformacion(Marker marker){
        Alojamiento alojamiento = null;
        TextView tipo = (TextView) view.findViewById(R.id.infoTipo);
        TextView moneda = (TextView) view.findViewById(R.id.infoMoneda);
        TextView precio = (TextView) view.findViewById(R.id.infoPrecio);
        TextView descripion = (TextView) view.findViewById(R.id.infoDescripcion);
        foto = (ImageView) view.findViewById(R.id.infoimage);
        RatingBar rating = (RatingBar) view.findViewById(R.id.infoRating);

        if(marker.getTag() instanceof  Alojamiento){
            alojamiento = (Alojamiento) marker.getTag();
        }

        if(alojamiento!=null){
            tipo.setText(alojamiento.getTipo());
            moneda.setText(alojamiento.getTipoMoneda());
            moneda.setVisibility(View.VISIBLE);
            precio.setText(Double.toString(alojamiento.getValorPorNoche()));
            precio.setVisibility(View.VISIBLE);
            descripion.setText(alojamiento.getDescripcion());
            descripion.setVisibility(View.VISIBLE);
            rating.setRating(alojamiento.getPuntaje());
            rating.setVisibility(View.VISIBLE);
            foto.setVisibility(View.VISIBLE);
            FotoAlojamiento fotoAlojamiento = alojamiento.getFotos().get(0);

            descargaryMostrarFoto(fotoAlojamiento.getRutaFoto());
        }
        else{
            tipo.setText(marker.getTitle());
            moneda.setVisibility(View.GONE);
            precio.setVisibility(View.GONE);
            descripion.setText(marker.getSnippet());
            rating.setVisibility(View.GONE);
            foto.setVisibility(View.GONE);
        }



    }

    @Override
    public View getInfoContents(Marker marker) {
        agregarInformacion(marker);
        return view;
    }

    @Override
    public View getInfoWindow(Marker m) {
        agregarInformacion(m);
        return view;
    }

    private void descargaryMostrarFoto (String ruta){
        final StorageReference fotoUsuario = mStorageRef.child(ruta);
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File finalLocalFile = localFile;
        fotoUsuario.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                        String filePath = finalLocalFile.getPath();
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        foto.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
    }
}
