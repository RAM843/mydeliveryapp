package com.example.mydelivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelStoreOwner;

import android.Manifest;
import  android.app.AlertDialog;
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
import com.example.mydelivery.Api.Uploadfile;
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

public class restaurant extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private MapView mapview;
    private GoogleMap googleMap;
    private Geocoder geocoder = null;
    private LatLng miubicacion = null;
    private  int CODE_CAMERA_LOGO = 1100;
    private  int CODE_GALERY_LOGO = 1101;
    private String logoPath= null;
    private  int CODE_CAMERA_FOTOLUGAR = 1200;
    private  int CODE_GALERY_FOTOLUGAR = 1201;
    private String FotoLugarPath= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        reviewPermissions();
        initializeMap(savedInstanceState);
        loadComponents();
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
        logo =findViewById(R.id.img_rr_logo);
        fotolugar =findViewById(R.id.img_rr_fotolugar);
        logo.setOnClickListener(this);
        fotolugar.setOnClickListener(this);


    }


    public void initializeMap(Bundle sis){
        mapview = findViewById(R.id.map_rr_ubicacion);
        mapview.onCreate(sis);
        mapview.onResume();
        MapsInitializer.initialize(this);
        mapview.getMapAsync(this);
        geocoder = new Geocoder(getBaseContext(), Locale.getDefault());

    }

    @Override
    public void onMapReady(GoogleMap gm) {
        this.googleMap = gm;
        LatLng potosi = new LatLng(-19-5730936,-65.7559122);
        miubicacion = potosi;
        googleMap.addMarker (new MarkerOptions (). position (potosi) .title ("Lugar"). zIndex (18) .draggable (true));
        googleMap.setMinZoomPreference (15);
        googleMap.moveCamera (CameraUpdateFactory.newLatLng (potosi));
        googleMap.setOnMarkerDragListener (new GoogleMap.OnMarkerDragListener () {
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
                logoPath=saveToInternalStorage(imgbitmap);
                logo.setImageBitmap(BitmapFactory.decodeFile(logoPath));
            }
           if(requestCode==CODE_GALERY_LOGO){
               Uri uri=data.getData();
               logoPath=getRealPathFromURI(uri);
               logo.setImageBitmap(BitmapFactory.decodeFile(logoPath));
            }
            if(requestCode==CODE_CAMERA_FOTOLUGAR){
                Bitmap imgbitmap=(Bitmap)data.getExtras().get("data");
                FotoLugarPath=saveToInternalStorage(imgbitmap);
                fotolugar.setImageBitmap(BitmapFactory.decodeFile(FotoLugarPath));
            }
            if(requestCode==CODE_GALERY_FOTOLUGAR){
                Uri uri=data.getData();
                FotoLugarPath=getRealPathFromURI(uri);
                fotolugar.setImageBitmap(BitmapFactory.decodeFile(FotoLugarPath));
            }
        }
    }
    public String getRealPathFromURI(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
            cursor.close();
            return result;
        }
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        result = cursor.getString(idx);
        cursor.close();
        return result;
    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String path = directory.getAbsolutePath() + "/profile.jpg";
        return path;
        //return directory.getAbsolutePath();
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
                onClockFotoLugar();
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

    private void onClickCrear() throws JSONException {
        Resource restResource = new Resource("restaurant");
        JSONObject jo = new JSONObject();
        jo.put("nombre",nombre.getText().toString());
        jo.put("nit",nit.getText().toString());
        jo.put("propietario",propietario.getText().toString());
        jo.put("calle",direccion.getText().toString());
        jo.put("telefono",telefono.getText().toString());
        jo.put("lat",String.valueOf(miubicacion.latitude));
        jo.put("log",String.valueOf(miubicacion.latitude));
        restResource.post(jo, new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                try {
                    Toast.makeText(getApplicationContext(),result.getString("msn"),Toast.LENGTH_LONG).show();
                    uploadLogo(result.getJSONObject("doc").getString("_id"));
                    uploadFotoLugar(result.getJSONObject("doc").getString("_id"));
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

    }
    public void uploadLogo(final String id){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Uploadfile.uploadRestaurantLogo(id, new File(logoPath), new ResourceHandler() {
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
    public void uploadFotoLugar(final  String id){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Uploadfile.uploadRestaurantFotoLugar(id, new File(logoPath), new ResourceHandler() {
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
            alertDialog.setTitle("SELECT");
            alertDialog.setMessage("Seleccione el metodo");
            alertDialog.setButton("CAMARA", new DialogInterface.OnClickListener() {
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


    public void onClockFotoLugar(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("SELECT");
        alertDialog.setMessage("Seleccione el metodo");
        alertDialog.setButton("CAMARA", new DialogInterface.OnClickListener() {
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