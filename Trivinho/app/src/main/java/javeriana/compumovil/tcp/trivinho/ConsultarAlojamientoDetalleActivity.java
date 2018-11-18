package javeriana.compumovil.tcp.trivinho;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;

public class ConsultarAlojamientoDetalleActivity extends AppCompatActivity {

    private Button versitiosInteres;
    private Button reservar;

    private TextView tipo;
    private TextView descripcion;
    private TextView moneda;
    private TextView valor;

    private RatingBar calificacion;

    private Alojamiento alojamiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_alojamiento);
        versitiosInteres=(Button) findViewById(R.id.sitiosDeInteres);
        reservar=(Button) findViewById(R.id.detalleReservar);
        tipo = (TextView) findViewById(R.id.detalleTipo);
        descripcion = (TextView) findViewById(R.id.detalleDescripcion);
        moneda = (TextView) findViewById(R.id.detalleMoneda) ;
        valor = (TextView) findViewById(R.id.detalleValor);
        calificacion = (RatingBar) findViewById(R.id.detalleRatingBar);

        alojamiento = (Alojamiento) getIntent().getSerializableExtra("alojamiento");

        tipo.setText(alojamiento.getTipo());
        descripcion.setText(alojamiento.getDescripcion());
        valor.setText(Double.toString(alojamiento.getValorPorNoche()));
        moneda.setText(alojamiento.getTipoMoneda());
        calificacion.setRating(alojamiento.getPuntaje());



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
