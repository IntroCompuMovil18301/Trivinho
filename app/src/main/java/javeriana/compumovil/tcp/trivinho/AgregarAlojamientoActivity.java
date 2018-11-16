package javeriana.compumovil.tcp.trivinho;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;


public class AgregarAlojamientoActivity extends AppCompatActivity {

    private Button siguiente;
    private Spinner mTipoAlojamiento;
    private EditText mValorNoche;
    private Spinner mTipoMoneda;
    private EditText mDescripcion;
    private Button salir;
    private Button inicio;



    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_alojamiento);


        mTipoAlojamiento = (Spinner) findViewById(R.id.tipoalojamiento);
        mValorNoche = (EditText) findViewById(R.id.valorpornoche);
        mTipoMoneda = (Spinner) findViewById(R.id.tipomoneda);
        mDescripcion = (EditText) findViewById(R.id.descripcion);
        salir = (Button) findViewById(R.id.salir3);
        inicio = (Button) findViewById(R.id.button6);

        mAuth = FirebaseAuth.getInstance();

        siguiente = (Button) findViewById(R.id.siguiente3);
        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerInformacion();
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(AgregarAlojamientoActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar = new Intent(view.getContext(),UsuarioMainActivity.class);
                startActivity(activar);
            }
        });

    }

    private void obtenerInformacion (){
        if (validarCampos()) {
            String valorNoche = mValorNoche.getText().toString();
            String descripcion = mDescripcion.getText().toString();
            String tipoMoneda = mTipoMoneda.getSelectedItem().toString();
            String tipoAlojamiento = mTipoAlojamiento.getSelectedItem().toString();

            Intent intent = new Intent(this, AgregarAlojamientoFotosActivity.class);

            Alojamiento alojamiento = new Alojamiento();
            alojamiento.setDescripcion(descripcion);
            alojamiento.setValorPorNoche(Double.parseDouble(valorNoche));
            alojamiento.setTipo(tipoAlojamiento);
            alojamiento.setTipoMoneda(tipoMoneda);

            intent.putExtra("alojamiento", alojamiento);
            startActivity(intent);
        }

    }
    private boolean validarCampos (){
        boolean validos = true;
        String valorNoche = mValorNoche.getText().toString();
        String descripcion = mDescripcion.getText().toString();
        if (valorNoche.isEmpty()) {
            validos = false;
            mValorNoche.setError ("Obligatoria.");
        }
        else{
            mValorNoche.setError (null);
        }

        if (descripcion.isEmpty()) {
            validos = false;
            mDescripcion.setError ("Obligatoria.");
        }
        else{
            mDescripcion.setError (null);
        }
        return validos;
    }
}
