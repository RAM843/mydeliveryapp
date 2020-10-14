package com.example.mydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import android.widget.ImageButton;

public class administrador extends AppCompatActivity {
    private Button btnRes;
    private Button btnMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);

        loadComponents();


    }
    //admin
    private Button btnCrearRestaurant;

    //usuario
    private ImageButton btnPedidos;
    //general
    private GridLayout lista;

    private void loadComponents() {
        btnRes=(Button) findViewById(R.id.btnRes);
        btnRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(administrador.this,restaurant.class);
                startActivity(intent);
            }
        });

        btnPedidos = findViewById(R.id.btn_rh_pedidos);
        lista =(GridLayout) findViewById(R.id.list_rh_view);

        ShowComponents();
    }
    private void ShowComponents(){
        if (Sesion.usuario.isAdmin()){
            btnPedidos.setVisibility(View.INVISIBLE);
        }else{
            btnCrearRestaurant.setVisibility(View.INVISIBLE);

        }
    }
}