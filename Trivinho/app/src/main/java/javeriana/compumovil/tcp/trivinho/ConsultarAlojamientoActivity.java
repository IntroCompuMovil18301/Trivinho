package javeriana.compumovil.tcp.trivinho;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.FechaDisponible;
import javeriana.compumovil.tcp.trivinho.negocio.FotoAlojamiento;

public class ConsultarAlojamientoActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button filtrarBusqueda;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private static final int ID_PERMISSION_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 1;

    private static boolean ubicacionInicialColocada = false;

    public static final int RADIUS_OF_EARTH_KM = 6371;


    private double latitudActual;
    private double longitudActual;

    private Button buscar;
    private Button reservar;

    private double latitudBusqueda;
    private double longitudBusqueda;

    private Geocoder mGeocoder;
    private Button siguiente;
    private EditText mAddress;

    private EditText mfechaInicio;
    private EditText mfechaFinal;

    private DatabaseReference myRef;
    private DatabaseReference myRef2;
    private FirebaseDatabase database;

    private Button salir;
    private Button inicio;

    private Alojamiento alojamientoSeleccionado;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);


        salir = (Button) findViewById(R.id.salir7);
        inicio = (Button) findViewById(R.id.button5);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        filtrarBusqueda = (Button) findViewById(R.id.filtrarBusqueda);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();

        mfechaInicio = (EditText) findViewById(R.id.fechaInicio2);
        mfechaFinal = (EditText) findViewById(R.id.fechaFinal2);

        buscar = (Button) findViewById(R.id.buscar2);
        reservar = (Button) findViewById(R.id.reservar);

        mAddress = (EditText) findViewById(R.id.ubicacion2);
        mGeocoder = new Geocoder(getBaseContext());

        inicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activar = new Intent(view.getContext(),UsuarioMainActivity.class);
                startActivity(activar);
            }
        });

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    latitudActual = location.getLatitude();
                    longitudActual = location.getLongitude();
                    Log.i("Coordenada Actuales", latitudActual + " " + longitudActual);

                    if (!ubicacionInicialColocada) {
                        ubicarMapaPosicion(latitudActual, longitudActual);
                        ubicacionInicialColocada = true;
                    }
                }
            }
        };

        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "El permiso es necesario para acceder a la localización.", ID_PERMISSION_LOCATION);
        startLocationUpdates();

        mfechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                ((DatePickerFragment) newFragment).setCampo((EditText) findViewById(R.id.fechaInicio2));
                newFragment.show(getFragmentManager(), "Date Picker");
            }
        });

        mfechaFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                ((DatePickerFragment) newFragment).setCampo((EditText) findViewById(R.id.fechaFinal2));
                newFragment.show(getFragmentManager(), "Date Picker");
            }
        });

        mAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    buscarUbicacion();
                }
                return false;
            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarUbicacion();
            }
        });

        filtrarBusqueda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    cargarAlojamientosFecha();
                }
            }
        });

        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservar();
            }
        });



        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(ConsultarAlojamientoActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(ConsultarAlojamientoActivity.this));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                if (marker.getTag()!= null)
                    alojamientoSeleccionado = (Alojamiento) marker.getTag();
                else{
                    alojamientoSeleccionado = null;
                }
                return true;
            }
        });

    }

    private void reservar(){
        if (alojamientoSeleccionado!=null) {
            Intent intent = new Intent(ConsultarAlojamientoActivity.this, ReservarAlojamiento.class);
            intent.putExtra("alojamiento", alojamientoSeleccionado);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "Debe seleccionar un alojamiento.", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarUbicacion() {
        String addressString = mAddress.getText().toString();
        if (!addressString.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(addressString, 5);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    LatLng position = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                    if (mMap != null) {
                        ubicarMapaPosicion(position.latitude, position.longitude);
                    }
                } else {
                    Toast.makeText(this, "Dirección no encontrada.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "La dirección esta vacía.", Toast.LENGTH_SHORT).show();
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000); //tasa de refresco en milisegundos
        mLocationRequest.setFastestInterval(5000); //máxima tasa de refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


    private void requestPermission(Activity context, String permission, String explanation, int requestId) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?Â  Â
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                Toast.makeText(context, explanation, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestId);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ID_PERMISSION_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Ya hay permiso para acceder a la localización", Toast.LENGTH_LONG).show();
                    startLocationUpdates();
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
                    SettingsClient client = LocationServices.getSettingsClient(this);
                    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


                    task.addOnFailureListener(this, new OnFailureListener() {
                        public void onFailure(@NonNull Exception e) {
                            Log.i("Ubicación desactivada", "si");
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case CommonStatusCodes.RESOLUTION_REQUIRED:
// Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                                    try {// Show the dialog by calling startResolutionForResult(), and check the result in onActivityResult().
                                        ResolvableApiException resolvable = (ResolvableApiException) e;
                                        resolvable.startResolutionForResult(ConsultarAlojamientoActivity.this,
                                                REQUEST_CHECK_SETTINGS);
                                    } catch (IntentSender.SendIntentException sendEx) {
// Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
// Location settings are not satisfied. No way to fix the settings so we won't show the dialog.
                                    break;
                            }
                        }
                    });

                } else {
                    Toast.makeText(this, "No hay Permiso", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS: {
                if (resultCode == RESULT_OK) {
                    startLocationUpdates(); //Se encendió la localización!!!
                } else {
                    Toast.makeText(this,
                            "Sin acceso a localización, hardware deshabilitado!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    protected void onResume() {
        super.onResume();
        ubicacionInicialColocada = false;
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void ubicarMapaPosicion(double latitud, double longitud) {
        LatLng ubicacion = new LatLng(latitud, longitud);
        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
        latitudBusqueda = latitud;
        longitudBusqueda = longitud;
        dibujarCirculo2km();
        cargarAlojamientos();
    }

    public void cargarAlojamientos() {
        myRef = database.getReference(Utils.getPathAlojamientos());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Alojamiento alojamiento = singleSnapshot.getValue(Alojamiento.class);
                    if (distance(latitudBusqueda, longitudBusqueda, alojamiento.getLatitud(), alojamiento.getLongitud()) <= 2) {
                        alojamiento.setId(singleSnapshot.getKey());
                        colocarAlojamiento(alojamiento);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ErrorDB", "error en la consulta", databaseError.toException());
            }
        });
    }

    public void cargarAlojamientosFecha() {
        //myRef.removeEventListener();
        Toast.makeText(this, "Filtrando alojamientos disponibles para las fechas indicadas.", Toast.LENGTH_LONG).show();
        mMap.clear();
        LatLng ubicacion = new LatLng(latitudBusqueda, longitudBusqueda);
        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Ubicación central")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        dibujarCirculo2km();

        myRef = database.getReference(Utils.getPathAlojamientos());
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshotAlojamiento) {
                for (final DataSnapshot singleSnapshotAlojamiento : dataSnapshotAlojamiento.getChildren()) {
                    final Alojamiento alojamiento = singleSnapshotAlojamiento.getValue(Alojamiento.class);
                    if (distance(latitudBusqueda, longitudBusqueda, alojamiento.getLatitud(), alojamiento.getLongitud()) <= 2) {
                            for (FechaDisponible fechaDisponible : alojamiento.getFechasDisponibles()) {
                                if (alojamientoDisponible(fechaDisponible)) {
                                    alojamiento.setId(singleSnapshotAlojamiento.getKey());
                                    colocarAlojamiento(alojamiento);
                                }
                            }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ErrorDB", "error en la consulta", databaseError.toException());
            }
        });
    }


    private double distance(double lat1, double long1, double lat2, double long2) {
        double latDistance = Math.toRadians(lat1 - lat2);
        double lngDistance = Math.toRadians(long1 - long2);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = RADIUS_OF_EARTH_KM * c;
        return Math.round(result * 100.0) / 100.0;
    }

    private boolean alojamientoDisponible (FechaDisponible fechaUsuario){
        try{
            Calendar fechaalojamientoInicio = Calendar.getInstance();
            fechaalojamientoInicio.set(Calendar.YEAR, fechaUsuario.getAnioInicio());
            fechaalojamientoInicio.set(Calendar.MONTH, fechaUsuario.getMesInicio()-1);
            fechaalojamientoInicio.set(Calendar.DAY_OF_MONTH, fechaUsuario.getDiaInicio()-1);
            Log.i("FECHA INICIAL", fechaalojamientoInicio.toString());

            Calendar fechaalojamientoFinal = Calendar.getInstance();
            fechaalojamientoFinal.set(Calendar.YEAR, fechaUsuario.getAnioFinal());
            fechaalojamientoFinal.set(Calendar.MONTH, fechaUsuario.getMesFinal()-1);
            fechaalojamientoFinal.set(Calendar.DAY_OF_MONTH, fechaUsuario.getDiaFinal()+1);
            Log.i("FECHA FINAL", fechaalojamientoFinal.toString());


            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fechaInicio = mfechaInicio.getText().toString();
            String fechaFinal = mfechaFinal.getText().toString();



            Calendar fechafiltroInicio = Calendar.getInstance();;
            Calendar fechafiltroFinal = Calendar.getInstance();;
            fechafiltroInicio.setTime(format.parse(fechaInicio));
            fechafiltroFinal.setTime(format.parse(fechaFinal));

            Log.i("FECHA FILTRO INICIAL", fechafiltroInicio.toString());
            Log.i("FECHA FILTRO FINAL", fechafiltroFinal.toString());

            if ( fechaalojamientoInicio.before(fechafiltroInicio)
                    && fechaalojamientoFinal.after(fechafiltroFinal) ){
                Log.i("RETORNO", "true");
                return true;

            }


        } catch (ParseException e) {
        e.printStackTrace();
        }

        return false;

    }

    private void dibujarCirculo2km (){
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitudBusqueda, longitudBusqueda))
                .radius(2000) //metros
                .strokeWidth(10)
                .strokeColor(Color.rgb(76,175,80))
                .fillColor(Color.argb(85, 57, 130, 60))
                .clickable(true);
        mMap.addCircle(circleOptions);
    }

    private void colocarAlojamiento (Alojamiento alojamiento){
        LatLng ubicacion = new LatLng(alojamiento.getLatitud(), alojamiento.getLongitud());
        Marker amarker = mMap.addMarker(new MarkerOptions().position(ubicacion).title(alojamiento.getTipo())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadorcasa)));
        amarker.setTag(alojamiento);
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
                    Toast.makeText(this, "Las fechas ingresadas no son validas.", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return validos;
    }

}
