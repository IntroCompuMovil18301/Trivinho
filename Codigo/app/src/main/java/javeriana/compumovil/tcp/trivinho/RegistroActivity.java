package javeriana.compumovil.tcp.trivinho;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javeriana.compumovil.tcp.trivinho.negocio.Anfitrion;
import javeriana.compumovil.tcp.trivinho.negocio.Huesped;
import javeriana.compumovil.tcp.trivinho.negocio.Usuario;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmail;
    private EditText mNombre;
    private EditText mApellido;
    private EditText mPassword;
    private EditText mEdad;
    private Spinner mPais;
    private Switch mEsHuesped;
    private Switch mEsAnfitrion;
    private Spinner mGenero;

    private Button elegirFoto;
    private Button tomarFoto;
    private ImageView mFoto;
    private Button salir;

    private Button registrarse;
    private String rutaFoto="";

    private static String TAG = "REGISTRO";
    private final static int CAMERA_PERMISSION = 1;
    private final static int EXTERNAL_STORAGE_PERMISSION = 2;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int IMAGE_PICKER_REQUEST = 2;



    private StorageReference mStorageRef;

    private UserProfileChangeRequest.Builder upcrb = new UserProfileChangeRequest.Builder();
    private FirebaseUser user;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mEmail = (EditText) findViewById(R.id.email);
        mNombre = (EditText) findViewById(R.id.nombre);
        mApellido = (EditText) findViewById(R.id.apellido);
        mPassword = (EditText) findViewById(R.id.pass);
        mEdad = (EditText) findViewById(R.id.edad);
        mPais = (Spinner) findViewById(R.id.pais);
        mEsHuesped = (Switch) findViewById(R.id.esHuesped);
        mEsAnfitrion = (Switch) findViewById(R.id.esAnfitrion);
        salir = (Button) findViewById(R.id.salir2);

        mGenero = (Spinner) findViewById(R.id.generospin);


        elegirFoto = (Button) findViewById(R.id.seleccionarImagen);
        tomarFoto = (Button) findViewById(R.id.tomarFoto);
        mFoto = (ImageView) findViewById(R.id.mFoto);

        registrarse = (Button) findViewById(R.id.registrarse);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();



        database= FirebaseDatabase.getInstance();


        tomarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission( RegistroActivity.this , Manifest.permission.CAMERA, "Se necesita usar la camara.", CAMERA_PERMISSION);
                takePicture();
            }
        });

        elegirFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission( RegistroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, "Se necesita acceder al almacenamiento externo.", EXTERNAL_STORAGE_PERMISSION);
                elegirFoto();
            }
        });

        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearCuenta();
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar =  new Intent(view.getContext(),MainActivity.class);
                startActivity(activar);
            }
        });

    }

    private boolean verificarCampos (){
        boolean validos=true;

        String email = mEmail.getText().toString();
        String nombre = mNombre.getText().toString();
        String apellido = mApellido.getText().toString();
        String password = mPassword.getText().toString();
        String edad = mEdad.getText().toString();
        String pais = mPais.getSelectedItem().toString();
        boolean esHuesped = mEsHuesped.isChecked();
        boolean esAnfitrion = mEsAnfitrion.isChecked();

        Bitmap foto = ((BitmapDrawable)mFoto.getDrawable()).getBitmap();
        Bitmap pordefecto  =((BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.usuario)).getBitmap();

        if(foto.equals(pordefecto)) {
            Toast.makeText(RegistroActivity.this, "La foto es obligatoria.", Toast.LENGTH_LONG).show();
            validos = false;
        }


        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Obligatorio.");
            validos = false;
        } else {
            if (!Utils.isEmailValid(email)){
                mEmail.setError("Email inválido.");
                validos = false;
            }
            else{
                mNombre.setError(null);
            }
        }

        if (TextUtils.isEmpty(nombre)) {
            mNombre.setError("Obligatorio.");
            validos = false;
        }
        else {
            mNombre.setError(null);
        }


        if (TextUtils.isEmpty(apellido)) {
            mApellido.setError("Obligatorio.");
            validos = false;
        }
        else {
            mApellido.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Obligatorio.");
            validos = false; //la contraseña debe tener al menos 8 caracteres
        }
        else {
            mPassword.setError(null);
        }

        if (TextUtils.isEmpty(edad)) {
            mEdad.setError("Obligatorio.");
            validos = false; //la contraseña debe tener al menos 8 caracteres
        }
        else {
            mEdad.setError(null);
        }

        if (!mEsHuesped.isChecked() && !mEsAnfitrion.isChecked()){
            validos = false;
            Toast.makeText(RegistroActivity.this, "El usuario debe ser huésped, anfitrión o ambos.", Toast.LENGTH_LONG).show();
        }

        //comprobar que la menos el huesped o el anfitrion estén seleccionados
        return validos;

    }


    private void crearCuenta (){
        if (verificarCampos()){
            final String email = mEmail.getText().toString();
            String password = mPassword.getText().toString();
            Log.d(TAG, "bien:");
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                                user = mAuth.getCurrentUser();

                                Log.d("here", "here" );
                                if (user != null) { //Update user Info
                                    upcrb.setDisplayName(mNombre.getText().toString() + " " + mApellido.getText().toString());
                                    uploadImage(user.getUid());

                                }
                            }
                            if (!task.isSuccessful()) {
                                Toast.makeText(RegistroActivity.this, R.string.auth_failed + task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, task.getException().getMessage());
                            }
                        }
                    });
        }
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


    @Override
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

    private void takePicture() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mFoto.setImageBitmap(imageBitmap);
                }
                break;
            }

            case IMAGE_PICKER_REQUEST: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        mFoto.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
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

    private void uploadImage (String UID){
        Bitmap foto = ((BitmapDrawable)mFoto.getDrawable()).getBitmap();
        final String rutafotoFireBase = "imagenes/usuarios/imagenesperfil/" +UID+ ".jgp";
        StorageReference fotoUsuario = mStorageRef.child(rutafotoFireBase);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        foto.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);

        fotoUsuario.putBytes(imageInByte)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        rutaFoto = rutafotoFireBase;
                        upcrb.setPhotoUri(Uri.parse(rutaFoto));
                        user.updateProfile(upcrb.build());
                        guardarInfoUsuario(); //una vez sube la foto sube la info del usuario

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

    private void guardarInfoUsuario (){
        Usuario usuario = new Usuario ();
        usuario.setRutaFoto(rutaFoto);
        usuario.setNombres(mNombre.getText().toString());
        usuario.setApellidos(mApellido.getText().toString());
        usuario.setEmail(mEmail.getText().toString());
        usuario.setEdad(Integer.parseInt(mEdad.getText().toString()));
        usuario.setPais(mPais.getSelectedItem().toString());
        usuario.setGenero(mGenero.getSelectedItem().toString());

        myRef = database.getReference(Utils.getPathUsers()+user.getUid());
        myRef.setValue(usuario);

        if (mEsHuesped.isChecked()) {
            Huesped huesped = new Huesped();
            huesped.setRutaFoto(rutaFoto);
            huesped.setNombres(mNombre.getText().toString());
            huesped.setApellidos(mApellido.getText().toString());
            huesped.setEmail(mEmail.getText().toString());
            huesped.setEdad(Integer.parseInt(mEdad.getText().toString()));
            huesped.setPais(mPais.getSelectedItem().toString());
            huesped.setGenero(mGenero.getSelectedItem().toString());

            myRef = database.getReference(Utils.getPathHuespedes() + user.getUid());
            myRef.setValue(huesped);
        }

        if (mEsAnfitrion.isChecked()) {
            Anfitrion anfitrion = new Anfitrion();
            anfitrion.setRutaFoto(rutaFoto);
            anfitrion.setNombres(mNombre.getText().toString());
            anfitrion.setApellidos(mApellido.getText().toString());
            anfitrion.setPais(mPais.getSelectedItem().toString());
            anfitrion.setEmail(mEmail.getText().toString());
            anfitrion.setEdad(Integer.parseInt(mEdad.getText().toString()));
            anfitrion.setPais(mPais.getSelectedItem().toString());
            anfitrion.setGenero(mGenero.getSelectedItem().toString());


            myRef = database.getReference(Utils.getPathAnfitriones() + user.getUid());
            myRef.setValue(anfitrion);
        }

        startActivity(new Intent(RegistroActivity.this, UsuarioMainActivity.class));


    }




}
