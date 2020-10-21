package com.example.mydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Geocoder;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class RealizarPedido extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapview;
    private GoogleMap googleMap;
    private Geocoder geocoder = null;
    private LatLng miubicacion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_pedido);

        //initializeMap(savedInstanceState);
    }

    public void initializeMap(Bundle sis){
        mapview = findViewById(R.id.map_rr_ubicacion);
        mapview.onCreate(sis);
        mapview.onResume();
        MapsInitializer.initialize(this);
        mapview.getMapAsync(this);
        geocoder =new Geocoder(getBaseContext(), Locale.getDefault());
    }
    @Override
    public void onMapReady(GoogleMap gm) {
        this.googleMap = gm;
        LatLng potosi =new LatLng(-19.5730936, -65.7559122);
        if(miubicacion==null)
            miubicacion = potosi;
        googleMap.addMarker(new MarkerOptions().position(miubicacion).title("Lugar").zIndex(18).draggable(true));
        googleMap.setMinZoomPreference(15);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(miubicacion));
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                miubicacion = marker.getPosition();
            }
        });
    }
}