package javeriana.compumovil.tcp.trivinho;

import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final String TAG = "CustomInfoWindowAdapter";
    private LayoutInflater inflater;

    public CustomInfoWindowAdapter(LayoutInflater inflater){
        this.inflater = inflater;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = inflater.inflate(R.layout.infowindowconstum, null);
        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }
}
