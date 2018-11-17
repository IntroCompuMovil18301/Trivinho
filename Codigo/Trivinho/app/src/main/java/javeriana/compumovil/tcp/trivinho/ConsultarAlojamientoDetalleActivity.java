package javeriana.compumovil.tcp.trivinho;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ConsultarAlojamientoDetalleActivity extends AppCompatActivity {

    Button versitiosInteres;
    Button reservar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_alojamiento);
        versitiosInteres=(Button) findViewById(R.id.sitiosDeInteres);
        reservar=(Button) findViewById(R.id.reservar);

        versitiosInteres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),VerSitiosDeInteres.class);
                startActivity(activar);
            }
        });

        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar=new Intent(view.getContext(),ReservarAlojamiento.class);
                startActivity(activar);
            }
        });
    }
}
