package javeriana.compumovil.tcp.trivinho;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class ReservarAlojamiento extends AppCompatActivity {

    private EditText mfechaInicio;
    private EditText mfechaFinal;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_alojamiento);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync((OnMapReadyCallback) this);


        mfechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                ((DatePickerFragment) newFragment).setCampo((EditText) findViewById(R.id.fechaInicio3));
                newFragment.show(getFragmentManager(), "Date Picker");
            }
        });

        mfechaFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                ((DatePickerFragment) newFragment).setCampo((EditText) findViewById(R.id.fechaFinal3));
                newFragment.show(getFragmentManager(), "Date Picker");
            }
        });

    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }


}
