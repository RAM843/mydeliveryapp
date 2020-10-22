package com.example.mydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;

import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mydelivery.Adapters.OnChange;
import com.example.mydelivery.Adapters.RestaurantAdapter;
import com.example.mydelivery.Api.Resource;
import com.example.mydelivery.Api.ResourceHandler;
import com.example.mydelivery.Models.LoadAllImages;
import com.example.mydelivery.Models.Restaurante;
import com.example.mydelivery.Utils.OnLoadImg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class administrador extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        loadComponents();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getRestaurantes();
    }

    //admin
    private Button btnCrearRestaurante;

    //user
    private ImageButton btnPedidos;
    //general
    private GridView lista;

    private void loadComponents() {
        btnCrearRestaurante=(Button) findViewById(R.id.btnRes);
        btnCrearRestaurante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(administrador.this,restaurant.class);
                startActivity(intent);
            }
        });

        btnPedidos = findViewById(R.id.btn_rh_pedidos);
        btnPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAPedidos();
            }
        });

        lista =(GridView) findViewById(R.id.list_rh_view);

        ShowComponents();
    }
    public void irAPedidos(){
        Intent i = new Intent(this,RealizarPedido.class);
        startActivity(i);
    }

    public void ShowComponents(){
        if(Sesion.usuario.isAdmin()){
            btnPedidos.setVisibility(View.INVISIBLE);
        }else{
            btnCrearRestaurante.setVisibility(View.INVISIBLE);
        }
    }
    public void getRestaurantes(){
        Resource rr = new Resource("restaurant");
        rr.get(new ResourceHandler() {
            @Override
            public void onSucces(JSONObject result) {
                try {
                    JSONArray list = result.getJSONArray("data");
                    final ArrayList<Restaurante> alr = new ArrayList<>();
                    final int cant =list.length();
                    for(int i=0;i<list.length();i++){
                        Restaurante r = new Restaurante(list.getJSONObject(i), new LoadAllImages() {
                            @Override
                            public void finishLoadImages(Object o) {
                                alr.add((Restaurante)o);
                                if(alr.size()==cant)
                                    lista.setAdapter(new RestaurantAdapter(administrador.this, alr, new OnChange() {
                                        @Override
                                        public void onChange(BaseAdapter ba) {
                                            lista.setAdapter(ba);
                                        }
                                    }));
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(JSONObject error) {
                try {
                    Toast.makeText(getApplicationContext(),error.getString("msn"),Toast.LENGTH_LONG);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}