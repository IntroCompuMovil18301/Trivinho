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
import android.widget.ImageView;
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

import javeriana.compumovil.tcp.trivinho.negocio.Usuario;

public class UsuarioMainActivity extends AppCompatActivity {

    private Button consultarAlojamiento;
    private Button verHistorialReservas;
    private Button reseñas;
    private Button verMisAlojamientos;
    private Button agregarAlojamiento;
    private Button alojamientosArrendados;
    private Button notificaciones;
    private Button verSolicitudes;
    private Button salir;


    private ImageView foto;
    private TextView mEdad;
    private TextView mNombre;
    private TextView mApellido;
    private TextView mPais;
    private TextView mGenero;

    private FirebaseAuth mAuth;

    private static String TAG = "Error Firebase";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_main);
        consultarAlojamiento=(Button) findViewById(R.id.consultarAlojamiento);
        verHistorialReservas=(Button) findViewById(R.id.verhistorialreservas);
        reseñas=(Button) findViewById(R.id.reseñas);
        verMisAlojamientos=(Button) findViewById(R.id.verMisAlojamientos);
        agregarAlojamiento=(Button) findViewById(R.id.agregarAlojamiento);
        alojamientosArrendados=(Button) findViewById(R.id.alojamientosArrendados);;
        notificaciones=(Button) findViewById(R.id.notificaciones);
        verSolicitudes=(Button) findViewById(R.id.verSolicitudes);

        foto = (ImageView) findViewById(R.id.fotoUsuario);

        mAuth = FirebaseAuth.getInstance();

        database= FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        salir = (Button) findViewById(R.id.salir);

        mEdad = (TextView) findViewById(R.id.edad);
        mNombre = (TextView) findViewById(R.id.nombre);
        mApellido = (TextView) findViewById(R.id.apellido);
        mPais = (TextView) findViewById(R.id.pais);
        mGenero = (TextView) findViewById(R.id.genero);

        inicializarVista();

        consultarAlojamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),ConsultarAlojamientoActivity.class);
                startActivity(activar);
            }
        });

        verHistorialReservas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),VerHistorialDeReservasActivity.class);
                startActivity(activar);
            }
        });

        reseñas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),UsuarioMainActivity.class);
                startActivity(activar);
            }
        });

        verMisAlojamientos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),VerMisAlojamientos.class);
                startActivity(activar);
            }
        });

        agregarAlojamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),AgregarAlojamientoActivity.class);
                startActivity(activar);
            }
        });

        alojamientosArrendados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),AlojamientosArrendadosActivity.class);
                startActivity(activar);
            }
        });

        notificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),Notificaciones.class);
                startActivity(activar);
            }
        });

        verSolicitudes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),VerSolicitudes.class);
                startActivity(activar);
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(UsuarioMainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }

    private void inicializarMenuHuesped(){
        consultarAlojamiento.setVisibility(View.VISIBLE);
        verHistorialReservas.setVisibility(View.VISIBLE);
    }
    private void inicializarMenuAnfitrion(){
        agregarAlojamiento.setVisibility(View.VISIBLE);
    }


    private void inicializarVista (){
        myRef = database.getReference(Utils.getPathUsers());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            FirebaseUser user = mAuth.getCurrentUser();
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    if (singleSnapshot.getKey().equals(user.getUid())){
                        Usuario myUser = singleSnapshot.getValue(Usuario.class);
                        mNombre.setText("Nombre: "+myUser.getNombres());
                        mApellido.setText("Apellido: "+myUser.getApellidos());
                        mEdad.setText("Edad: "+String.valueOf(myUser.getEdad()));
                        mPais.setText("Pais: "+myUser.getPais());
                        mGenero.setText("Genero: "+myUser.getGenero());
                        descargaryMostrarFoto(myUser.getRutaFoto());
                        if (myUser.getEsAnfitrion()){
                            inicializarMenuAnfitrion();
                        }
                        if (myUser.getEsHuesped()){
                            inicializarMenuHuesped();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Error al contactar con la base de datos.", databaseError.toException());
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
