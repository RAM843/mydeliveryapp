package com.example.mydelivery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mydelivery.Adapters.MenuAdapter;
import com.example.mydelivery.Adapters.OnChange;
import com.example.mydelivery.Adapters.OnSelectItem;
import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;
import com.example.mydelivery.Models.LoadAllImages;
import com.example.mydelivery.Models.Menu;
import com.example.mydelivery.Models.OnSaveModel;
import com.example.mydelivery.Models.Pedido;
import com.example.mydelivery.Models.Restaurante;
import com.example.mydelivery.Utils.CameraPhotoManager;
import com.example.mydelivery.Utils.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class Menus extends AppCompatActivity implements View.OnClickListener {
    private Menu selectMenu =null;
    private Restaurante restaurante = null;
    private String fotoproductoPath=null;
    private int CODE_CAMERA_FP= 1100;
    private int CODE_GALERY_FP=1101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);
        Intent i = getIntent();
        String restJson = i.getStringExtra("restaurantJson");
        try {
            restaurante = new Restaurante(new JSONObject(restJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        loadComponents();

    }
    //admin
    ConstraintLayout vnuevo;
    TextView ftitulo;
    EditText txtnombre,txtprecio,txtdescripcion;
    ImageView imgfotoproducto;

    Button btninsertar, btntomarfoto;
    //boot
    LinearLayout ly;
    TextView titulo;
    GridView lista;
    private void loadComponents() {
        vnuevo = findViewById(R.id.lyt_m_nuevo);
        titulo = findViewById(R.id.txt_m_main_titulo);
        lista = findViewById(R.id.lyt_m_lista);
        ly = findViewById(R.id.lyt_m_linear_head);
        if (Sesion.usuario.isAdmin()){
            titulo.setText("MENUS");
            ftitulo = findViewById(R.id.txt_m_titulo);
            txtnombre = findViewById(R.id.txt_m_nombre);
            txtdescripcion = findViewById(R.id.txt_m_descripcion);
            txtprecio = findViewById(R.id.txt_m_precio);

            imgfotoproducto = findViewById(R.id.img_m_fotoproducto);

            btninsertar = findViewById(R.id.btn_m_insertar);
            btntomarfoto = findViewById(R.id.btn_m_tomarfoto);
            btninsertar.setOnClickListener(this);
            btntomarfoto.setOnClickListener(this);
        }else{
            titulo.setText("HACER PEDIDO");
            ly.removeView(vnuevo);
            vnuevo.setVisibility(View.INVISIBLE);
        }
        loadMenus();
    }

    public void Guardar(){
        if (selectMenu==null){
            guardarNuevo();
        }else{
            guardarExistente();
        }

    }
    public void guardarNuevo(){
        String nombre = txtnombre.getText().toString();
        String descripcion = txtdescripcion.getText().toString();
        double precio = Double.parseDouble(txtprecio.getText().toString());
        if (nombre.length()>0 && descripcion.length()>0 && precio>0 && fotoproductoPath!=null){
            Menu nuevo = new Menu(restaurante.id,nombre,descripcion,precio,new File(fotoproductoPath));
            try {
                nuevo.save(new OnSaveModel() {
                    @Override
                    public void onSave(Object o) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Menu Registrado!!!",Toast.LENGTH_LONG).show();
                                //recargar lista
                                limpiarFormulario();
                                loadMenus();
                            }
                        });
                    }

                    @Override
                    public void onFailed(final String err) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),err,Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
    public void guardarExistente(){
        selectMenu.nombre = txtnombre.getText().toString();
        selectMenu.precio = Double.parseDouble(txtprecio.getText().toString());
        selectMenu.descripcion = txtdescripcion.getText().toString();
        if (fotoproductoPath!=null){
            selectMenu.nueva_imagen = new File(fotoproductoPath);
        }
        try {
            selectMenu.save(new OnSaveModel() {
                @Override
                public void onSave(Object o) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Menu Guuardado exitosamente",Toast.LENGTH_SHORT).show();
                        }
                    });
                    limpiarFormulario();
                    loadMenus();

                }
                @Override
                public void onFailed(String err) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"error inesperado!",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void limpiarFormulario(){
        selectMenu = null;
        fotoproductoPath = null;
        //titulos
        ftitulo.setText("Crear Menu");
        btninsertar.setText("Insertar");

        //contenido
        txtnombre.setText("");
        txtprecio.setText("");
        txtdescripcion.setText("");
        imgfotoproducto.setImageBitmap(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_m_insertar:
                Guardar();
                break;
            case R.id.btn_m_tomarfoto:
                onClickFotoProducto();
                break;
        }
    }
    public void onClickFotoProducto(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("SELECCIONAR");
        alertDialog.setMessage("Seleccione el metodo");
        alertDialog.setButton("CAMARA",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),CODE_CAMERA_FP);
            }
        });
        alertDialog.setButton2("GALERIA",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/");
                startActivityForResult(Intent.createChooser(intent,"Seleccione la Aplicacion"),CODE_GALERY_FP);
            }
        });
        alertDialog.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            if(requestCode==CODE_CAMERA_FP){
                Bitmap imgbitmap=(Bitmap)data.getExtras().get("data");
                fotoproductoPath= CameraPhotoManager.saveToInternalStorage(this,imgbitmap);
                imgfotoproducto.setImageBitmap(BitmapFactory.decodeFile(fotoproductoPath));
            }
            if(requestCode==CODE_GALERY_FP){
                Uri uri=data.getData();
                fotoproductoPath=CameraPhotoManager.getRealPathFromURI(this,uri);
                imgfotoproducto.setImageBitmap(BitmapFactory.decodeFile(fotoproductoPath));
            }

        }
    }
    private void loadMenus(){
        Resource rm = new Resource("menu");
        Query q = new Query();
        q.add("id_restaurante",restaurante.id);
        rm.get(q ,new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                try {
                    final JSONArray list = result.getJSONArray("data");
                    final ArrayList<Menu> al = new ArrayList<>();
                    final int cant = list.length();
                    for (int i = 0;i<list.length();i++){
                        Menu c = new Menu(list.getJSONObject(i), new LoadAllImages() {
                            @Override
                            public void finishLoadImages(Object o) {
                                al.add((Menu)o);
                                if (al.size()==cant){
                                    lista.setAdapter(new MenuAdapter(getApplicationContext(), al, new OnChange() {
                                        @Override
                                        public void onChange(BaseAdapter ba) {
                                            lista.setAdapter(ba);
                                        }
                                    }, new OnSelectItem() {
                                        @Override
                                        public void onSelect(Object item) {
                                            selectMenu = (Menu)item;
                                            if(Sesion.usuario.isAdmin()){
                                                //editar
                                                editarMenu();
                                            }else {
                                                //hacer pedido
                                                seleccionarMenuParaPedido();
                                            }
                                        }
                                    }));
                                }

                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(final JSONObject error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(getApplicationContext(),error.getString("msn"),Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
        });
    }
    public void editarMenu(){
        txtnombre.setText(selectMenu.nombre);
        txtprecio.setText(selectMenu.precio+"");
        txtdescripcion.setText(selectMenu.descripcion);
        imgfotoproducto.setImageBitmap(selectMenu.img_fotografiaProducto);
        //cambiar titulo
        ftitulo.setText("Editar Menu");
        btninsertar.setText("Guardar");
    }
    private int Cantidad;
    private AlertDialog al;
    private TextView cantidad;
    public void seleccionarMenuParaPedido(){
        AlertDialog.Builder alb = new AlertDialog.Builder(this);
        LayoutInflater inflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.select_cantidad,null);
        Cantidad = 1;
        TextView nombre = view.findViewById(R.id.txt_sc_nombre);
        nombre.setText(selectMenu.nombre);
        TextView descripcion = view.findViewById(R.id.txt_sc_descripcion);
        descripcion.setText(selectMenu.descripcion);
        descripcion.setEnabled(false);

        cantidad = view.findViewById(R.id.txt_sc_cantidad);
        cantidad.setText(Cantidad+"");
        final TextView precioTotal = view.findViewById(R.id.txt_sc_dtotal);
        precioTotal.setText(""+selectMenu.precio+" * "+Cantidad+" = "+(selectMenu.precio*Cantidad)+" Bs.");

        SeekBar sbcant = view.findViewById(R.id.sb_sc_cantidad);
        sbcant.setMax(10);
        sbcant.setProgress(Cantidad);

        sbcant.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                Cantidad = i;
                cantidad.setText(Cantidad+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Button btnordenar = view.findViewById(R.id.btn_sc_ordenar);
        btnordenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sesion.carrito.add(new Pedido(Cantidad,selectMenu));
                if(al!=null){
                    al.dismiss();
                }
            }
        });
        alb.setView(view);
        al = alb.create();
        al.show();

    }

}