package javeriana.compumovil.tcp.trivinho;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private ImageView logo;
    private Animation anima;
    private TextView registro;
    private Button inicio;

    private EditText mUser;
    private EditText mPassword;

    private static final String TAG="AUTENTICACIÓN";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, ReservaTerminada.class));


        mUser= (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.pass);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
// User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    startActivity(new Intent(MainActivity.this, UsuarioMainActivity.class));
                } else {
// User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        logo=(ImageView)findViewById(R.id.logo);
        registro=(EditText)findViewById(R.id.email);
        inicio=(Button)findViewById(R.id.bsesion);
        registro=(TextView)findViewById(R.id.registrarse);
        anima= AnimationUtils.loadAnimation(this,R.anim.iniciologo);
        logo.startAnimation(anima);

        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),RegistroActivity.class);
                startActivity(activar);
            }
        });

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });
    }


    private boolean validateForm() {
        boolean valid = true;
        String email = mUser.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mUser.setError("Requerido.");
            valid = false;
        } else {
            if (!Utils.isEmailValid(email)){
                mUser.setError("Email inválido.");
                valid = false;
            }
            else {
                mUser.setError(null);
            }
        }
        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Requerido.");
            valid = false;
        } else {
            mPassword.setError(null);
        }
        return valid;
    }

    protected void signInUser(){
        if(validateForm()){
            String email = mUser.getText().toString();
            String password = mPassword.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(MainActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                                mUser.setText("");
                                mPassword.setText("");
                            }
                        }
                    });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}