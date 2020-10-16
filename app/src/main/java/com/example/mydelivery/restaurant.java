package com.example.mydelivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;
import com.example.mydelivery.Api.UploadFile;
import com.example.mydelivery.Models.LoadAllImages;
import com.example.mydelivery.Models.Restaurante;
import com.example.mydelivery.Utils.CameraPhotoManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

public class restaurant extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private MapView mapview;
    private GoogleMap googleMap;
    private Geocoder geocoder = null;
    private LatLng miubicacion = null;
    private int CODE_CAMERA_LOGO = 1100;
    private int CODE_GALERY_LOGO = 1101;
    private String logoPath = null;
    private int CODE_CAMERA_FOTOLUGAR = 1200;
    private int CODE_GALERY_FOTOLUGAR = 1201;
    private String fotoLugarPath = null;

    private Restaurante restaurante = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        reviewPermissions();

        loadComponents();
        Intent intent = this.getIntent();

        if(intent.hasExtra("restaurantJson")){
            try {
                restaurante = new Restaurante(new JSONObject(intent.getStringExtra("restaurantJson")), new LoadAllImages() {
                    @Override
                    public void finishLoadImages(Object o) {
                        Restaurante r = (Restaurante) o;
                        logo.setImageBitmap(r.img_logo);
                        fotolugar.setImageBitmap(r.img_foto_lugar);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            editRestaurant();
        }

        initializeMap(savedInstanceState);
    }
    ImageView logo,fotolugar;
    EditText nombre,nit,propietario,direccion,telefono;
    Button crear;
    private void loadComponents() {
        nombre = findViewById(R.id.txt_rr_nombre);
        nit = findViewById(R.id.txt_rr_nit);
        propietario = findViewById(R.id.txt_rr_propietario);
        direccion = findViewById(R.id.txt_rr_direccion);
        telefono = findViewById(R.id.txt_rr_telefono);

        crear = findViewById(R.id.btn_rr_crear);
        crear.setOnClickListener(this);
        //imagenes
        logo = findViewById(R.id.img_rr_logo);
        fotolugar = findViewById(R.id.img_rr_fotolugar);
        logo.setOnClickListener(this);
        fotolugar.setOnClickListener(this);
    }

    public void editRestaurant(){
        nombre.setText(restaurante.nombre);
        nit.setText(restaurante.nit);
        propietario.setText(restaurante.propietario);
        direccion.setText(restaurante.direccion);
        telefono.setText(restaurante.telefono);
        miubicacion = new LatLng(restaurante.lat,restaurante.log);
        crear.setText("Guardar");
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            if(requestCode==CODE_CAMERA_LOGO){
                Bitmap imgbitmap=(Bitmap)data.getExtras().get("data");
                logoPath= CameraPhotoManager.saveToInternalStorage(this,imgbitmap);
                logo.setImageBitmap(BitmapFactory.decodeFile(logoPath));
            }
            if(requestCode==CODE_GALERY_LOGO){
                Uri uri=data.getData();
                logoPath=CameraPhotoManager.getRealPathFromURI(this,uri);
                logo.setImageBitmap(BitmapFactory.decodeFile(logoPath));
            }
            if(requestCode==CODE_CAMERA_FOTOLUGAR){
                Bitmap imgbitmap=(Bitmap)data.getExtras().get("data");
                fotoLugarPath=CameraPhotoManager.saveToInternalStorage(this,imgbitmap);
                fotolugar.setImageBitmap(BitmapFactory.decodeFile(fotoLugarPath));
            }
            if(requestCode==CODE_GALERY_FOTOLUGAR){
                Uri uri=data.getData();
                fotoLugarPath=CameraPhotoManager.getRealPathFromURI(this,uri);
                fotolugar.setImageBitmap(BitmapFactory.decodeFile(fotoLugarPath));
            }
        }
    }

    private boolean reviewPermissions() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }

        if(this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        requestPermissions(new String [] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_rr_logo:
                onClickLogo();
                break;
            case R.id.img_rr_fotolugar:
                onClickFotoLugar();
                break;
            case R.id.btn_rr_crear:
                try {
                    onClickCrear();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    private void irAAdministrador(){
        Intent i =new Intent(this,administrador.class);
        startActivity(i);
    }
    private void onClickCrear() throws JSONException {
        Resource restResource = new Resource("restaurant");
        JSONObject jo = new JSONObject();
        jo.put("nombre",nombre.getText().toString());
        jo.put("nit",nit.getText().toString());
        jo.put("propietario",propietario.getText().toString());
        jo.put("calle",direccion.getText().toString());
        jo.put("telefono",telefono.getText().toString());
        jo.put("log",String.valueOf(miubicacion.longitude));
        jo.put("lat",String.valueOf(miubicacion.latitude));
        ResourceHandler rh = new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                try {
                    Toast.makeText(getApplicationContext(),result.getString("msn"),Toast.LENGTH_LONG).show();
                    if(logoPath!=null)
                        uploadLogo(result.getJSONObject("doc").getString("_id"));
                    if(fotoLugarPath!=null)
                        uploadFotoLugar(result.getJSONObject("doc").getString("_id"));
                    irAAdministrador();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(JSONObject error) {
                try {
                    Toast.makeText(getApplicationContext(),error.getString("msn"),Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        if(restaurante!=null)
            restResource.patch(restaurante.id,jo, rh);
        else
            restResource.post(jo, rh);
    }

    public void uploadLogo(final String id){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    UploadFile.uploadRestaurantLogo(id, new File(logoPath), new ResourceHandler() {
                        @Override
                        public void onSucces(JSONObject result) {
                            try {
                                Toast.makeText(getApplicationContext(),result.getString("msn"),Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(JSONObject error) {
                            try {
                                Toast.makeText(getApplicationContext(),error.getString("msn"),Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void uploadFotoLugar(final String id){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    UploadFile.uploadRestaurantFotoLugar(id, new File(fotoLugarPath), new ResourceHandler() {
                        @Override
                        public void onSucces(JSONObject result) {
                            try {
                                Toast.makeText(getApplicationContext(),result.getString("msn"),Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(JSONObject error) {
                            try {
                                Toast.makeText(getApplicationContext(),error.getString("msn"),Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClickLogo(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("SELECCIONAR");
        alertDialog.setMessage("Seleccione el metodo");
        alertDialog.setButton("CAMARA",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),CODE_CAMERA_LOGO);
            }
        });
        alertDialog.setButton2("GALERIA",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(Intent.createChooser(intent,"Seleccione la Aplicacion"),CODE_GALERY_LOGO);
            }
        });
        alertDialog.show();
    }
    public void onClickFotoLugar(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("SELECCIONAR");
        alertDialog.setMessage("Seleccione el metodo");
        alertDialog.setButton("CAMARA",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),CODE_CAMERA_FOTOLUGAR);
            }
        });
        alertDialog.setButton2("GALERIA",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(Intent.createChooser(intent,"Seleccione la Aplicacion"),CODE_GALERY_FOTOLUGAR);
            }
        });
        alertDialog.show();
    }
}