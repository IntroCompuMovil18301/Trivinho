package javeriana.compumovil.tcp.trivinho;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.FotoAlojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.Huesped;
import javeriana.compumovil.tcp.trivinho.negocio.Reserva;
import javeriana.compumovil.tcp.trivinho.negocio.Usuario;

public class VerHistorialDeReservasActivity extends AppCompatActivity {


    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private FirebaseDatabase database;
    List<Reserva> reservas;

    private StorageReference mStorageRef;
    private Reserva reserva;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_historial_de_reservas);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        user = mAuth.getCurrentUser();
        reservas = new ArrayList<Reserva>();

        obtenerListaDeReservas();

    }


    private void obtenerListaDeReservas(){
        myRef = database.getReference(Utils.getPathHuespedes() + user.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Huesped huesped = dataSnapshot.getValue(Huesped.class);
                if(huesped.getReservas()!=null){
                    for (Reserva reservaO: huesped.getReservas()){
                        reserva = reservaO;
                        descargarAnfitron();
                    }
                }
                else{
                    Toast.makeText(VerHistorialDeReservasActivity.this, "No tiene reservas.", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR: ", "error en la consulta", databaseError.toException());
            }
        });
    }

    private void descargarAnfitron(){
        Log.i("RESERVA", reserva.getAlojamiento());
        myRef = database.getReference(Utils.getPathAlojamientos()+reserva.getAlojamiento());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                Alojamiento alojamiento = dataSnapshot.getValue(Alojamiento.class);
                alojamiento.getDescripcion();
                reserva.setAlojamientoO(alojamiento);
                myRef2 = database.getReference(Utils.getPathUsers()+alojamiento.getAnfitrion());
                myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        Usuario usuario = dataSnapshot2.getValue(Usuario.class);
                        reserva.setAnfitrionO(usuario);
                        descargaryMostrarFoto(reserva);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("ERROR: ", "error en la consulta", databaseError.toException());
                    }
                });

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR: ", "error en la consulta", databaseError.toException());
            }
        });
    }


    private void descargaryMostrarFoto (Reserva reserva){

        final StorageReference fotoAlojamiento = mStorageRef.child(reserva.getAlojamientoO().getFotos().get(0).getRutaFoto());
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        final File finalLocalFile = localFile;
        fotoAlojamiento.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                        String filePath = finalLocalFile.getPath();
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                        //foto.setImageBitmap(bitmap); //AQUI VA EL IMAGEVIEW QUE SE CAMBIA --- DEBERIA SER EL DE LA LISTA
                        // AQUI YA TIENE EL RESTO DE LA INFO A PARTIR DE RESERVA
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
