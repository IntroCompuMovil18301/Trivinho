package javeriana.compumovil.tcp.trivinho;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.FechaDisponible;
import javeriana.compumovil.tcp.trivinho.negocio.FotoAlojamiento;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.vo.DateData;

public class AgregarAlojamientoFechasActivity extends AppCompatActivity {


    private Button agregarFecha;
    private Button agregarAlojamiento;
    private Button salir;
    private Button inicio;

    private EditText mfechaInicio;
    private EditText mfechaFinal;

    private Alojamiento alojamiento;

    private List<FechaDisponible> fechasDisponibles;
    private List<FotoAlojamiento> fotos;
    private List<byte[]> fotosB;
    private StorageReference mStorageRef;

    private MCalendarView calendarView;

    private FirebaseAuth mAuth;
    private FirebaseUser user;


    private DatabaseReference myRef;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_alojamiento_fechas);

        alojamiento = (Alojamiento) getIntent().getSerializableExtra("alojamiento");


        agregarFecha = (Button) findViewById(R.id.agregarFechas);
        mfechaInicio = (EditText) findViewById(R.id.fechainicio);
        mfechaFinal = (EditText) findViewById(R.id.fechafinal);
        inicio = (Button) findViewById(R.id.button6);

        agregarAlojamiento = (Button) findViewById(R.id.agregarAlojamientoFin);

        calendarView = (MCalendarView) findViewById(R.id.calendarioFechas);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database= FirebaseDatabase.getInstance();
        salir = (Button) findViewById(R.id.salir4);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        alojamiento.setAnfitrion(user.getUid());

        fechasDisponibles = new ArrayList<FechaDisponible>();
        fotos = new ArrayList<FotoAlojamiento>();

        fotosB = (ArrayList) ((ArrayList)ImageAdapter.getImagenes()).clone();
        ImageAdapter.limpiarImagenes();

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar = new Intent(view.getContext(),UsuarioMainActivity.class);
                startActivity(activar);
            }
        });

        mfechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                ((DatePickerFragment) newFragment).setCampo((EditText)findViewById(R.id.fechainicio));
                newFragment.show(getFragmentManager(),"Date Picker");
            }
        });

        mfechaFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                ((DatePickerFragment) newFragment).setCampo((EditText)findViewById(R.id.fechafinal));
                newFragment.show(getFragmentManager(),"Date Picker");
            }
        });


        agregarFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarFecha();
            }
        });

        agregarAlojamiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAlojamiento ();
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(AgregarAlojamientoFechasActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void guardarAlojamiento (){
        if (comprobarNumFechas()){
            int i=0;

            myRef = database.getReference(Utils.getPathAlojamientos());
            String key = myRef.push().getKey();
            myRef = database.getReference(Utils.getPathAlojamientos()+key);

            alojamiento.setFechasDisponibles(fechasDisponibles);
            for (byte[] foto: fotosB){
                uploadImage(key, i, foto);
                i++;
            }
        }
    }

    private void uploadImage (String IDalojamiento, int numeroFoto, byte[] foto){

        final String rutafotoFireBase = "imagenes/alojamientos/fotosalojamiento/"+IDalojamiento+"/"+ String.valueOf(numeroFoto)+ ".jgp";
        final String IDalojamiento_ = IDalojamiento;
        final int numeroFoto_ = numeroFoto;
        StorageReference fotoUsuario = mStorageRef.child(rutafotoFireBase);


        byte[] imageInByte = foto;

        fotoUsuario.putBytes(imageInByte)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        guardarImagenBD(rutafotoFireBase, IDalojamiento_);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }


    private void guardarImagenBD (String ruta, String IDalojamiento){
        FotoAlojamiento fotoAlojamiento= new FotoAlojamiento();
        fotoAlojamiento.setRutaFoto(ruta);
        fotos.add(fotoAlojamiento);

        if (fotosB.size() == fotos.size()){//significa que ya se subieron todas las fotos
            alojamiento.setFotos(fotos);
            myRef.setValue(alojamiento);
            Toast.makeText(this, "Alojamiento agregado con éxito.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, UsuarioMainActivity.class);
            startActivity(intent);
        }
    }


    private boolean comprobarNumFechas(){
        if (fechasDisponibles.isEmpty()) {
            Toast.makeText(this, "Debe agregar al menos un intervalo de fechas de disponibilidad.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    private void agregarFecha (){
        if (validarCampos()){
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String fechaInicio = mfechaInicio.getText().toString();
                String fechaFinal = mfechaFinal.getText().toString();

                mfechaInicio.setText("");
                mfechaFinal.setText("");

                Calendar inicio_date = Calendar.getInstance();
                Calendar final_date = Calendar.getInstance();
                inicio_date.setTime(format.parse(fechaInicio));
                final_date.setTime(format.parse(fechaFinal));

                FechaDisponible fecha= new FechaDisponible();
                fecha.setAnioInicio(inicio_date.get(Calendar.YEAR));
                fecha.setMesInicio(inicio_date.get(Calendar.MONTH)+1);
                fecha.setDiaInicio(inicio_date.get(Calendar.DAY_OF_MONTH));


                fecha.setAnioFinal(final_date.get(Calendar.YEAR));
                fecha.setMesFinal(final_date.get(Calendar.MONTH)+1);
                fecha.setDiaFinal(final_date.get(Calendar.DAY_OF_MONTH));


                fechasDisponibles.add(fecha);


                Calendar cStart = inicio_date;
                Calendar cEnd = final_date;
                cEnd.add(Calendar.DAY_OF_MONTH, 1);


                while (cStart.before(cEnd)) {
                    calendarView.markDate (cStart.get(Calendar.YEAR), cStart.get(Calendar.MONTH)+1, cStart.get(Calendar.DAY_OF_MONTH));
                    cStart.add(Calendar.DAY_OF_MONTH, 1);
                }


                calendarView.travelTo(new DateData(inicio_date.get(Calendar.YEAR), inicio_date.get(Calendar.MONTH)+1, inicio_date.get(Calendar.DAY_OF_MONTH)));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validarCampos (){


        boolean validos = true;
        String fechaInicio = mfechaInicio.getText().toString();
        String fechaFinal = mfechaFinal.getText().toString();

        if (fechaInicio.isEmpty()) {
            validos = false;
            mfechaInicio.setError ("Obligatoria.");
        }
        else{
            mfechaInicio.setError (null);
        }

        if (fechaFinal.isEmpty()) {
            validos = false;
            mfechaFinal.setError ("Obligatoria.");

        }
        else{
            mfechaFinal.setError (null);
        }


        if (!mfechaInicio.getText().toString().isEmpty() && !mfechaFinal.getText().toString().isEmpty()){
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Calendar inicio_date = Calendar.getInstance();;
                Calendar final_date = Calendar.getInstance();;
                inicio_date.setTime(format.parse(fechaInicio));
                final_date.setTime(format.parse(fechaFinal));
                Calendar fechaActual = Calendar.getInstance();
                if (inicio_date.after(final_date) || inicio_date.before (fechaActual)){
                    validos = false;
                    Toast.makeText(AgregarAlojamientoFechasActivity.this, "Las fechas ingresadas no son validas.", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return validos;
    }
}
