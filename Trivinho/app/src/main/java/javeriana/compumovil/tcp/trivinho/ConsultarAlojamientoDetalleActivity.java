package javeriana.compumovil.tcp.trivinho;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.FotoAlojamiento;

public class ConsultarAlojamientoDetalleActivity extends AppCompatActivity {

    private Button versitiosInteres;
    private Button reservar;
    private Button salir;
    private Button inicio;

    private TextView tipo;
    private TextView descripcion;
    private TextView moneda;
    private TextView valor;

    private GridView fotos;
    private StorageReference mStorageRef;
    private ListView listacomentarios;

    private RatingBar calificacion;
    private ImageAdapter imageAdapter;

    private Alojamiento alojamiento;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_alojamiento);
        reservar=(Button) findViewById(R.id.detalleReservar);
        tipo = (TextView) findViewById(R.id.detalleTipo);
        descripcion = (TextView) findViewById(R.id.detalleDescripcion);
        moneda = (TextView) findViewById(R.id.detalleMoneda) ;
        valor = (TextView) findViewById(R.id.detalleValor);
        calificacion = (RatingBar) findViewById(R.id.detalleRatingBar);
        fotos=(GridView)findViewById(R.id.gridFotos);
        listacomentarios=(ListView)findViewById(R.id.listComentarios);
        salir = (Button) findViewById(R.id.salirDetalle);
        inicio = (Button) findViewById(R.id.inicioDetalle);
        mAuth = FirebaseAuth.getInstance();


        imageAdapter = new ImageAdapter(this);

        fotos.setAdapter(imageAdapter);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        alojamiento = (Alojamiento) getIntent().getSerializableExtra("alojamiento");
        listacomentarios.setAdapter(new ComentariosListAdapter(this,alojamiento.getCalificaciones()));

        tipo.setText(alojamiento.getTipo());
        descripcion.setText(alojamiento.getDescripcion());
        valor.setText(Double.toString(alojamiento.getValorPorNoche()));
        moneda.setText(alojamiento.getTipoMoneda());
        calificacion.setRating(alojamiento.getPuntaje());

        listacomentarios.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        fotos.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar = new Intent(view.getContext(),UsuarioMainActivity.class);
                startActivity(activar);
            }
        });

        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),ReservarAlojamiento.class);
                activar.putExtra("alojamiento", alojamiento);
                startActivity(activar);
            }
        });

        for(FotoAlojamiento fotico:alojamiento.getFotos()){
            descargaryMostrarFoto(fotico.getRutaFoto());
        }

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ConsultarAlojamientoDetalleActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }


    private void descargaryMostrarFoto (String ruta) {
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
                        imageAdapter.agregarImagen(bitmap);
                        fotos.setAdapter(imageAdapter);
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
