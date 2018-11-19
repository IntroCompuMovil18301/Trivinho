package javeriana.compumovil.tcp.trivinho;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.Calificacion;
import javeriana.compumovil.tcp.trivinho.negocio.Usuario;

public class CalificarAlojamientoActivity extends AppCompatActivity {

    private String alojamiento;
    private String usuario;
    private EditText comentario;
    private RatingBar estrellas;
    private TextView anfitrion;
    private ImageView foto;
    private Button calificar;


    private Alojamiento alojamientoO;



    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private FirebaseDatabase database;

    private StorageReference mStorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar_alojamiento);



        alojamiento = getIntent().getStringExtra("alojamiento");
        usuario = getIntent().getStringExtra("usuario");
        estrellas = (RatingBar) findViewById(R.id.estrellasCal);

        estrellas.setNumStars(5);
        anfitrion = (TextView) findViewById(R.id.nombreAnfitrion);
        foto = (ImageView) findViewById(R.id.fotoAlojamientoCal);
        calificar = (Button) findViewById(R.id.calificar);
        comentario = (EditText) findViewById(R.id.comentarioCal);




        mStorageRef = FirebaseStorage.getInstance().getReference();
        database= FirebaseDatabase.getInstance();

        inicializarVista();

        calificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                almacenarCalificacion();
                Intent intent = new Intent (CalificarAlojamientoActivity.this, UsuarioMainActivity.class);
                startActivity(intent);
            }
        });

    }


    private void almacenarCalificacion(){

        int numeroReservasCalificaciones=alojamientoO.getNumeroCalificaciones()+1;
        alojamientoO.setNumeroCalificaciones(numeroReservasCalificaciones);
        Calificacion calificacion = new Calificacion();
        calificacion.setCalificacion(estrellas.getRating());
        calificacion.setUsuario(usuario);
        calificacion.setComentario(comentario.getText().toString());
        myRef = database.getReference(Utils.getPathAlojamientos() + alojamiento +"/" + "numeroCalificaciones");
        myRef.setValue(alojamientoO.getNumeroCalificaciones());

        myRef = database.getReference(Utils.getPathAlojamientos()+alojamiento+"/calificaciones/"+ String.valueOf(alojamientoO.getNumeroCalificaciones()-1));
        myRef.setValue(calificacion);
    }

    private void inicializarVista(){
        myRef = database.getReference(Utils.getPathAlojamientos()+alojamiento);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                alojamientoO = dataSnapshot.getValue(Alojamiento.class);
                descargaryMostrarFoto(alojamientoO.getFotos().get(0).getRutaFoto());
                myRef2 = database.getReference(Utils.getPathUsers()+alojamientoO.getAnfitrion());
                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        Usuario usuario = dataSnapshot2.getValue(Usuario.class);
                        anfitrion.setText("Anfitrion: "+usuario.getNombres()+" "+usuario.getApellidos());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
