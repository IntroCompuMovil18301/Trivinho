package javeriana.compumovil.tcp.trivinho;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;

public class AgregarAlojamientoFotosActivity extends AppCompatActivity {



    private Button elegirFoto;
    private Button tomarFoto;
    private Button siguiente;
    private GridView gridview;
    private final static int CAMERA_PERMISSION = 1;
    private final static int EXTERNAL_STORAGE_PERMISSION = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int IMAGE_PICKER_REQUEST = 2;
    private Button salir;
    private Button inicio;

    private FirebaseAuth mAuth;

    private ImageAdapter imageAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_alojamiento_fotos);

        imageAdapter = new ImageAdapter(this);

        gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        elegirFoto = (Button) findViewById(R.id.seleccionarImagen4);
        tomarFoto = (Button) findViewById(R.id.tomarFoto4);
        siguiente = (Button) findViewById(R.id.siguiente2);
        salir = (Button) findViewById(R.id.salir5);
        inicio = (Button) findViewById(R.id.button6);

        mAuth = FirebaseAuth.getInstance();

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar = new Intent(view.getContext(),UsuarioMainActivity.class);
                startActivity(activar);
            }
        });

        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission( AgregarAlojamientoFotosActivity.this , Manifest.permission.CAMERA, "Se necesita usar la camara.", CAMERA_PERMISSION);
                takePicture();
            }
        });

        elegirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission( AgregarAlojamientoFotosActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Se necesita acceder al almacenamiento externo.", EXTERNAL_STORAGE_PERMISSION);
                elegirFoto();
            }
        });

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarFotos();

            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(AgregarAlojamientoFotosActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void agregarFotos(){
        if (validarNumFotos()) {
            Intent intent = new Intent(this, AgregarAlojamientoUbicacionActivity.class);
            Alojamiento alojamiento = (Alojamiento) getIntent().getSerializableExtra("alojamiento");
            Bundle bundle = new Bundle ();
            intent.putExtra("alojamiento", alojamiento);
            startActivity(intent);
        }
    }

    private boolean validarNumFotos(){
        if (imageAdapter.getNumImagenes() < 4){
            Toast.makeText(this, "Deben ser al menos 4 imágenes.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void requestPermission(Activity context, String permission, String explanation, int requestId ){
        if (ContextCompat.checkSelfPermission(context,permission)!= PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?Â  Â
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,permission)) {
                Toast.makeText(context, explanation, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestId);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case CAMERA_PERMISSION : {
                takePicture();
                break;
            }
            case EXTERNAL_STORAGE_PERMISSION: {
                elegirFoto();
                break;
            }
        }
    }

    private void elegirFoto (){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            Intent pickImage = new Intent(Intent.ACTION_PICK);
            pickImage.setType("image/*");
            startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
        }
    }

    private void takePicture() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageAdapter.agregarImagen(imageBitmap);
                    gridview.setAdapter(imageAdapter);
                }
                break;
            }

            case IMAGE_PICKER_REQUEST: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageAdapter.agregarImagen(selectedImage);
                        gridview.setAdapter(imageAdapter);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}

