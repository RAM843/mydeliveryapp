package com.example.mydelivery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydelivery.Adapters.OnChange;
import com.example.mydelivery.Adapters.OnSelectItem;
import com.example.mydelivery.Adapters.PedidoItemAdapter;
import com.example.mydelivery.Api.JsonHelper;
import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;
import com.example.mydelivery.Models.Pedido;
import com.example.mydelivery.Utils.LocationTrack;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RealizarPedido extends AppCompatActivity implements OnMapReadyCallback {
    private MapView mapview;
    private GoogleMap googleMap;
    private LatLng miubicacion = null;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList();
    private ArrayList<String> permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realizar_pedido);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }


        initializeMap(savedInstanceState);
        loadComponents();
    }
    private GridView lista;
    private TextView total;
    private Button btnPedir;
    private PedidoItemAdapter listaAdapter;
    private void loadComponents() {
        lista = findViewById(R.id.gv_rp_lista);
        total = findViewById(R.id.txt_rp_total);
        btnPedir = findViewById(R.id.btn_rp_pedir);
        btnPedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pedir();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        total.setText("Total: "+calcularTotal()+" Bs.");
        listaAdapter = new PedidoItemAdapter(this, Sesion.carrito, new OnChange() {
            @Override
            public void onChange(BaseAdapter ba) {
                lista.setAdapter(ba);
            }
        }, new OnSelectItem(){
            @Override
            public void onSelect(Object item) {
                total.setText("Total: "+calcularTotal()+" Bs.");
            }
        });
        lista.setAdapter(listaAdapter);
    }

    public void pedir() throws JSONException {
        JSONObject le = new JSONObject();
        le.put("lat",miubicacion.latitude);
        le.put("log",miubicacion.longitude);
        JSONObject jo = new JSONObject();
        jo.put("idcliente",Sesion.usuario.id);
        jo.put("pago_total",calcularTotal());
        jo.put("lugardeenvio",le.toString());

        JSONArray pedidos = new JSONArray();
        for (Pedido p: Sesion.carrito) {
            pedidos.put(p.getJson());
        }
        jo.put("pedidos",pedidos.toString());
        Resource ro = new Resource("orden");
        ro.post(jo, new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Pedido realizado exitosamente!!",Toast.LENGTH_SHORT).show();
                    }
                });
                Sesion.carrito.clear();
                irAAdministrador();

            }

            @Override
            public void onFailure(final JSONObject error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(getApplicationContext(),error.getString("msn"),Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private double calcularTotal() {
        double total = 0;
        for (Pedido p: Sesion.carrito){
            total+=p.getCantidad()*p.getMenu().precio;
        }
        return total;
    }

    public void irAAdministrador(){
        Intent i = new Intent(this,administrador.class);
        startActivity(i);
    }

    public void initializeMap(Bundle sis){
        mapview = findViewById(R.id.map_rp_mapadir);
        mapview.onCreate(sis);
        mapview.onResume();
        MapsInitializer.initialize(this);
        mapview.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap gm) {
        this.googleMap = gm;
        locationTrack = new LocationTrack(this);
        double longitude,latitude;
        if (locationTrack.canGetLocation()) {
            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();
        } else {
            longitude = -65.7559122;
            latitude =  -19.5730936;
            locationTrack.showSettingsAlert();
        }

        LatLng potosi =new LatLng(latitude, longitude);
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


    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission(perm.toString())) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }
}
