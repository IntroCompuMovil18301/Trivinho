package javeriana.compumovil.tcp.trivinho;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javeriana.compumovil.tcp.trivinho.negocio.Alojamiento;
import javeriana.compumovil.tcp.trivinho.negocio.FechaDisponible;
import javeriana.compumovil.tcp.trivinho.negocio.Reserva;

public class ReservarAlojamiento extends FragmentActivity implements OnMapReadyCallback  {

    private EditText mfechaInicio;
    private EditText mfechaFinal;

    private Boolean disponible;

    public static List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>();

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    private GoogleMap mMap;
    private double latitudActual;
    private double longitudActual;

    private Alojamiento alojamiento;
    private static final int ID_PERMISSION_LOCATION = 1;
    private static final int REQUEST_CHECK_SETTINGS = 1;

    private static boolean ubicacionInicialColocada = false;
    private Button reservar;

    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue request;



    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private DatabaseReference myRef2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_alojamiento);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapp3);
        mapFragment.getMapAsync(this);

        database= FirebaseDatabase.getInstance();

        mfechaInicio = (EditText) findViewById(R.id.fechaInicio3);
        mfechaFinal = (EditText) findViewById(R.id.fechaFinal3);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();

        reservar = (Button) findViewById(R.id.agregarsitio);

        request= Volley.newRequestQueue(getApplicationContext());

        alojamiento = (Alojamiento) getIntent().getSerializableExtra("alojamiento");
        //Log.i("INICIADO", alojamiento.getId());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                Log.i("LOCATION", "Location update in the callback: " + location);
                if (location != null) {
                    latitudActual = location.getLatitude();
                    longitudActual = location.getLongitude();
                    Log.i ("Coordenada Actuales", latitudActual+" "+longitudActual);

                    if (!ubicacionInicialColocada && mMap!=null) {
                        webServiceObtenerRuta(latitudActual, longitudActual, alojamiento.getLatitud(), alojamiento.getLongitud());
                        ubicarMapaPosicion(latitudActual, longitudActual);
                        colocarAlojamiento(alojamiento);
                        ubicacionInicialColocada = true;
                    }
                }
            }
        };


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

        reservar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completarReserva();
            }
        });



    }

    private void completarReserva (){
        if (validarCampos()){
            reservaDisponible();
        }
    }

    private void reservaDisponible (){
        for (FechaDisponible fechaDisponible: alojamiento.getFechasDisponibles()) {
            if (alojamientoDisponible(fechaDisponible)) {
                myRef = database.getReference(Utils.getPathReservas() + alojamiento.getId());
                String key = myRef.push().getKey();
                myRef = database.getReference(Utils.getPathReservas() + alojamiento.getId() + key);
                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                    String fechaInicio = mfechaInicio.getText().toString();
                    String fechaFinal = mfechaFinal.getText().toString();

                    Calendar inicio_date = Calendar.getInstance();
                    Calendar final_date = Calendar.getInstance();
                    inicio_date.setTime(format.parse(fechaInicio));
                    final_date.setTime(format.parse(fechaFinal));

                    Reserva reserva = new Reserva();
                    reserva.setAnioInicio(inicio_date.get(Calendar.YEAR));
                    reserva.setMesInicio(inicio_date.get(Calendar.MONTH) + 1);
                    reserva.setDiaInicio(inicio_date.get(Calendar.DAY_OF_MONTH));


                    reserva.setAnioFinal(final_date.get(Calendar.YEAR));
                    reserva.setMesFinal(final_date.get(Calendar.MONTH) + 1);
                    reserva.setDiaFinal(final_date.get(Calendar.DAY_OF_MONTH));
                    myRef2.setValue(reserva);

                    Toast.makeText(ReservarAlojamiento.this, "Reserva realizada con éxito.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ReservarAlojamiento.this, UsuarioMainActivity.class);
                    startActivity(intent);
                    return;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        Toast.makeText(this, "El alojamiento no está disponible o está reservado en estas fechas.", Toast.LENGTH_LONG).show();
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i("ENTRO: ", "MAPA MOSTRADO");
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
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
                                        resolvable.startResolutionForResult(ReservarAlojamiento.this,
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
        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Ubicación actual")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
    }

    private void webServiceObtenerRuta(Double latitudInicial, Double longitudInicial, Double latitudFinal, Double longitudFinal) {

        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+String.valueOf(latitudInicial)+
                ","+String.valueOf(longitudInicial)+"&destination="+String.valueOf(latitudFinal)+","+
                String.valueOf(longitudFinal)+"&key=AIzaSyAqYCyHsdafKZuGtUus62G1JqV3wb8DHTw";

        Log.i("ruta:", "peticion enviada");

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;

                try {

                    jRoutes = response.getJSONArray("routes");

                    /** Traversing all routes */
                    for(int i=0;i<jRoutes.length();i++){

                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        /** Traversing all legs */
                        for(int j=0;j<jLegs.length();j++){
                            jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for(int k=0;k<jSteps.length();k++){
                                String polyline = "";
                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */
                                for(int l=0;l<list.size();l++){
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                    path.add(hm);
                                }
                            }
                            routes.add(path);
                        }
                        dibujarRuta();
                        Log.i("ruta: ", "peticion respondida");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        }
        );

        request.add(jsonObjectRequest);
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void dibujarRuta(){
        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // setUpMapIfNeeded();

        // recorriendo todas las rutas
        for(int i=0;i<routes.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            //Definimos el grosor de las Polilíneas
            lineOptions.width(9);
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.GREEN);

            mMap.addPolyline(lineOptions);
        }
    }

    private void colocarAlojamiento (Alojamiento alojamiento){
        LatLng ubicacion = new LatLng(alojamiento.getLatitud(), alojamiento.getLongitud());
        Marker amarker = mMap.addMarker(new MarkerOptions().position(ubicacion).title("Alojamiento")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marcadorcasa)));
    }


    public List<List<HashMap<String,String>>> parse(JSONObject jObject){
        //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
        //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
        //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return routes;
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
                    Toast.makeText(ReservarAlojamiento.this, "Las fechas ingresadas no son validas.", Toast.LENGTH_LONG).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return validos;
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


}
