package javeriana.compumovil.tcp.trivinho;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.SitioDeInteres;

public class AgregarAlojamientoSitiosInteresActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Alojamiento alojamiento;
    private Button agregarSitio;
    private Spinner tipoSitio;
    private EditText descripcionSitio;
    private Button siguiente;


    List<Marker> sitios;
    List<SitioDeInteres> sitiosDeInteres;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_alojamiento_sitios_interes);

        alojamiento = (Alojamiento) getIntent().getSerializableExtra("alojamiento");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapp4);
        mapFragment.getMapAsync(this);

        sitiosDeInteres = new ArrayList<SitioDeInteres>();
        sitios = new ArrayList<Marker>();

        agregarSitio = (Button) findViewById(R.id.agregarsitio);

        tipoSitio = (Spinner) findViewById(R.id.tiposi);
        descripcionSitio = (EditText) findViewById(R.id.descripcionsi);
        siguiente = (Button) findViewById(R.id.siguiente6);



        agregarSitio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarMarcador();
            }
        });

        siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarSitiosDeInteres();
            }
        });

    }

    private void guardarSitiosDeInteres(){
        boolean valido =  verificarSitios();
        Log.i("VALIDO:", String.valueOf(valido));
        if( verificarSitios()){
            for (Marker marker: sitios){
                SitioDeInteres sitioDeInteres = (SitioDeInteres) marker.getTag();
                sitioDeInteres.setLatitud(marker.getPosition().latitude);
                sitioDeInteres.setLongitud(marker.getPosition().longitude);
                sitiosDeInteres.add(sitioDeInteres);
            }
            alojamiento.setSitiosDeInteres(sitiosDeInteres);
            Intent intent = new Intent (this, AgregarAlojamientoFechasActivity.class);
            intent.putExtra("alojamiento", alojamiento);
            startActivity(intent);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                         @Override
                                         public void onMarkerDragStart(Marker marker) {

                                         }

                                         @Override
                                         public void onMarkerDrag(Marker marker) {

                                         }

                                         @Override
                                         public void onMarkerDragEnd(Marker marker) {
                                         }});

                ubicarMapaPosicion(alojamiento.getLatitud(), alojamiento.getLongitud());
    }

    private void ubicarMapaPosicion (double latitud, double longitud){
        LatLng ubicacion = new LatLng(latitud, longitud);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Alojamiento")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadorcasa)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
    }

    private void agregarMarcador(){
        //CAMBIAR ICONO Y TITULO DEPENDIENDO DEL SPINNER
        if (sitiosDeInteres.size()<=8) {
            LatLng ubicacion = new LatLng(alojamiento.getLatitud(), alojamiento.getLongitud());

            SitioDeInteres sitioDeInteres = new SitioDeInteres();
            sitioDeInteres.setDescripcion(descripcionSitio.getText().toString());
            descripcionSitio.setText("");

            if (tipoSitio.getSelectedItem().toString().equals("Gimnasio")) {
                sitios.add(mMap.addMarker(new MarkerOptions().position(ubicacion).title("Gimnasio").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadorgimnasio))));
                sitioDeInteres.setTipo("Gimnasio");
            }

            if (tipoSitio.getSelectedItem().toString().equals("Transporte")) {
                sitios.add(mMap.addMarker(new MarkerOptions().position(ubicacion).title("Transporte").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadortransporte))));
                sitioDeInteres.setTipo("Transporte");
            }

            if (tipoSitio.getSelectedItem().toString().equals("Restaurante")) {
                sitios.add(mMap.addMarker(new MarkerOptions().position(ubicacion).title("Restaurante").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadorrestaurante))));
                sitioDeInteres.setTipo("Restaurante");
            }

            if (tipoSitio.getSelectedItem().toString().equals("Cajero")) {
                sitios.add(mMap.addMarker(new MarkerOptions().position(ubicacion).title("Cajero").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadordinero))));
                sitioDeInteres.setTipo("Cajero");
            }

            if (tipoSitio.getSelectedItem().toString().equals("Farmacia")) {
                sitios.add(mMap.addMarker(new MarkerOptions().position(ubicacion).title("Farmacia").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadorenfermeria))));
                sitioDeInteres.setTipo("Farmacia");
            }

            if (tipoSitio.getSelectedItem().toString().equals("Supermercado")) {
                sitios.add(mMap.addMarker(new MarkerOptions().position(ubicacion).title("Supermercado").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadormercado))));
                sitioDeInteres.setTipo("Supermercado");
            }

            if (tipoSitio.getSelectedItem().toString().equals("Centro comercial")) {
                sitios.add(mMap.addMarker(new MarkerOptions().position(ubicacion).title("Centro comercial").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadortienda))));
                sitioDeInteres.setTipo("Centro comercial");
            }

            if (tipoSitio.getSelectedItem().toString().equals("Aeropuerto")) {
                sitios.add(mMap.addMarker(new MarkerOptions().position(ubicacion).title("Aeropuerto").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadoraeropuerto))));
                sitioDeInteres.setTipo("Aeropuerto");
            }

            if (tipoSitio.getSelectedItem().toString().equals("Otros")) {
                sitios.add(mMap.addMarker(new MarkerOptions().position(ubicacion).title("Otros").draggable(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadorotros))));
                sitioDeInteres.setTipo("Otros");
            }
            sitios.get(sitios.size() - 1).setTag(sitioDeInteres);
        }
        else{
            Toast.makeText(this, "Puede agregar máximo 8 sitios de interés.", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean verificarSitios(){
        for (Marker marker: sitios){
            Log.i("POSICION", String.valueOf(marker.getPosition().latitude));
            if (marker.getPosition().latitude == alojamiento.getLatitud() && marker.getPosition().longitude == alojamiento.getLongitud()){
                Toast.makeText(AgregarAlojamientoSitiosInteresActivity.this, "No pueden haber sitios de interés sobre el alojamiento.", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        Log.i("LLAMADO", "SI");
        return true;
    }


}
